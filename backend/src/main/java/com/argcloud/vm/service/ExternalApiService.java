package com.argcloud.vm.service;

import com.argcloud.vm.dto.VirtualMachineRequest;
import com.argcloud.vm.entity.VirtualMachine;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Interfaz genérica para servicios de API externa.
 */
public interface ExternalApiService {
    
    /**
     * Crea una máquina virtual en el sistema externo.
     */
    Mono<String> createVirtualMachine(VirtualMachineRequest request);
    
    /**
     * Inicia una máquina virtual.
     */
    Mono<Boolean> startVirtualMachine(String externalId);
    
    /**
     * Detiene una máquina virtual.
     */
    Mono<Boolean> stopVirtualMachine(String externalId);
    
    /**
     * Reinicia una máquina virtual.
     */
    Mono<Boolean> restartVirtualMachine(String externalId);
    
    /**
     * Elimina una máquina virtual.
     */
    Mono<Boolean> deleteVirtualMachine(String externalId);
    
    /**
     * Obtiene el estado de una máquina virtual.
     */
    Mono<VirtualMachine.VMStatus> getVirtualMachineStatus(String externalId);
    
    /**
     * Obtiene información detallada de una máquina virtual.
     */
    Mono<VirtualMachineInfo> getVirtualMachineInfo(String externalId);
    
    /**
     * Lista todas las máquinas virtuales disponibles.
     */
    Mono<List<VirtualMachineInfo>> listVirtualMachines();
    
    /**
     * Obtiene los nodos disponibles en el sistema.
     */
    Mono<List<String>> getAvailableNodes();
    
    /**
     * Autentica con el sistema externo.
     */
    Mono<String> authenticate();
    
    /**
     * Clase para información de máquina virtual desde API externa.
     */
    class VirtualMachineInfo {
        private String id;
        private String name;
        private String status;
        private String node;
        private Integer cpu;
        private Integer memory;
        private Integer disk;
        private String ipAddress;
        private String macAddress;
        
        // Constructores
        public VirtualMachineInfo() {}
        
        public VirtualMachineInfo(String id, String name, String status, String node) {
            this.id = id;
            this.name = name;
            this.status = status;
            this.node = node;
        }
        
        // Getters y Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getNode() { return node; }
        public void setNode(String node) { this.node = node; }
        
        public Integer getCpu() { return cpu; }
        public void setCpu(Integer cpu) { this.cpu = cpu; }
        
        public Integer getMemory() { return memory; }
        public void setMemory(Integer memory) { this.memory = memory; }
        
        public Integer getDisk() { return disk; }
        public void setDisk(Integer disk) { this.disk = disk; }
        
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        
        public String getMacAddress() { return macAddress; }
        public void setMacAddress(String macAddress) { this.macAddress = macAddress; }
    }
} 