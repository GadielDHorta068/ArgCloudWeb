package com.argcloud.vm.service;

import com.argcloud.vm.dto.PaymentRequest;
import com.argcloud.vm.dto.PaymentResponse;
import com.argcloud.vm.entity.HardwarePlan;
import com.argcloud.vm.entity.Payment;
import com.argcloud.vm.entity.User;
import com.argcloud.vm.entity.UserSubscription;
import com.argcloud.vm.repository.HardwarePlanRepository;
import com.argcloud.vm.repository.PaymentRepository;
import com.argcloud.vm.repository.UserSubscriptionRepository;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Servicio para manejar pagos con MercadoPago usando la SDK oficial.
 * Implementa la integración según la documentación oficial de MercadoPago.
 * https://www.mercadopago.com.ar/developers/es/docs/checkout-api/integration-configuration/card/web-integration
 */
@Service
@Transactional
public class MercadoPagoService {

    private static final Logger logger = LoggerFactory.getLogger(MercadoPagoService.class);

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Value("${mercadopago.public-key}")
    private String publicKey;

    @Value("${mercadopago.environment:sandbox}")
    private String environment;

    private final PaymentRepository paymentRepository;
    private final HardwarePlanRepository hardwarePlanRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;

    public MercadoPagoService(PaymentRepository paymentRepository,
                             HardwarePlanRepository hardwarePlanRepository,
                             UserSubscriptionRepository userSubscriptionRepository) {
        this.paymentRepository = paymentRepository;
        this.hardwarePlanRepository = hardwarePlanRepository;
        this.userSubscriptionRepository = userSubscriptionRepository;
    }

