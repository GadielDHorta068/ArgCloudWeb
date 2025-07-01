package com.argcloud.vm.service;

import com.argcloud.vm.dto.AuthResponse;
import com.argcloud.vm.dto.LoginRequest;
import com.argcloud.vm.dto.RegisterRequest;
import com.argcloud.vm.dto.ResetPasswordRequest;
import com.argcloud.vm.entity.User;
import com.argcloud.vm.repository.UserRepository;
import com.argcloud.vm.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para la lógica de negocio relacionada con los usuarios.
 * Incluye registro, autenticación y verificación de email.
 */
@Service  
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EmailService emailService;

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param registerRequest los datos del usuario a registrar.
     * @return un mensaje indicando el resultado de la operación.
     * @throws RuntimeException si el email ya está registrado.
     */
    public String registerUser(RegisterRequest registerRequest) {
        // Verificar si el email ya existe
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Crear nuevo usuario
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmailVerified(false);

        // Generar token de verificación
        String verificationToken = UUID.randomUUID().toString();
        user.setEmailVerificationToken(verificationToken);
        user.setEmailVerificationExpiresAt(LocalDateTime.now().plusHours(24));

        userRepository.save(user);

        // Enviar email de verificación
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);

        return "Usuario registrado exitosamente. Por favor, verifica tu email.";
    }

    /**
     * Autentica a un usuario.
     *
     * @param loginRequest las credenciales de inicio de sesión.
     * @return una respuesta de autenticación con el token JWT.
     * @throws RuntimeException si las credenciales son inválidas o el email no ha sido verificado.
     */
    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Credenciales inválidas");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        if (!user.getEmailVerified()) {
            throw new RuntimeException("Por favor, verifica tu email antes de iniciar sesión");
        }

        String jwt = jwtUtils.generateJwtToken(user.getEmail());

        return new AuthResponse(
                jwt,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmailVerified()
        );
    }

    /**
     * Verifica el email de un usuario utilizando un token de verificación.
     *
     * @param token el token de verificación.
     * @return un mensaje indicando el resultado de la operación.
     * @throws RuntimeException si el token es inválido o ha expirado.
     */
    public String verifyEmail(String token) {
        Optional<User> userOptional = userRepository.findByEmailVerificationToken(token);
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Token de verificación inválido");
        }

        User user = userOptional.get();

        if (user.getEmailVerificationExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El token de verificación ha expirado");
        }

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationExpiresAt(null);

        userRepository.save(user);

        return "Email verificado exitosamente";
    }

    /**
     * Inicia el proceso de restablecimiento de contraseña para un usuario.
     *
     * @param email el email del usuario que solicita el restablecimiento.
     * @return un mensaje indicando que se ha enviado un correo si el usuario existe.
     */
    public String forgotPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            String token = UUID.randomUUID().toString();
            user.setPasswordResetToken(token);
            user.setPasswordResetExpiresAt(LocalDateTime.now().plusHours(1)); // 1 hora de validez

            userRepository.save(user);

            emailService.sendPasswordResetEmail(user.getEmail(), token);
        }

        // Por seguridad, no revelamos si el email fue encontrado o no.
        return "Si tu dirección de email está en nuestra base de datos, recibirás un correo con las instrucciones para restablecer tu contraseña.";
    }

    /**
     * Restablece la contraseña de un usuario utilizando un token.
     *
     * @param request la solicitud con el token y la nueva contraseña.
     * @return un mensaje indicando el resultado.
     * @throws RuntimeException si el token es inválido o ha expirado.
     */
    public String resetPassword(ResetPasswordRequest request) {
        Optional<User> userOptional = userRepository.findByPasswordResetToken(request.getToken());

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Token de restablecimiento inválido");
        }

        User user = userOptional.get();

        if (user.getPasswordResetExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El token de restablecimiento ha expirado");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpiresAt(null);

        userRepository.save(user);

        return "Tu contraseña ha sido actualizada exitosamente.";
    }

    /**
     * Busca un usuario por su dirección de correo electrónico.
     *
     * @param email la dirección de correo electrónico a buscar.
     * @return un Optional que contiene al usuario si se encuentra.
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
} 