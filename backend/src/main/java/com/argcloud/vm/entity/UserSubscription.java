package com.argcloud.vm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entidad que representa la suscripción de un usuario a un plan de hardware.
 * Mantiene el estado de los recursos utilizados y las fechas de facturación.
 */
@Entity
@Table(name = "user_subscriptions")
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private HardwarePlan plan;

    @NotNull(message = "El tipo de suscripción es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type", nullable = false)
    private SubscriptionType subscriptionType;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @NotNull(message = "La fecha de finalización es obligatoria")
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    // Recursos utilizados actualmente
    @Column(name = "used_cpu", nullable = false)
    private Integer usedCpu = 0;

    @Column(name = "used_memory", nullable = false)
    private Integer usedMemory = 0;

    @Column(name = "used_disk", nullable = false)
    private Integer usedDisk = 0;

    @Column(name = "current_vms", nullable = false)
    private Integer currentVMs = 0;

    // Información de facturación
    @Column(name = "last_billing_date")
    private LocalDateTime lastBillingDate;

    @Column(name = "next_billing_date")
    private LocalDateTime nextBillingDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Enum que define los tipos de suscripción disponibles
     */
    public enum SubscriptionType {
        MONTHLY("monthly"),
        YEARLY("yearly");

        private final String value;

        SubscriptionType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * Enum que define los estados de una suscripción
     */
    public enum SubscriptionStatus {
        ACTIVE("active"),
        EXPIRED("expired"),
        CANCELLED("cancelled"),
        PENDING("pending");

        private final String value;

        SubscriptionStatus(String value) {
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
    public UserSubscription() {}

    /**
     * Constructor principal.
     */
    public UserSubscription(User user, HardwarePlan plan, SubscriptionType subscriptionType, 
                           SubscriptionStatus status, LocalDateTime startDate, LocalDateTime endDate) {
        this.user = user;
        this.plan = plan;
        this.subscriptionType = subscriptionType;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public HardwarePlan getPlan() {
        return plan;
    }

    public void setPlan(HardwarePlan plan) {
        this.plan = plan;
    }

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getUsedCpu() {
        return usedCpu;
    }

    public void setUsedCpu(Integer usedCpu) {
        this.usedCpu = usedCpu;
    }

    public Integer getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(Integer usedMemory) {
        this.usedMemory = usedMemory;
    }

    public Integer getUsedDisk() {
        return usedDisk;
    }

    public void setUsedDisk(Integer usedDisk) {
        this.usedDisk = usedDisk;
    }

    public Integer getCurrentVMs() {
        return currentVMs;
    }

    public void setCurrentVMs(Integer currentVMs) {
        this.currentVMs = currentVMs;
    }

    public LocalDateTime getLastBillingDate() {
        return lastBillingDate;
    }

    public void setLastBillingDate(LocalDateTime lastBillingDate) {
        this.lastBillingDate = lastBillingDate;
    }

    public LocalDateTime getNextBillingDate() {
        return nextBillingDate;
    }

    public void setNextBillingDate(LocalDateTime nextBillingDate) {
        this.nextBillingDate = nextBillingDate;
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
     * Verifica si la suscripción está activa y no ha expirado.
     */
    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE && 
               endDate.isAfter(LocalDateTime.now());
    }

    /**
     * Verifica si hay recursos disponibles para crear una nueva VM.
     */
    public boolean hasAvailableResources(Integer requiredCpu, Integer requiredMemory, Integer requiredDisk) {
        if (!isActive()) {
            return false;
        }

        return (usedCpu + requiredCpu <= plan.getTotalCpu()) &&
               (usedMemory + requiredMemory <= plan.getTotalMemory()) &&
               (usedDisk + requiredDisk <= plan.getTotalDisk()) &&
               (currentVMs < plan.getMaxVMs());
    }

    /**
     * Actualiza los recursos utilizados al crear una nueva VM.
     */
    public void allocateResources(Integer cpu, Integer memory, Integer disk) {
        this.usedCpu += cpu;
        this.usedMemory += memory;
        this.usedDisk += disk;
        this.currentVMs += 1;
    }

    /**
     * Libera recursos al eliminar una VM.
     */
    public void deallocateResources(Integer cpu, Integer memory, Integer disk) {
        this.usedCpu = Math.max(0, this.usedCpu - cpu);
        this.usedMemory = Math.max(0, this.usedMemory - memory);
        this.usedDisk = Math.max(0, this.usedDisk - disk);
        this.currentVMs = Math.max(0, this.currentVMs - 1);
    }

    /**
     * Calcula los recursos disponibles.
     */
    public ResourceAvailability getResourceAvailability() {
        return new ResourceAvailability(
            plan.getTotalCpu() - usedCpu,
            plan.getTotalMemory() - usedMemory,
            plan.getTotalDisk() - usedDisk,
            plan.getMaxVMs() - currentVMs
        );
    }

    /**
     * Clase auxiliar para representar disponibilidad de recursos.
     */
    public static class ResourceAvailability {
        private final Integer availableCpu;
        private final Integer availableMemory;
        private final Integer availableDisk;
        private final Integer availableVMs;

        public ResourceAvailability(Integer availableCpu, Integer availableMemory, 
                                  Integer availableDisk, Integer availableVMs) {
            this.availableCpu = Math.max(0, availableCpu);
            this.availableMemory = Math.max(0, availableMemory);
            this.availableDisk = Math.max(0, availableDisk);
            this.availableVMs = Math.max(0, availableVMs);
        }

        public Integer getAvailableCpu() { return availableCpu; }
        public Integer getAvailableMemory() { return availableMemory; }
        public Integer getAvailableDisk() { return availableDisk; }
        public Integer getAvailableVMs() { return availableVMs; }
    }

    @Override
    public String toString() {
        return "UserSubscription{" +
                "id=" + id +
                ", user=" + (user != null ? user.getEmail() : null) +
                ", plan=" + (plan != null ? plan.getName() : null) +
                ", subscriptionType=" + subscriptionType +
                ", status=" + status +
                ", usedCpu=" + usedCpu +
                ", usedMemory=" + usedMemory +
                ", usedDisk=" + usedDisk +
                ", currentVMs=" + currentVMs +
                '}';
    }
} 