    /**
     * Inicializa la configuración de MercadoPago.
     * Debe llamarse antes de usar cualquier funcionalidad de la SDK.
     */
    private void initializeMercadoPago() {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new IllegalStateException("Access token de MercadoPago no configurado");
        }
        MercadoPagoConfig.setAccessToken(accessToken);
        logger.info("MercadoPago configurado para entorno: {}", environment);
    }

    /**
     * Obtiene la clave pública de MercadoPago para el frontend.
     * @return clave pública configurada
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * Crea un pago con tarjeta usando el token generado por CardForm.
     * Sigue exactamente la documentación oficial de MercadoPago.
     * 
     * @param token Token de la tarjeta generado por CardForm
     * @param transactionAmount Monto de la transacción
     * @param description Descripción del pago
     * @param installments Número de cuotas
     * @param paymentMethodId ID del método de pago (visa, mastercard, etc.)
     * @param issuerId ID del emisor de la tarjeta
     * @param payerEmail Email del pagador
     * @param identificationType Tipo de identificación
     * @param identificationNumber Número de identificación
     * @return Pago creado en MercadoPago
     * @throws MPException si hay error en la creación del pago
     */
    public com.mercadopago.resources.payment.Payment createCardPayment(String token, BigDecimal transactionAmount, String description,
                                     Integer installments, String paymentMethodId, String issuerId,
                                     String payerEmail, String identificationType, String identificationNumber) 
                                     throws MPException, MPApiException {
        
        initializeMercadoPago();
        
        PaymentClient client = new PaymentClient();
        
        // Crear el request de pago según la documentación oficial
        PaymentCreateRequest createRequest = PaymentCreateRequest.builder()
            .transactionAmount(transactionAmount)
            .token(token)
            .description(description)
            .installments(installments)
            .paymentMethodId(paymentMethodId)
            .issuerId(issuerId)
            .payer(PaymentPayerRequest.builder()
                .email(payerEmail)
                .identification(IdentificationRequest.builder()
                    .type(identificationType)
                    .number(identificationNumber)
                    .build())
                .build())
            .build();
        
        // Crear headers de idempotencia para evitar pagos duplicados
        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put("x-idempotency-key", UUID.randomUUID().toString());
        
        MPRequestOptions requestOptions = MPRequestOptions.builder()
            .customHeaders(customHeaders)
            .build();
        
        logger.info("Creando pago con MercadoPago - Monto: {}, Email: {}", transactionAmount, payerEmail);
        
        try {
            com.mercadopago.resources.payment.Payment payment = client.create(createRequest, requestOptions);
            logger.info("Pago creado exitosamente - ID: {}, Estado: {}", payment.getId(), payment.getStatus());
            return payment;
        } catch (MPApiException e) {
            logger.error("Error de API de MercadoPago: {}", e.getMessage(), e);
            throw e;
        } catch (MPException e) {
            logger.error("Error general de MercadoPago: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Obtiene un pago por su ID de MercadoPago.
     * @param paymentId ID del pago en MercadoPago
     * @return Pago obtenido
     * @throws MPException si hay error al obtener el pago
     */
    public com.mercadopago.resources.payment.Payment getPayment(Long paymentId) throws MPException, MPApiException {
        initializeMercadoPago();
        
        PaymentClient client = new PaymentClient();
        return client.get(paymentId);
    }

    /**
     * Procesa un pago completo: crea la suscripción y registra el pago.
     * @param user Usuario que realiza el pago
     * @param paymentRequest Datos del pago
     * @param token Token de la tarjeta
     * @param paymentMethodId ID del método de pago
     * @param issuerId ID del emisor
     * @param installments Número de cuotas
     * @param identificationType Tipo de identificación
     * @param identificationNumber Número de identificación
     * @return Respuesta del pago procesado
     */
    public PaymentResponse processPayment(User user, PaymentRequest paymentRequest, String token,
                                        String paymentMethodId, String issuerId, Integer installments,
                                        String identificationType, String identificationNumber) {
        try {
            // Obtener el plan de hardware
            HardwarePlan plan = hardwarePlanRepository.findById(paymentRequest.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Plan no encontrado"));
            
            // Calcular el monto según el tipo de suscripción
            BigDecimal amount = "yearly".equals(paymentRequest.getSubscriptionType()) 
                ? plan.getYearlyPrice() : plan.getMonthlyPrice();
            
            String description = String.format("Suscripción %s - Plan %s - ArgCloud", 
                paymentRequest.getSubscriptionType().equals("yearly") ? "Anual" : "Mensual",
                plan.getName());
            
            // Crear el pago en MercadoPago
            com.mercadopago.resources.payment.Payment mpPayment = createCardPayment(
                token, amount, description, installments, paymentMethodId, issuerId,
                paymentRequest.getEmail(), identificationType, identificationNumber
            );
            
            // Crear la suscripción
            UserSubscription subscription = createSubscription(user, plan, paymentRequest.getSubscriptionType());
            
            // Registrar el pago en la base de datos
            Payment payment = createPaymentRecord(user, subscription, mpPayment, amount, token);
            
            // Procesar según el estado del pago
            if ("approved".equals(mpPayment.getStatus())) {
                subscription.setStatus(UserSubscription.SubscriptionStatus.ACTIVE);
                subscription.setStartDate(LocalDateTime.now());
                subscription.setNextBillingDate(calculateNextBillingDate(paymentRequest.getSubscriptionType()));
                userSubscriptionRepository.save(subscription);
                
                payment.setStatus(Payment.PaymentStatus.APPROVED);
                payment.setDateApproved(LocalDateTime.ofInstant(
                    mpPayment.getDateApproved().toInstant(), ZoneId.systemDefault()));
                payment.markAsProcessed();
                paymentRepository.save(payment);
                
                logger.info("Pago aprobado y suscripción activada - Usuario: {}, Plan: {}", 
                    user.getEmail(), plan.getName());
            }
            
            return new PaymentResponse(
                null, // No usamos checkoutUrl en CardForm
                mpPayment.getId().toString(),
                token,
                mpPayment.getStatus(),
                subscription.getId()
            );
            
        } catch (Exception e) {
            logger.error("Error procesando pago para usuario {}: {}", user.getEmail(), e.getMessage(), e);
            return new PaymentResponse(null, null, null, "error", null);
        }
    }

    /**
     * Crea una nueva suscripción para el usuario.
     */
    private UserSubscription createSubscription(User user, HardwarePlan plan, String subscriptionType) {
        UserSubscription subscription = new UserSubscription();
        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setSubscriptionType(
            "yearly".equals(subscriptionType) ? 
                UserSubscription.SubscriptionType.YEARLY : 
                UserSubscription.SubscriptionType.MONTHLY
        );
        subscription.setStatus(UserSubscription.SubscriptionStatus.PENDING);
        subscription.setCreatedAt(LocalDateTime.now());
        
        // ✅ CORRECCIÓN: Establecer fechas obligatorias
        LocalDateTime now = LocalDateTime.now();
        subscription.setStartDate(now); // Fecha de inicio inmediata
        
        // Calcular fecha de finalización según el tipo de suscripción
        LocalDateTime endDate = "yearly".equals(subscriptionType) ? 
            now.plusYears(1) : now.plusMonths(1);
        subscription.setEndDate(endDate);
        
        // Establecer próxima fecha de facturación
        subscription.setNextBillingDate(endDate);
        
        return userSubscriptionRepository.save(subscription);
    }

    /**
     * Registra el pago en la base de datos local.
     */
    private Payment createPaymentRecord(User user, UserSubscription subscription, com.mercadopago.resources.payment.Payment mpPayment, BigDecimal amount, String token) {
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setSubscription(subscription);
        payment.setMercadoPagoPaymentId(mpPayment.getId().toString());
        payment.setAmount(amount);
        payment.setCurrency("ARS");
        payment.setStatus(Payment.PaymentStatus.valueOf(mpPayment.getStatus().toUpperCase()));
        payment.setPaymentMethod(Payment.PaymentMethod.CREDIT_CARD);
        payment.setPaymentMethodId(mpPayment.getPaymentMethodId());
        payment.setInstallments(mpPayment.getInstallments());
        payment.setTransactionToken(token); // Token pasado como parámetro
        payment.setStatusDetail(mpPayment.getStatusDetail());
        payment.setDescription(mpPayment.getDescription());
        payment.setDateCreated(LocalDateTime.ofInstant(
            mpPayment.getDateCreated().toInstant(), ZoneId.systemDefault()));
        
        if (mpPayment.getPayer() != null) {
            payment.setPayerEmail(mpPayment.getPayer().getEmail());
            if (mpPayment.getPayer().getIdentification() != null) {
                payment.setPayerIdentificationType(mpPayment.getPayer().getIdentification().getType());
                payment.setPayerIdentificationNumber(mpPayment.getPayer().getIdentification().getNumber());
            }
        }
        
        if (mpPayment.getCard() != null) {
            payment.setCardLastFourDigits(mpPayment.getCard().getLastFourDigits());
            payment.setCardHolderName(mpPayment.getCard().getCardholder() != null ? 
                mpPayment.getCard().getCardholder().getName() : null);
        }
        
        payment.setIssuerId(mpPayment.getIssuerId());
        
        return paymentRepository.save(payment);
    }

    /**
     * Calcula la próxima fecha de facturación.
     */
    private LocalDateTime calculateNextBillingDate(String subscriptionType) {
        LocalDateTime now = LocalDateTime.now();
        return "yearly".equals(subscriptionType) ? 
            now.plusYears(1) : now.plusMonths(1);
    }

    /**
     * Maneja las notificaciones webhook de MercadoPago.
     * @param paymentId ID del pago notificado
     * @param action Acción de la notificación
     */
    public void handleWebhookNotification(Long paymentId, String action) {
        try {
            logger.info("Procesando notificación webhook - Payment ID: {}, Action: {}", paymentId, action);
            
            // Obtener el pago de MercadoPago
            com.mercadopago.resources.payment.Payment mpPayment = getPayment(paymentId);
            
            // Buscar el pago en nuestra base de datos
            Payment localPayment = paymentRepository.findByMercadoPagoPaymentId(paymentId.toString())
                .orElse(null);
            
            if (localPayment != null) {
                // Actualizar el estado del pago local
                localPayment.setStatus(Payment.PaymentStatus.valueOf(mpPayment.getStatus().toUpperCase()));
                localPayment.setStatusDetail(mpPayment.getStatusDetail());
                localPayment.setDateLastUpdated(LocalDateTime.now());
                
                if ("approved".equals(mpPayment.getStatus()) && !localPayment.isApproved()) {
                    localPayment.setDateApproved(LocalDateTime.ofInstant(
                        mpPayment.getDateApproved().toInstant(), ZoneId.systemDefault()));
                    localPayment.markAsProcessed();
                    
                    // Activar la suscripción si el pago fue aprobado
                    UserSubscription subscription = localPayment.getSubscription();
                    if (subscription.getStatus() == UserSubscription.SubscriptionStatus.PENDING) {
                        subscription.setStatus(UserSubscription.SubscriptionStatus.ACTIVE);
                        subscription.setStartDate(LocalDateTime.now());
                        userSubscriptionRepository.save(subscription);
                    }
                }
                
                paymentRepository.save(localPayment);
                logger.info("Pago actualizado por webhook - ID: {}, Nuevo estado: {}", 
                    paymentId, mpPayment.getStatus());
            }
            
        } catch (Exception e) {
            logger.error("Error procesando webhook para payment ID {}: {}", paymentId, e.getMessage(), e);
        }
    }
} 