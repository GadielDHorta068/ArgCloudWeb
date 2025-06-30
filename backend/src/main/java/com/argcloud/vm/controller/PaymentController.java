package com.argcloud.vm.controller;

import com.argcloud.vm.dto.PaymentRequest;
import com.argcloud.vm.dto.PaymentResponse;
import com.argcloud.vm.entity.User;
import com.argcloud.vm.service.MercadoPagoService;
import com.argcloud.vm.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para manejar operaciones relacionadas con pagos.
 * Integra con MercadoPago para procesar pagos y manejar webhooks.
 */
@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final MercadoPagoService mercadoPagoService;
    private final UserService userService;

    public PaymentController(MercadoPagoService mercadoPagoService, UserService userService) {
        this.mercadoPagoService = mercadoPagoService;
        this.userService = userService;
    }

    /**
     * Obtiene la clave pública de MercadoPago para el frontend.
     * Endpoint público necesario para inicializar CardForm.
     * 
     * @return clave pública de MercadoPago
     */
    @GetMapping("/public-key")
    public ResponseEntity<Map<String, String>> getPublicKey() {
        try {
            String publicKey = mercadoPagoService.getPublicKey();
            return ResponseEntity.ok(Map.of("public_key", publicKey));
        } catch (Exception e) {
            logger.error("Error obteniendo clave pública de MercadoPago: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error obteniendo configuración de pagos"));
        }
    }

    /**
     * Crea un pago usando el token generado por CardForm.
     * Este endpoint sigue exactamente la documentación oficial de MercadoPago.
     * 
     * @param createPaymentData datos del pago incluyendo el token de la tarjeta
     * @param authentication autenticación de Spring Security
     * @return respuesta del pago creado
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@Valid @RequestBody CreatePaymentData createPaymentData, 
                                          Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                logger.error("❌ Usuario no autenticado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario no autenticado"));
            }

            String email = authentication.getName();

            if (createPaymentData == null || createPaymentData.getPaymentRequest() == null) {
                logger.error("❌ Datos de pago nulos o incompletos recibidos");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Datos de pago requeridos"));
            }

            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));

            Long planId = createPaymentData.getPaymentRequest().getPlanId();
            logger.info("📋 Procesando pago para usuario: {} - Plan ID: {}", 
                user.getEmail(), planId != null ? planId : "No especificado");

            if (planId == null || createPaymentData.getToken() == null || createPaymentData.getToken().trim().isEmpty()) {
                logger.error("❌ Faltan datos clave en la solicitud de pago (planId o token)");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Faltan datos clave en la solicitud de pago"));
            }

            PaymentResponse response = mercadoPagoService.processPayment(
                user,
                createPaymentData.getPaymentRequest(),
                createPaymentData.getToken(),
                createPaymentData.getPaymentMethodId(),
                createPaymentData.getIssuerId(),
                createPaymentData.getInstallments(),
                createPaymentData.getIdentificationType(),
                createPaymentData.getIdentificationNumber()
            );

            if ("error".equals(response.getStatus())) {
                logger.error("❌ Error procesando pago con MercadoPago");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Error procesando el pago"));
            }

            logger.info("✅ Pago procesado exitosamente - ID: {}, Estado: {}", 
                response.getPaymentId(), response.getStatus());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("💥 Error crítico creando pago: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor", "details", e.getMessage()));
        }
    }

    /**
     * Verifica el estado de un pago específico.
     * 
     * @param paymentId ID del pago en MercadoPago
     * @param authentication autenticación de Spring Security
     * @return estado del pago
     */
    @GetMapping("/{paymentId}/status")
    public ResponseEntity<?> checkPaymentStatus(@PathVariable String paymentId, Authentication authentication) {
        try {
            // Validar autenticación usando Spring Security Context
            if (authentication == null || !authentication.isAuthenticated()) {
                logger.error("❌ Usuario no autenticado al verificar estado de pago");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario no autenticado"));
            }

            logger.info("👤 Usuario autenticado verificando pago {}: {}", paymentId, authentication.getName());

            // Obtener el pago de MercadoPago
            com.mercadopago.resources.payment.Payment mpPayment = 
                mercadoPagoService.getPayment(Long.parseLong(paymentId));

            return ResponseEntity.ok(Map.of(
                "status", mpPayment.getStatus(),
                "status_detail", mpPayment.getStatusDetail(),
                "payment_id", mpPayment.getId()
            ));

        } catch (Exception e) {
            logger.error("Error verificando estado del pago {}: {}", paymentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error verificando estado del pago"));
        }
    }

    /**
     * Maneja las notificaciones webhook de MercadoPago.
     * Este endpoint es llamado por MercadoPago cuando cambia el estado de un pago.
     * 
     * @param webhookData datos de la notificación
     * @return confirmación de recepción
     */
    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(@RequestBody Map<String, Object> webhookData) {
        try {
            logger.info("Recibida notificación webhook de MercadoPago: {}", webhookData);

            // Extraer información de la notificación
            String action = (String) webhookData.get("action");
            Object dataObj = webhookData.get("data");
            
            if (dataObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) dataObj;
                Object idObj = data.get("id");
                
                if (idObj != null) {
                    Long paymentId = Long.parseLong(idObj.toString());
                    
                    // Procesar la notificación
                    mercadoPagoService.handleWebhookNotification(paymentId, action);
                }
            }

            return ResponseEntity.ok(Map.of("status", "ok"));

        } catch (Exception e) {
            logger.error("Error procesando webhook de MercadoPago: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error procesando notificación"));
        }
    }

    /**
     * Clase interna para recibir los datos de creación de pago.
     * Incluye tanto los datos del PaymentRequest como los datos adicionales del CardForm.
     */
    public static class CreatePaymentData {
        @Valid
        @NotNull(message = "La información de solicitud de pago es requerida")
        private PaymentRequest paymentRequest;
        
        @NotBlank(message = "El token de la tarjeta es requerido")
        private String token; // Token generado por CardForm
        
        @NotBlank(message = "El método de pago es requerido")
        private String paymentMethodId; // visa, mastercard, etc.
        
        private String issuerId; // ID del emisor de la tarjeta (opcional en algunos casos)
        
        @NotNull(message = "El número de cuotas es requerido")
        @Min(value = 1, message = "El número de cuotas debe ser mayor a 0")
        private Integer installments; // Número de cuotas
        
        @NotBlank(message = "El tipo de identificación es requerido")
        private String identificationType; // Tipo de identificación
        
        @NotBlank(message = "El número de identificación es requerido")
        private String identificationNumber; // Número de identificación

        // Constructores
        public CreatePaymentData() {}

        // Getters y setters
        public PaymentRequest getPaymentRequest() {
            return paymentRequest;
        }

        public void setPaymentRequest(PaymentRequest paymentRequest) {
            this.paymentRequest = paymentRequest;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getPaymentMethodId() {
            return paymentMethodId;
        }

        public void setPaymentMethodId(String paymentMethodId) {
            this.paymentMethodId = paymentMethodId;
        }

        public String getIssuerId() {
            return issuerId;
        }

        public void setIssuerId(String issuerId) {
            this.issuerId = issuerId;
        }

        public Integer getInstallments() {
            return installments;
        }

        public void setInstallments(Integer installments) {
            this.installments = installments;
        }

        public String getIdentificationType() {
            return identificationType;
        }

        public void setIdentificationType(String identificationType) {
            this.identificationType = identificationType;
        }

        public String getIdentificationNumber() {
            return identificationNumber;
        }

        public void setIdentificationNumber(String identificationNumber) {
            this.identificationNumber = identificationNumber;
        }
    }
} 