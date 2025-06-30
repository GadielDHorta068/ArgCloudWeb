package com.argcloud.vm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad que representa un plan de hardware disponible para suscripción.
 * Define los recursos disponibles y precios para cada plan.
 */
@Entity
@Table(name = "hardware_plans")
public class HardwarePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del plan es obligatorio")
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "El precio mensual es obligatorio")
    @Positive(message = "El precio mensual debe ser mayor a 0")
    @Column(name = "monthly_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyPrice;

    @NotNull(message = "El precio anual es obligatorio")
    @Positive(message = "El precio anual debe ser mayor a 0")
    @Column(name = "yearly_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal yearlyPrice;

    @NotNull(message = "El CPU total es obligatorio")
    @Positive(message = "El CPU total debe ser mayor a 0")
    @Column(name = "total_cpu", nullable = false)
    private Integer totalCpu; // Cores disponibles

    @NotNull(message = "La memoria total es obligatoria")
    @Positive(message = "La memoria total debe ser mayor a 0")
    @Column(name = "total_memory", nullable = false)
    private Integer totalMemory; // RAM en MB

    @NotNull(message = "El disco total es obligatorio")
    @Positive(message = "El disco total debe ser mayor a 0")
    @Column(name = "total_disk", nullable = false)
    private Integer totalDisk; // Almacenamiento en GB

    @NotNull(message = "El máximo de VMs es obligatorio")
    @Positive(message = "El máximo de VMs debe ser mayor a 0")
    @Column(name = "max_vms", nullable = false)
    private Integer maxVMs;

    @NotNull(message = "El ancho de banda mensual es obligatorio")
    @Positive(message = "El ancho de banda mensual debe ser mayor a 0")
    @Column(name = "monthly_bandwidth", nullable = false)
    private Integer monthlyBandwidth; // En GB

    @NotNull(message = "El nivel de soporte es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "support_level", nullable = false)
    private SupportLevel supportLevel;

    @ElementCollection
    @CollectionTable(name = "plan_features", joinColumns = @JoinColumn(name = "plan_id"))
    @Column(name = "feature")
    private List<String> features;

    @NotBlank(message = "El color es obligatorio")
    @Column(nullable = false)
    private String color;

    @NotBlank(message = "El icono es obligatorio")
    @Column(nullable = false)
    private String icon;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_popular", nullable = false)
    private Boolean isPopular = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Enum que define los niveles de soporte disponibles
     */
    public enum SupportLevel {
        BASIC("basic"),
        STANDARD("standard"),
        PREMIUM("premium");

        private final String value;

        SupportLevel(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * Establece las fechas de creación y actualización antes de persistir.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Actualiza la fecha de actualización antes de actualizar.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructores
    public HardwarePlan() {}

    /**
     * Constructor con parámetros principales.
     */
    public HardwarePlan(String name, String description, BigDecimal monthlyPrice, BigDecimal yearlyPrice,
                       Integer totalCpu, Integer totalMemory, Integer totalDisk, Integer maxVMs,
                       Integer monthlyBandwidth, SupportLevel supportLevel, String color, String icon) {
        this.name = name;
        this.description = description;
        this.monthlyPrice = monthlyPrice;
        this.yearlyPrice = yearlyPrice;
        this.totalCpu = totalCpu;
        this.totalMemory = totalMemory;
        this.totalDisk = totalDisk;
        this.maxVMs = maxVMs;
        this.monthlyBandwidth = monthlyBandwidth;
        this.supportLevel = supportLevel;
        this.color = color;
        this.icon = icon;
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

    public SupportLevel getSupportLevel() {
        return supportLevel;
    }

    public void setSupportLevel(SupportLevel supportLevel) {
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

    /**
     * Calcula el porcentaje de descuento del plan anual vs mensual.
     * @return el porcentaje de descuento (0-100)
     */
    public double getYearlyDiscount() {
        if (monthlyPrice == null || yearlyPrice == null) {
            return 0.0;
        }
        BigDecimal monthlyTotal = monthlyPrice.multiply(BigDecimal.valueOf(12));
        BigDecimal savings = monthlyTotal.subtract(yearlyPrice);
        return savings.divide(monthlyTotal, 4, BigDecimal.ROUND_HALF_UP)
                     .multiply(BigDecimal.valueOf(100))
                     .doubleValue();
    }

    @Override
    public String toString() {
        return "HardwarePlan{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", monthlyPrice=" + monthlyPrice +
                ", totalCpu=" + totalCpu +
                ", totalMemory=" + totalMemory +
                ", totalDisk=" + totalDisk +
                ", maxVMs=" + maxVMs +
                '}';
    }
} 