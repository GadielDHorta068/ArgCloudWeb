package com.argcloud.vm.controller;

import com.argcloud.vm.dto.UserSubscriptionResponse;
import com.argcloud.vm.entity.User;
import com.argcloud.vm.entity.UserSubscription;
import com.argcloud.vm.repository.UserSubscriptionRepository;
import com.argcloud.vm.service.UserService;
import com.argcloud.vm.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para manejar suscripciones de usuarios.
 * Proporciona endpoints para gestionar las suscripciones activas y historial.
 */
@RestController
@RequestMapping("/api/subscriptions")
@CrossOrigin(origins = "*")
public class SubscriptionController {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

    private final UserSubscriptionRepository userSubscriptionRepository;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    public SubscriptionController(UserSubscriptionRepository userSubscriptionRepository,
                                 UserService userService,
                                 JwtUtils jwtUtils) {
        this.userSubscriptionRepository = userSubscriptionRepository;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Obtiene la suscripción activa del usuario actual.
     * 
     * @param request request HTTP para extraer el token JWT
     * @return suscripción activa o null si no tiene
     */
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentSubscription(HttpServletRequest request) {
        try {
            // Extraer y validar el token JWT
            String token = extractJwtFromRequest(request);
            if (token == null || !jwtUtils.validateJwtToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token de autenticación inválido"));
            }

            // Obtener el usuario autenticado
            String email = jwtUtils.getEmailFromJwtToken(token);
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Buscar suscripción activa
            Optional<UserSubscription> activeSubscription = userSubscriptionRepository
                .findByUserAndStatus(user, UserSubscription.SubscriptionStatus.ACTIVE);

            if (activeSubscription.isPresent()) {
                UserSubscriptionResponse response = new UserSubscriptionResponse(activeSubscription.get());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.ok(null);
            }

        } catch (Exception e) {
            logger.error("Error obteniendo suscripción actual para usuario: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }

    /**
     * Obtiene el historial de suscripciones del usuario.
     * 
     * @param request request HTTP para autenticación
     * @return lista de todas las suscripciones del usuario
     */
    @GetMapping("/history")
    public ResponseEntity<?> getSubscriptionHistory(HttpServletRequest request) {
        try {
            // Extraer y validar el token JWT
            String token = extractJwtFromRequest(request);
            if (token == null || !jwtUtils.validateJwtToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token de autenticación inválido"));
            }

            // Obtener el usuario autenticado
            String email = jwtUtils.getEmailFromJwtToken(token);
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Obtener todas las suscripciones del usuario
            List<UserSubscription> subscriptions = userSubscriptionRepository.findByUserOrderByCreatedAtDesc(user);
            
            List<UserSubscriptionResponse> subscriptionResponses = subscriptions.stream()
                .map(UserSubscriptionResponse::new)
                .toList();

            return ResponseEntity.ok(subscriptionResponses);

        } catch (Exception e) {
            logger.error("Error obteniendo historial de suscripciones: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }

    /**
     * Cancela una suscripción activa.
     * 
     * @param subscriptionId ID de la suscripción a cancelar
     * @param request request HTTP para autenticación
     * @return confirmación de cancelación
     */
    @PostMapping("/{subscriptionId}/cancel")
    public ResponseEntity<?> cancelSubscription(@PathVariable Long subscriptionId, 
                                               HttpServletRequest request) {
        try {
            // Extraer y validar el token JWT
            String token = extractJwtFromRequest(request);
            if (token == null || !jwtUtils.validateJwtToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token de autenticación inválido"));
            }

            // Obtener el usuario autenticado
            String email = jwtUtils.getEmailFromJwtToken(token);
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Buscar la suscripción
            Optional<UserSubscription> subscription = userSubscriptionRepository.findById(subscriptionId);
            
            if (subscription.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Suscripción no encontrada"));
            }

            UserSubscription userSubscription = subscription.get();

            // Verificar que la suscripción pertenece al usuario
            if (!userSubscription.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "No tiene permisos para cancelar esta suscripción"));
            }

            // Verificar que la suscripción esté activa
            if (userSubscription.getStatus() != UserSubscription.SubscriptionStatus.ACTIVE) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Solo se pueden cancelar suscripciones activas"));
            }

            // Cancelar la suscripción
            userSubscription.setStatus(UserSubscription.SubscriptionStatus.CANCELLED);
            userSubscriptionRepository.save(userSubscription);

            logger.info("Suscripción {} cancelada para usuario {}", subscriptionId, user.getEmail());

            return ResponseEntity.ok(Map.of("message", "Suscripción cancelada exitosamente"));

        } catch (Exception e) {
            logger.error("Error cancelando suscripción {}: {}", subscriptionId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }

    /**
     * Extrae el token JWT del header Authorization.
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 