package com.argcloud.vm.controller;

import com.argcloud.vm.dto.MessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para las operaciones del dashboard.
 * Requiere autenticación para acceder a sus endpoints.
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DashboardController {

    /**
     * Devuelve un mensaje de bienvenida al usuario autenticado.
     * @return un mensaje de bienvenida.
     */
    @GetMapping("/welcome")
    public ResponseEntity<?> getWelcomeMessage() {
        try {
            // Obtener el usuario autenticado desde el contexto de seguridad
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = "";
            
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }
            
            String message = "¡Bienvenido al dashboard, " + username + "!";
            return ResponseEntity.ok(new MessageResponse(message));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para obtener la lista de máquinas virtuales.
     * Actualmente es un placeholder.
     * @return un mensaje indicando que la funcionalidad está en desarrollo.
     */
    @GetMapping("/vms")
    public ResponseEntity<?> getVirtualMachines() {
        // Por ahora retornamos un mensaje simple
        // En el futuro aquí irá la lógica de máquinas virtuales
        return ResponseEntity.ok(new MessageResponse("Lista de máquinas virtuales - Funcionalidad en desarrollo"));
    }
} 