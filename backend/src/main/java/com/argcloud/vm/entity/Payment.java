package com.argcloud.vm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un pago realizado por un usuario.
 * Registra todas las transacciones de Mercado Pago y su estado.
 */
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private UserSubscription subscription;

    @NotBlank(message = "El ID de pago de Mercado Pago es obligatorio")
    @Column(name = "mercadopago_payment_id", nullable = false, unique = true)
    private String mercadoPagoPaymentId;

    @Column(name = "mercadopago_preference_id")
    private String mercadoPagoPreferenceId;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @NotBlank(message = "La moneda es obligatoria")
    @Column(nullable = false)
    private String currency;

    @NotNull(message = "El estado del pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @NotNull(message = "El método de pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "payment_method_id")
    private String paymentMethodId; // visa, mastercard, etc.

    @Column(name = "installments")
    private Integer installments;

    @Column(name = "transaction_token")
    private String transactionToken;

    @Column(name = "status_detail")
    private String statusDetail;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "external_reference")
    private String externalReference;

    @Column(name = "date_approved")
    private LocalDateTime dateApproved;

    @Column(name = "date_created", nullable = false)
    private LocalDateTime dateCreated;

    @Column(name = "date_last_updated")
    private LocalDateTime dateLastUpdated;

    // Información del pagador
    @Column(name = "payer_email")
    private String payerEmail;

    @Column(name = "payer_identification_type")
    private String payerIdentificationType;

    @Column(name = "payer_identification_number")
    private String payerIdentificationNumber;

    // Información de la tarjeta (si aplica)
    @Column(name = "card_last_four_digits")
    private String cardLastFourDigits;

    @Column(name = "card_holder_name")
    private String cardHolderName;

    @Column(name = "issuer_id")
    private String issuerId;

    // Información de procesamiento interno
    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Enum que define los estados de un pago
     */
    public enum PaymentStatus {
        PENDING("pending"),
        APPROVED("approved"),
        REJECTED("rejected"),
        CANCELLED("cancelled"),
        REFUNDED("refunded"),
        CHARGED_BACK("charged_back");

        private final String value;

        PaymentStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * Enum que define los métodos de pago disponibles
     */
    public enum PaymentMethod {
        CREDIT_CARD("credit_card"),
        DEBIT_CARD("debit_card"),
        BANK_TRANSFER("bank_transfer"),
        DIGITAL_WALLET("digital_wallet"),
        CASH("cash");

        private final String value;

        PaymentMethod(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * Establece las fechas de creación y actualización antes de persistir.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (dateCreated == null) {
            dateCreated = LocalDateTime.now();
        }
    }

    /**
     * Actualiza la fecha de actualización antes de actualizar.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        dateLastUpdated = LocalDateTime.now();
    }

    // Constructores
    public Payment() {}

    /**
     * Constructor principal.
     */
    public Payment(User user, UserSubscription subscription, String mercadoPagoPaymentId,
                  BigDecimal amount, String currency, PaymentStatus status, PaymentMethod paymentMethod) {
        this.user = user;
        this.subscription = subscription;
        this.mercadoPagoPaymentId = mercadoPagoPaymentId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserSubscription getSubscription() {
        return subscription;
    }

    public void setSubscription(UserSubscription subscription) {
        this.subscription = subscription;
    }

    public String getMercadoPagoPaymentId() {
        return mercadoPagoPaymentId;
    }

    public void setMercadoPagoPaymentId(String mercadoPagoPaymentId) {
        this.mercadoPagoPaymentId = mercadoPagoPaymentId;
    }

    public String getMercadoPagoPreferenceId() {
        return mercadoPagoPreferenceId;
    }

    public void setMercadoPagoPreferenceId(String mercadoPagoPreferenceId) {
        this.mercadoPagoPreferenceId = mercadoPagoPreferenceId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public String getTransactionToken() {
        return transactionToken;
    }

    public void setTransactionToken(String transactionToken) {
        this.transactionToken = transactionToken;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public LocalDateTime getDateApproved() {
        return dateApproved;
    }

    public void setDateApproved(LocalDateTime dateApproved) {
        this.dateApproved = dateApproved;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateLastUpdated() {
        return dateLastUpdated;
    }

    public void setDateLastUpdated(LocalDateTime dateLastUpdated) {
        this.dateLastUpdated = dateLastUpdated;
    }

    public String getPayerEmail() {
        return payerEmail;
    }

    public void setPayerEmail(String payerEmail) {
        this.payerEmail = payerEmail;
    }

    public String getPayerIdentificationType() {
        return payerIdentificationType;
    }

    public void setPayerIdentificationType(String payerIdentificationType) {
        this.payerIdentificationType = payerIdentificationType;
    }

    public String getPayerIdentificationNumber() {
        return payerIdentificationNumber;
    }

    public void setPayerIdentificationNumber(String payerIdentificationNumber) {
        this.payerIdentificationNumber = payerIdentificationNumber;
    }

    public String getCardLastFourDigits() {
        return cardLastFourDigits;
    }

    public void setCardLastFourDigits(String cardLastFourDigits) {
        this.cardLastFourDigits = cardLastFourDigits;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getIssuerId() {
        return issuerId;
    }

    public void setIssuerId(String issuerId) {
        this.issuerId = issuerId;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Verifica si el pago está aprobado.
     */
    public boolean isApproved() {
        return status == PaymentStatus.APPROVED;
    }

    /**
     * Verifica si el pago está pendiente.
     */
    public boolean isPending() {
        return status == PaymentStatus.PENDING;
    }

    /**
     * Verifica si el pago fue rechazado.
     */
    public boolean isRejected() {
        return status == PaymentStatus.REJECTED;
    }

    /**
     * Marca el pago como procesado exitosamente.
     */
    public void markAsProcessed() {
        this.processedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", mercadoPagoPaymentId='" + mercadoPagoPaymentId + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", status=" + status +
                ", paymentMethod=" + paymentMethod +
                ", user=" + (user != null ? user.getEmail() : null) +
                '}';
    }
} 