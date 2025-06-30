package com.argcloud.vm.controller;

import com.argcloud.vm.dto.*;
import com.argcloud.vm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Controlador para gestionar la autenticación de usuarios,
 * incluyendo el inicio de sesión, registro y verificación de correo electrónico.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * Autentica a un usuario y devuelve un token JWT si las credenciales son correctas.
     * @param loginRequest los datos de inicio de sesión.
     * @return una respuesta con el token JWT o un mensaje de error.
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = userService.authenticateUser(loginRequest);
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * @param registerRequest los datos del nuevo usuario.
     * @return una respuesta con un mensaje de éxito o de error.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            String message = userService.registerUser(registerRequest);
            return ResponseEntity.ok(new MessageResponse(message));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Verifica la dirección de correo electrónico de un usuario a través de un token.
     * @param token el token de verificación enviado al correo del usuario.
     * @return una respuesta con un mensaje de éxito o de error.
     */
    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        try {
            String message = userService.verifyEmail(token);
            return ResponseEntity.ok(new MessageResponse(message));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
} 