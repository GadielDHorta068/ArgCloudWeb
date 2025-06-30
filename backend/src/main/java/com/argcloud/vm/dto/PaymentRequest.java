package com.argcloud.vm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para solicitar un pago.
 * Contiene la información necesaria para crear un pago con Mercado Pago.
 */
public class PaymentRequest {

    @NotNull(message = "El ID del plan es obligatorio")
    private Long planId;

    @NotBlank(message = "El tipo de suscripción es obligatorio")
    private String subscriptionType; // "monthly" o "yearly"

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;

    private String additionalInfo;

    // Constructores
    public PaymentRequest() {}

    /**
     * Constructor con parámetros principales.
     */
    public PaymentRequest(Long planId, String subscriptionType, String email) {
        this.planId = planId;
        this.subscriptionType = subscriptionType;
        this.email = email;
    }

    // Getters y Setters
    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "planId=" + planId +
                ", subscriptionType='" + subscriptionType + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
} 