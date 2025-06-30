package com.argcloud.vm.service;

import com.argcloud.vm.dto.VirtualMachineRequest;
import com.argcloud.vm.dto.VirtualMachineResponse;
import com.argcloud.vm.entity.User;
import com.argcloud.vm.entity.UserSubscription;
import com.argcloud.vm.entity.VirtualMachine;
import com.argcloud.vm.entity.HardwarePlan;
import com.argcloud.vm.repository.VirtualMachineRepository;
import com.argcloud.vm.repository.UserRepository;
import com.argcloud.vm.repository.UserSubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class VirtualMachineService {
    
    private static final Logger logger = LoggerFactory.getLogger(VirtualMachineService.class);
    
    private final VirtualMachineRepository vmRepository;
    private final UserRepository userRepository;
    private final ExternalApiService externalApiService;
    private final UserSubscriptionRepository userSubscriptionRepository;
    
    public VirtualMachineService(VirtualMachineRepository vmRepository,
                               UserRepository userRepository,
                               @Qualifier("proxmoxApiService") ExternalApiService externalApiService,
                               UserSubscriptionRepository userSubscriptionRepository) {
        this.vmRepository = vmRepository;
        this.userRepository = userRepository;
        this.externalApiService = externalApiService;
        this.userSubscriptionRepository = userSubscriptionRepository;
    }
    
    /**
     * Crea una nueva máquina virtual.
     */
    public VirtualMachineResponse createVirtualMachine(VirtualMachineRequest request, Long userId) {
        try {
            // Buscar el usuario
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Verificar suscripción activa y límites de recursos
            UserSubscription subscription = userSubscriptionRepository.findActiveSubscriptionByUserId(userId, LocalDateTime.now())
                    .orElseThrow(() -> new RuntimeException("No tienes una suscripción activa."));

            HardwarePlan plan = subscription.getPlan();

            // Validar límites de recursos
            if (subscription.getUsedCpu() + request.getCpu() > plan.getTotalCpu() ||
                subscription.getUsedMemory() + request.getMemory() > plan.getTotalMemory() ||
                subscription.getUsedDisk() + request.getDisk() > plan.getTotalDisk() ||
                subscription.getCurrentVMs() + 1 > plan.getMaxVMs()) {
                throw new RuntimeException("La creación de la VM excede los límites de tu plan.");
            }
            
            // Verificar que no existe una VM con el mismo nombre para este usuario
            if (vmRepository.existsByNameAndUser(request.getName(), user)) {
                throw new RuntimeException("Ya existe una máquina virtual con ese nombre");
            }
            
            // Actualizar los recursos utilizados en la suscripción
            subscription.setUsedCpu(subscription.getUsedCpu() + request.getCpu());
            subscription.setUsedMemory(subscription.getUsedMemory() + request.getMemory());
            subscription.setUsedDisk(subscription.getUsedDisk() + request.getDisk());
            subscription.setCurrentVMs(subscription.getCurrentVMs() + 1);
            userSubscriptionRepository.save(subscription);
            
            // Crear la entidad en la base de datos
            VirtualMachine vm = new VirtualMachine(
                    request.getName(),
                    VirtualMachine.VMStatus.CREATING,
                    request.getOs(),
                    request.getCpu(),
                    request.getMemory(),
                    request.getDisk(),
                    user
            );
            
            if (request.getNodeName() != null) {
                vm.setNodeName(request.getNodeName());
            }
            
            vm = vmRepository.save(vm);
            logger.info("VM creada en BD con ID: {}", vm.getId());
            
            // Crear la VM en el sistema externo de forma asíncrona
            final VirtualMachine savedVm = vm;
            externalApiService.createVirtualMachine(request)
                    .subscribe(
                            externalId -> {
                                // Actualizar con el ID externo
                                savedVm.setExternalId(externalId);
                                savedVm.setStatus(VirtualMachine.VMStatus.STOPPED);
                                vmRepository.save(savedVm);
                                logger.info("VM actualizada con ID externo: {}", externalId);
                            },
                            error -> {
                                // Marcar como error si falla la creación externa
                                savedVm.setStatus(VirtualMachine.VMStatus.ERROR);
                                vmRepository.save(savedVm);
                                
                                // Liberar recursos de la suscripción
                                userSubscriptionRepository.findActiveSubscriptionByUserId(userId, LocalDateTime.now()).ifPresent(s -> {
                                    s.setUsedCpu(s.getUsedCpu() - request.getCpu());
                                    s.setUsedMemory(s.getUsedMemory() - request.getMemory());
                                    s.setUsedDisk(s.getUsedDisk() - request.getDisk());
                                    s.setCurrentVMs(s.getCurrentVMs() - 1);
                                    userSubscriptionRepository.save(s);
                                });

                                logger.error("Error creando VM externa: {}", error.getMessage());
                            }
                    );
            
            return new VirtualMachineResponse(vm);
            
        } catch (Exception e) {
            logger.error("Error creando máquina virtual: {}", e.getMessage());
            throw new RuntimeException("Error creando máquina virtual: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene todas las máquinas virtuales de un usuario.
     */
    public List<VirtualMachineResponse> getUserVirtualMachines(Long userId) {
        List<VirtualMachine> vms = vmRepository.findByUserId(userId);
        return vms.stream()
                .map(VirtualMachineResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene una máquina virtual por ID.
     */
    public VirtualMachineResponse getVirtualMachine(Long vmId, Long userId) {
        VirtualMachine vm = vmRepository.findById(vmId)
                .orElseThrow(() -> new RuntimeException("Máquina virtual no encontrada"));
        
        // Verificar que pertenece al usuario
        if (!vm.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permisos para acceder a esta máquina virtual");
        }
        
        return new VirtualMachineResponse(vm);
    }
    
    /**
     * Inicia una máquina virtual.
     */
    public VirtualMachineResponse startVirtualMachine(Long vmId, Long userId) {
        VirtualMachine vm = getVmAndValidateOwnership(vmId, userId);
        
        if (vm.getExternalId() == null) {
            throw new RuntimeException("La máquina virtual no tiene ID externo");
        }
        
        // Actualizar estado a reiniciando
        vm.setStatus(VirtualMachine.VMStatus.RESTARTING);
        final VirtualMachine finalVm = vmRepository.save(vm);
        
        // Iniciar en el sistema externo
        externalApiService.startVirtualMachine(finalVm.getExternalId())
                .subscribe(
                        success -> {
                            if (success) {
                                finalVm.setStatus(VirtualMachine.VMStatus.RUNNING);
                                vmRepository.save(finalVm);
                                logger.info("VM {} iniciada correctamente", finalVm.getId());
                            } else {
                                finalVm.setStatus(VirtualMachine.VMStatus.ERROR);
                                vmRepository.save(finalVm);
                                logger.error("Error iniciando VM {}", finalVm.getId());
                            }
                        },
                        error -> {
                            finalVm.setStatus(VirtualMachine.VMStatus.ERROR);
                            vmRepository.save(finalVm);
                            logger.error("Error iniciando VM {}: {}", finalVm.getId(), error.getMessage());
                        }
                );
        
        return new VirtualMachineResponse(finalVm);
    }
    
    /**
     * Detiene una máquina virtual.
     */
    public VirtualMachineResponse stopVirtualMachine(Long vmId, Long userId) {
        VirtualMachine vm = getVmAndValidateOwnership(vmId, userId);
        
        if (vm.getExternalId() == null) {
            throw new RuntimeException("La máquina virtual no tiene ID externo");
        }
        
        // Actualizar estado
        vm.setStatus(VirtualMachine.VMStatus.RESTARTING);
        final VirtualMachine finalVm = vmRepository.save(vm);
        
        // Detener en el sistema externo
        externalApiService.stopVirtualMachine(finalVm.getExternalId())
                .subscribe(
                        success -> {
                            if (success) {
                                finalVm.setStatus(VirtualMachine.VMStatus.STOPPED);
                                vmRepository.save(finalVm);
                                logger.info("VM {} detenida correctamente", finalVm.getId());
                            } else {
                                finalVm.setStatus(VirtualMachine.VMStatus.ERROR);
                                vmRepository.save(finalVm);
                                logger.error("Error deteniendo VM {}", finalVm.getId());
                            }
                        },
                        error -> {
                            finalVm.setStatus(VirtualMachine.VMStatus.ERROR);
                            vmRepository.save(finalVm);
                            logger.error("Error deteniendo VM {}: {}", finalVm.getId(), error.getMessage());
                        }
                );
        
        return new VirtualMachineResponse(finalVm);
    }
    
    /**
     * Reinicia una máquina virtual.
     */
    public VirtualMachineResponse restartVirtualMachine(Long vmId, Long userId) {
        VirtualMachine vm = getVmAndValidateOwnership(vmId, userId);
        
        if (vm.getExternalId() == null) {
            throw new RuntimeException("La máquina virtual no tiene ID externo");
        }
        
        // Actualizar estado
        vm.setStatus(VirtualMachine.VMStatus.RESTARTING);
        final VirtualMachine finalVm = vmRepository.save(vm);
        
        // Reiniciar en el sistema externo
        externalApiService.restartVirtualMachine(finalVm.getExternalId())
                .subscribe(
                        success -> {
                            if (success) {
                                finalVm.setStatus(VirtualMachine.VMStatus.RUNNING);
                                vmRepository.save(finalVm);
                                logger.info("VM {} reiniciada correctamente", finalVm.getId());
                            } else {
                                finalVm.setStatus(VirtualMachine.VMStatus.ERROR);
                                vmRepository.save(finalVm);
                                logger.error("Error reiniciando VM {}", finalVm.getId());
                            }
                        },
                        error -> {
                            finalVm.setStatus(VirtualMachine.VMStatus.ERROR);
                            vmRepository.save(finalVm);
                            logger.error("Error reiniciando VM {}: {}", finalVm.getId(), error.getMessage());
                        }
                );
        
        return new VirtualMachineResponse(finalVm);
    }
    
    /**
     * Elimina una máquina virtual.
     */
    public void deleteVirtualMachine(Long vmId, Long userId) {
        VirtualMachine vm = getVmAndValidateOwnership(vmId, userId);

        // Marcar como eliminando
        vm.setStatus(VirtualMachine.VMStatus.DELETING);
        final VirtualMachine finalVm = vmRepository.save(vm);

        // Si tiene ID externo, eliminar del sistema externo
        if (finalVm.getExternalId() != null) {
            externalApiService.deleteVirtualMachine(finalVm.getExternalId())
                    .subscribe(
                            success -> {
                                if (success) {
                                    // Liberar recursos de la suscripción
                                    userSubscriptionRepository.findActiveSubscriptionByUserId(userId, LocalDateTime.now()).ifPresent(subscription -> {
                                        subscription.setUsedCpu(subscription.getUsedCpu() - finalVm.getCpu());
                                        subscription.setUsedMemory(subscription.getUsedMemory() - finalVm.getMemory());
                                        subscription.setUsedDisk(subscription.getUsedDisk() - finalVm.getDisk());
                                        subscription.setCurrentVMs(subscription.getCurrentVMs() - 1);
                                        userSubscriptionRepository.save(subscription);
                                    });

                                    // Si la eliminación externa es exitosa, eliminar de la BD local
                                    vmRepository.delete(finalVm);
                                    logger.info("VM {} eliminada de la BD", finalVm.getId());
                                } else {
                                    finalVm.setStatus(VirtualMachine.VMStatus.ERROR);
                                    vmRepository.save(finalVm);
                                    logger.error("Error eliminando VM {} del sistema externo", finalVm.getId());
                                }
                            },
                            error -> {
                                // Si falla, marcar como error
                                finalVm.setStatus(VirtualMachine.VMStatus.ERROR);
                                vmRepository.save(finalVm);
                                logger.error("Error eliminando VM {} del sistema externo: {}", 
                                        finalVm.getId(), error.getMessage());
                            }
                    );
        } else {
            // Si no tiene ID externo, eliminar directamente y liberar recursos
            userSubscriptionRepository.findActiveSubscriptionByUserId(userId, LocalDateTime.now()).ifPresent(subscription -> {
                subscription.setUsedCpu(subscription.getUsedCpu() - vm.getCpu());
                subscription.setUsedMemory(subscription.getUsedMemory() - vm.getMemory());
                subscription.setUsedDisk(subscription.getUsedDisk() - vm.getDisk());
                subscription.setCurrentVMs(subscription.getCurrentVMs() - 1);
                userSubscriptionRepository.save(subscription);
            });
            vmRepository.delete(finalVm);
            logger.info("VM {} sin ID externo, eliminada de la BD", finalVm.getId());
        }
    }
    
    /**
     * Sincroniza el estado de una máquina virtual.
     */
    public VirtualMachineResponse syncVirtualMachineStatus(Long vmId, Long userId) {
        VirtualMachine vm = getVmAndValidateOwnership(vmId, userId);
        
        if (vm.getExternalId() == null) {
            throw new RuntimeException("La máquina virtual no tiene ID externo, no se puede sincronizar");
        }
        
        // Obtener estado del sistema externo
        externalApiService.getVirtualMachineStatus(vm.getExternalId())
                .subscribe(
                        status -> {
                            // Actualizar el estado en la base de datos
                            vm.setStatus(status);
                            vmRepository.save(vm);
                            logger.info("Estado de VM {} sincronizado a {}", vm.getId(), status);
                        },
                        error -> {
                            logger.error("Error sincronizando estado de VM {}: {}", vm.getId(), error.getMessage());
                            // Opcional: marcar la VM con un estado de error
                            vm.setStatus(VirtualMachine.VMStatus.ERROR);
                            vmRepository.save(vm);
                        }
                );
        
        return new VirtualMachineResponse(vm);
    }
    
    /**
     * Obtiene los nodos disponibles.
     */
    public Mono<List<String>> getAvailableNodes() {
        return externalApiService.getAvailableNodes();
    }
    
    // Método auxiliar
    private VirtualMachine getVmAndValidateOwnership(Long vmId, Long userId) {
        VirtualMachine vm = vmRepository.findById(vmId)
                .orElseThrow(() -> new RuntimeException("Máquina virtual no encontrada"));
        
        // Verificar que pertenece al usuario
        if (!vm.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permisos para acceder a esta máquina virtual");
        }
        
        return vm;
    }
} 