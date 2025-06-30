package com.argcloud.vm.dto;

import com.argcloud.vm.entity.UserSubscription;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para UserSubscription.
 * Representa la información de una suscripción que se envía al frontend.
 */
public class UserSubscriptionResponse {

    private Long id;
    private Long userId;
    private Long planId;
    private HardwarePlanResponse plan;
    private String subscriptionType;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer usedCpu;
    private Integer usedMemory;
    private Integer usedDisk;
    private Integer currentVMs;
    private LocalDateTime lastBillingDate;
    private LocalDateTime nextBillingDate;
    private ResourceSummaryResponse resourceSummary;

    // Constructores
    public UserSubscriptionResponse() {}

    /**
     * Constructor que convierte una entidad UserSubscription en DTO.
     * @param subscription la entidad UserSubscription.
     */
    public UserSubscriptionResponse(UserSubscription subscription) {
        this.id = subscription.getId();
        this.userId = subscription.getUser().getId();
        this.planId = subscription.getPlan().getId();
        this.plan = new HardwarePlanResponse(subscription.getPlan());
        this.subscriptionType = subscription.getSubscriptionType().getValue();
        this.status = subscription.getStatus().getValue();
        this.startDate = subscription.getStartDate();
        this.endDate = subscription.getEndDate();
        this.usedCpu = subscription.getUsedCpu();
        this.usedMemory = subscription.getUsedMemory();
        this.usedDisk = subscription.getUsedDisk();
        this.currentVMs = subscription.getCurrentVMs();
        this.lastBillingDate = subscription.getLastBillingDate();
        this.nextBillingDate = subscription.getNextBillingDate();
        this.resourceSummary = new ResourceSummaryResponse(subscription);
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public HardwarePlanResponse getPlan() {
        return plan;
    }

    public void setPlan(HardwarePlanResponse plan) {
        this.plan = plan;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public ResourceSummaryResponse getResourceSummary() {
        return resourceSummary;
    }

    public void setResourceSummary(ResourceSummaryResponse resourceSummary) {
        this.resourceSummary = resourceSummary;
    }

    @Override
    public String toString() {
        return "UserSubscriptionResponse{" +
                "id=" + id +
                ", userId=" + userId +
                ", planId=" + planId +
                ", subscriptionType='" + subscriptionType + '\'' +
                ", status='" + status + '\'' +
                ", usedCpu=" + usedCpu +
                ", usedMemory=" + usedMemory +
                ", usedDisk=" + usedDisk +
                ", currentVMs=" + currentVMs +
                '}';
    }

    /**
     * Clase auxiliar para el resúmen de recursos.
     */
    public static class ResourceSummaryResponse {
        private ResourceInfo total;
        private ResourceInfo used;
        private ResourceInfo available;
        private UsagePercentage usage;

        public ResourceSummaryResponse(UserSubscription subscription) {
            this.total = new ResourceInfo(
                subscription.getPlan().getTotalCpu(),
                subscription.getPlan().getTotalMemory(),
                subscription.getPlan().getTotalDisk(),
                subscription.getPlan().getMaxVMs()
            );
            
            this.used = new ResourceInfo(
                subscription.getUsedCpu(),
                subscription.getUsedMemory(),
                subscription.getUsedDisk(),
                subscription.getCurrentVMs()
            );
            
            UserSubscription.ResourceAvailability availability = subscription.getResourceAvailability();
            this.available = new ResourceInfo(
                availability.getAvailableCpu(),
                availability.getAvailableMemory(),
                availability.getAvailableDisk(),
                availability.getAvailableVMs()
            );
            
            this.usage = new UsagePercentage(
                calculateUsagePercentage(subscription.getUsedCpu(), subscription.getPlan().getTotalCpu()),
                calculateUsagePercentage(subscription.getUsedMemory(), subscription.getPlan().getTotalMemory()),
                calculateUsagePercentage(subscription.getUsedDisk(), subscription.getPlan().getTotalDisk()),
                calculateUsagePercentage(subscription.getCurrentVMs(), subscription.getPlan().getMaxVMs())
            );
        }

        private double calculateUsagePercentage(Integer used, Integer total) {
            if (total == null || total == 0) return 0.0;
            return (used != null ? used.doubleValue() : 0.0) / total.doubleValue() * 100.0;
        }

        // Getters y Setters
        public ResourceInfo getTotal() { return total; }
        public void setTotal(ResourceInfo total) { this.total = total; }
        
        public ResourceInfo getUsed() { return used; }
        public void setUsed(ResourceInfo used) { this.used = used; }
        
        public ResourceInfo getAvailable() { return available; }
        public void setAvailable(ResourceInfo available) { this.available = available; }
        
        public UsagePercentage getUsage() { return usage; }
        public void setUsage(UsagePercentage usage) { this.usage = usage; }

        public static class ResourceInfo {
            private Integer cpu;
            private Integer memory;
            private Integer disk;
            private Integer maxVMs;

            public ResourceInfo(Integer cpu, Integer memory, Integer disk, Integer maxVMs) {
                this.cpu = cpu;
                this.memory = memory;
                this.disk = disk;
                this.maxVMs = maxVMs;
            }

            // Getters y Setters
            public Integer getCpu() { return cpu; }
            public void setCpu(Integer cpu) { this.cpu = cpu; }
            
            public Integer getMemory() { return memory; }
            public void setMemory(Integer memory) { this.memory = memory; }
            
            public Integer getDisk() { return disk; }
            public void setDisk(Integer disk) { this.disk = disk; }
            
            public Integer getMaxVMs() { return maxVMs; }
            public void setMaxVMs(Integer maxVMs) { this.maxVMs = maxVMs; }
        }

        public static class UsagePercentage {
            private Double cpu;
            private Double memory;
            private Double disk;
            private Double vms;

            public UsagePercentage(Double cpu, Double memory, Double disk, Double vms) {
                this.cpu = cpu;
                this.memory = memory;
                this.disk = disk;
                this.vms = vms;
            }

            // Getters y Setters
            public Double getCpu() { return cpu; }
            public void setCpu(Double cpu) { this.cpu = cpu; }
            
            public Double getMemory() { return memory; }
            public void setMemory(Double memory) { this.memory = memory; }
            
            public Double getDisk() { return disk; }
            public void setDisk(Double disk) { this.disk = disk; }
            
            public Double getVms() { return vms; }
            public void setVms(Double vms) { this.vms = vms; }
        }
    }
} 