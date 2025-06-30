package com.argcloud.vm.dto;

import com.argcloud.vm.entity.VirtualMachine;
import java.time.LocalDateTime;

public class VirtualMachineResponse {
    
    private Long id;
    private String name;
    private String status;
    private String os;
    private Integer cpu;
    private Integer memory; // in MB
    private Integer disk; // in GB
    private String ipAddress;
    private String macAddress;
    private String nodeName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userName; // Nombre del usuario propietario
    
    // Constructores
    public VirtualMachineResponse() {}
    
    public VirtualMachineResponse(VirtualMachine vm) {
        this.id = vm.getId();
        this.name = vm.getName();
        this.status = vm.getStatus().getValue();
        this.os = vm.getOs();
        this.cpu = vm.getCpu();
        this.memory = vm.getMemory();
        this.disk = vm.getDisk();
        this.ipAddress = vm.getIpAddress();
        this.macAddress = vm.getMacAddress();
        this.nodeName = vm.getNodeName();
        this.createdAt = vm.getCreatedAt();
        this.updatedAt = vm.getUpdatedAt();
        this.userName = vm.getUser() != null ? vm.getUser().getEmail() : null;
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
    
    public String getNodeName() {
        return nodeName;
    }
    
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
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
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
} 