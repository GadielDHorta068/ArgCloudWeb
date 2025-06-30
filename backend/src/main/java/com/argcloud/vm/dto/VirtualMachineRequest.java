package com.argcloud.vm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class VirtualMachineRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    private String name;
    
    @NotBlank(message = "El sistema operativo es obligatorio")
    private String os;
    
    @NotNull(message = "El CPU es obligatorio")
    @Positive(message = "El CPU debe ser mayor a 0")
    private Integer cpu;
    
    @NotNull(message = "La memoria es obligatoria")
    @Positive(message = "La memoria debe ser mayor a 0")
    private Integer memory; // in MB
    
    @NotNull(message = "El disco es obligatorio")
    @Positive(message = "El disco debe ser mayor a 0")
    private Integer disk; // in GB
    
    private String nodeName; // Opcional, para especificar el nodo
    
    // Constructores
    public VirtualMachineRequest() {}
    
    public VirtualMachineRequest(String name, String os, Integer cpu, Integer memory, Integer disk) {
        this.name = name;
        this.os = os;
        this.cpu = cpu;
        this.memory = memory;
        this.disk = disk;
    }
    
    // Getters y Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getOs() {
        return os;
    }
    
    public void setOs(String os) {
        this.os = os;
    }
    
    public Integer getCpu() {
        return cpu;
    }
    
    public void setCpu(Integer cpu) {
        this.cpu = cpu;
    }
    
    public Integer getMemory() {
        return memory;
    }
    
    public void setMemory(Integer memory) {
        this.memory = memory;
    }
    
    public Integer getDisk() {
        return disk;
    }
    
    public void setDisk(Integer disk) {
        this.disk = disk;
    }
    
    public String getNodeName() {
        return nodeName;
    }
    
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
} 