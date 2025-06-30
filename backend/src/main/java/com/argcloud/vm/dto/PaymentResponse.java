package com.argcloud.vm.dto;

/**
 * DTO de respuesta para pagos.
 * Contiene la información de respuesta de Mercado Pago.
 */
public class PaymentResponse {

    private String checkoutUrl;
    private String paymentId;
    private String transactionToken;
    private String status;
    private Long subscriptionId;
    private String message;

    // Constructores
    public PaymentResponse() {}

    /**
     * Constructor con parámetros principales.
     */
    public PaymentResponse(String checkoutUrl, String paymentId, String transactionToken, 
                          String status, Long subscriptionId) {
        this.checkoutUrl = checkoutUrl;
        this.paymentId = paymentId;
        this.transactionToken = transactionToken;
        this.status = status;
        this.subscriptionId = subscriptionId;
    }

    // Getters y Setters
    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getTransactionToken() {
        return transactionToken;
    }

    public void setTransactionToken(String transactionToken) {
        this.transactionToken = transactionToken;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "PaymentResponse{" +
                "checkoutUrl='" + checkoutUrl + '\'' +
                ", paymentId='" + paymentId + '\'' +
                ", status='" + status + '\'' +
                ", subscriptionId=" + subscriptionId +
                '}';
    }
} 