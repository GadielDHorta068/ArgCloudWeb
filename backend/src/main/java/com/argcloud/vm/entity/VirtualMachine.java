package com.argcloud.vm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

@Entity
@Table(name = "virtual_machines")
public class VirtualMachine {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String name;
    
    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VMStatus status;
    
    @NotBlank(message = "El sistema operativo es obligatorio")
    @Column(nullable = false)
    private String os;
    
    @NotNull(message = "El CPU es obligatorio")
    @Positive(message = "El CPU debe ser mayor a 0")
    @Column(nullable = false)
    private Integer cpu;
    
    @NotNull(message = "La memoria es obligatoria")
    @Positive(message = "La memoria debe ser mayor a 0")
    @Column(nullable = false)
    private Integer memory; // in MB
    
    @NotNull(message = "El disco es obligatorio")
    @Positive(message = "El disco debe ser mayor a 0")
    @Column(nullable = false)
    private Integer disk; // in GB
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Campos específicos para la API externa (Proxmox/MikroTik)
    @Column(name = "external_id")
    private String externalId; // ID en el sistema externo
    
    @Column(name = "node_name")
    private String nodeName; // Nodo donde está la VM
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "mac_address")
    private String macAddress;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructores
    public VirtualMachine() {
        this.createdAt = LocalDateTime.now();
    }
    
    public VirtualMachine(String name, VMStatus status, String os, Integer cpu, Integer memory, Integer disk, User user) {
        this();
        this.name = name;
        this.status = status;
        this.os = os;
        this.cpu = cpu;
        this.memory = memory;
        this.disk = disk;
        this.user = user;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public VMStatus getStatus() {
        return status;
    }
    
    public void setStatus(VMStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
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
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getExternalId() {
        return externalId;
    }
    
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
    
    public String getNodeName() {
        return nodeName;
    }
    
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getMacAddress() {
        return macAddress;
    }
    
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Enum para el estado de la VM
    public enum VMStatus {
        RUNNING("running"),
        STOPPED("stopped"),
        RESTARTING("restarting"),
        CREATING("creating"),
        DELETING("deleting"),
        ERROR("error");
        
        private final String value;
        
        VMStatus(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static VMStatus fromString(String value) {
            for (VMStatus status : VMStatus.values()) {
                if (status.value.equalsIgnoreCase(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Estado de VM no válido: " + value);
        }
    }
} 