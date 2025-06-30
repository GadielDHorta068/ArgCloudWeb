package com.argcloud.vm.dto;

/**
 * DTO para enviar mensajes de respuesta genéricos.
 * Utilizado para comunicar el resultado de una operación.
 */
public class MessageResponse {

    private String message;

    // Constructors
    public MessageResponse() {}

    /**
     * Constructor con parámetros.
     * @param message el mensaje a enviar.
     */
    public MessageResponse(String message) {
        this.message = message;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
} 