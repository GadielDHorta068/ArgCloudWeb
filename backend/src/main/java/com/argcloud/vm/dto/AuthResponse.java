package com.argcloud.vm.dto;

/**
 * DTO para la respuesta de autenticación.
 * Contiene el token JWT y la información del usuario autenticado.
 */
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean emailVerified;

    // Constructors
    public AuthResponse() {}

    /**
     * Constructor con parámetros.
     * @param token el token JWT.
     * @param id el ID del usuario.
     * @param email el email del usuario.
     * @param firstName el nombre del usuario.
     * @param lastName el apellido del usuario.
     * @param emailVerified si el email del usuario ha sido verificado.
     */
    public AuthResponse(String token, Long id, String email, String firstName, String lastName, Boolean emailVerified) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailVerified = emailVerified;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
} 