package com.argcloud.vm.controller;

import com.argcloud.vm.dto.MessageResponse;
import com.argcloud.vm.dto.VirtualMachineRequest;
import com.argcloud.vm.dto.VirtualMachineResponse;
import com.argcloud.vm.entity.User;
import com.argcloud.vm.repository.UserRepository;
import com.argcloud.vm.service.VirtualMachineService;
import com.argcloud.vm.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/vms")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VirtualMachineController {
    
    private static final Logger logger = LoggerFactory.getLogger(VirtualMachineController.class);
    
    private final VirtualMachineService vmService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    
    public VirtualMachineController(VirtualMachineService vmService, JwtUtils jwtUtils, UserRepository userRepository) {
        this.vmService = vmService;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }
    
    /**
     * Crea una nueva máquina virtual.
     */
    @PostMapping
    public ResponseEntity<?> createVirtualMachine(@Valid @RequestBody VirtualMachineRequest request,
                                                  HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromToken(httpRequest);
            VirtualMachineResponse response = vmService.createVirtualMachine(request, userId);
            
            logger.info("VM creada por usuario {}: {}", userId, response.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error creando VM: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error creando máquina virtual: " + e.getMessage()));
        }
    }
    
    /**
     * Obtiene todas las máquinas virtuales del usuario.
     */
    @GetMapping
    public ResponseEntity<?> getUserVirtualMachines(HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromToken(httpRequest);
            List<VirtualMachineResponse> vms = vmService.getUserVirtualMachines(userId);
            
            return ResponseEntity.ok(vms);
            
        } catch (Exception e) {
            logger.error("Error obteniendo VMs: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error obteniendo máquinas virtuales: " + e.getMessage()));
        }
    }
    
    /**
     * Obtiene una máquina virtual específica.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getVirtualMachine(@PathVariable Long id,
                                               HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromToken(httpRequest);
            VirtualMachineResponse vm = vmService.getVirtualMachine(id, userId);
            
            return ResponseEntity.ok(vm);
            
        } catch (Exception e) {
            logger.error("Error obteniendo VM {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error obteniendo máquina virtual: " + e.getMessage()));
        }
    }
    
    /**
     * Inicia una máquina virtual.
     */
    @PostMapping("/{id}/start")
    public ResponseEntity<?> startVirtualMachine(@PathVariable Long id,
                                                 HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromToken(httpRequest);
            VirtualMachineResponse vm = vmService.startVirtualMachine(id, userId);
            
            logger.info("VM {} iniciada por usuario {}", id, userId);
            return ResponseEntity.ok(vm);
            
        } catch (Exception e) {
            logger.error("Error iniciando VM {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error iniciando máquina virtual: " + e.getMessage()));
        }
    }
    
    /**
     * Detiene una máquina virtual.
     */
    @PostMapping("/{id}/stop")
    public ResponseEntity<?> stopVirtualMachine(@PathVariable Long id,
                                                HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromToken(httpRequest);
            VirtualMachineResponse vm = vmService.stopVirtualMachine(id, userId);
            
            logger.info("VM {} detenida por usuario {}", id, userId);
            return ResponseEntity.ok(vm);
            
        } catch (Exception e) {
            logger.error("Error deteniendo VM {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error deteniendo máquina virtual: " + e.getMessage()));
        }
    }
    
    /**
     * Reinicia una máquina virtual.
     */
    @PostMapping("/{id}/restart")
    public ResponseEntity<?> restartVirtualMachine(@PathVariable Long id,
                                                   HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromToken(httpRequest);
            VirtualMachineResponse vm = vmService.restartVirtualMachine(id, userId);
            
            logger.info("VM {} reiniciada por usuario {}", id, userId);
            return ResponseEntity.ok(vm);
            
        } catch (Exception e) {
            logger.error("Error reiniciando VM {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error reiniciando máquina virtual: " + e.getMessage()));
        }
    }
    
    /**
     * Elimina una máquina virtual.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVirtualMachine(@PathVariable Long id,
                                                  HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromToken(httpRequest);
            vmService.deleteVirtualMachine(id, userId);
            
            logger.info("VM {} eliminada por usuario {}", id, userId);
            return ResponseEntity.ok(new MessageResponse("Máquina virtual eliminada correctamente"));
            
        } catch (Exception e) {
            logger.error("Error eliminando VM {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error eliminando máquina virtual: " + e.getMessage()));
        }
    }
    
    /**
     * Sincroniza el estado de una máquina virtual.
     */
    @PostMapping("/{id}/sync")
    public ResponseEntity<?> syncVirtualMachine(@PathVariable Long id,
                                                HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromToken(httpRequest);
            VirtualMachineResponse vm = vmService.syncVirtualMachineStatus(id, userId);
            
            logger.info("VM {} sincronizada por usuario {}", id, userId);
            return ResponseEntity.ok(vm);
            
        } catch (Exception e) {
            logger.error("Error sincronizando VM {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error sincronizando máquina virtual: " + e.getMessage()));
        }
    }
    
    /**
     * Obtiene los nodos disponibles.
     */
    @GetMapping("/nodes")
    public ResponseEntity<?> getAvailableNodes(HttpServletRequest httpRequest) {
        try {
            getUserIdFromToken(httpRequest); // Verificar autenticación
            
            Mono<List<String>> nodesMono = vmService.getAvailableNodes();
            List<String> nodes = nodesMono.block(); // Bloquear para obtener resultado síncrono
            
            return ResponseEntity.ok(nodes);
            
        } catch (Exception e) {
            logger.error("Error obteniendo nodos: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error obteniendo nodos disponibles: " + e.getMessage()));
        }
    }
    
    // Método auxiliar para extraer el ID de usuario del token JWT
    private Long getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token de autorización requerido");
        }
        
        String token = authHeader.substring(7);
        if (!jwtUtils.validateJwtToken(token)) {
            throw new RuntimeException("Token inválido");
        }
        
        String email = jwtUtils.getEmailFromJwtToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return user.getId();
    }
} 