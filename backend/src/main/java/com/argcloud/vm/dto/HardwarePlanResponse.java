package com.argcloud.vm.dto;

import com.argcloud.vm.entity.HardwarePlan;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para HardwarePlan.
 * Representa la información de un plan de hardware que se envía al frontend.
 */
public class HardwarePlanResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal monthlyPrice;
    private BigDecimal yearlyPrice;
    private Integer totalCpu;
    private Integer totalMemory;
    private Integer totalDisk;
    private Integer maxVMs;
    private Integer monthlyBandwidth;
    private String supportLevel;
    private List<String> features;
    private String color;
    private String icon;
    private Boolean isActive;
    private Boolean isPopular;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double yearlyDiscountPercentage;

    // Constructores
    public HardwarePlanResponse() {}

    /**
     * Constructor que convierte una entidad HardwarePlan en DTO.
     * @param plan la entidad HardwarePlan.
     */
    public HardwarePlanResponse(HardwarePlan plan) {
        this.id = plan.getId();
        this.name = plan.getName();
        this.description = plan.getDescription();
        this.monthlyPrice = plan.getMonthlyPrice();
        this.yearlyPrice = plan.getYearlyPrice();
        this.totalCpu = plan.getTotalCpu();
        this.totalMemory = plan.getTotalMemory();
        this.totalDisk = plan.getTotalDisk();
        this.maxVMs = plan.getMaxVMs();
        this.monthlyBandwidth = plan.getMonthlyBandwidth();
        this.supportLevel = plan.getSupportLevel().getValue();
        this.features = plan.getFeatures();
        this.color = plan.getColor();
        this.icon = plan.getIcon();
        this.isActive = plan.getIsActive();
        this.isPopular = plan.getIsPopular();
        this.createdAt = plan.getCreatedAt();
        this.updatedAt = plan.getUpdatedAt();
        this.yearlyDiscountPercentage = plan.getYearlyDiscount();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getMonthlyPrice() {
        return monthlyPrice;
    }

    public void setMonthlyPrice(BigDecimal monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    public BigDecimal getYearlyPrice() {
        return yearlyPrice;
    }

    public void setYearlyPrice(BigDecimal yearlyPrice) {
        this.yearlyPrice = yearlyPrice;
    }

    public Integer getTotalCpu() {
        return totalCpu;
    }

    public void setTotalCpu(Integer totalCpu) {
        this.totalCpu = totalCpu;
    }

    public Integer getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(Integer totalMemory) {
        this.totalMemory = totalMemory;
    }

    public Integer getTotalDisk() {
        return totalDisk;
    }

    public void setTotalDisk(Integer totalDisk) {
        this.totalDisk = totalDisk;
    }

    public Integer getMaxVMs() {
        return maxVMs;
    }

    public void setMaxVMs(Integer maxVMs) {
        this.maxVMs = maxVMs;
    }

    public Integer getMonthlyBandwidth() {
        return monthlyBandwidth;
    }

    public void setMonthlyBandwidth(Integer monthlyBandwidth) {
        this.monthlyBandwidth = monthlyBandwidth;
    }

    public String getSupportLevel() {
        return supportLevel;
    }

    public void setSupportLevel(String supportLevel) {
        this.supportLevel = supportLevel;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsPopular() {
        return isPopular;
    }

    public void setIsPopular(Boolean isPopular) {
        this.isPopular = isPopular;
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

    public Double getYearlyDiscountPercentage() {
        return yearlyDiscountPercentage;
    }

    public void setYearlyDiscountPercentage(Double yearlyDiscountPercentage) {
        this.yearlyDiscountPercentage = yearlyDiscountPercentage;
    }

    @Override
    public String toString() {
        return "HardwarePlanResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", monthlyPrice=" + monthlyPrice +
                ", yearlyPrice=" + yearlyPrice +
                ", totalCpu=" + totalCpu +
                ", totalMemory=" + totalMemory +
                ", totalDisk=" + totalDisk +
                ", maxVMs=" + maxVMs +
                ", isActive=" + isActive +
                ", isPopular=" + isPopular +
                '}';
    }
} 