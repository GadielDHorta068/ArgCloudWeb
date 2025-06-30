package com.argcloud.vm.repository;

import com.argcloud.vm.entity.User;
import com.argcloud.vm.entity.UserSubscription;
import com.argcloud.vm.entity.HardwarePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad UserSubscription.
 * Proporciona métodos para interactuar con la base de datos para las suscripciones de usuarios.
 */
@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    /**
     * Encuentra la suscripción activa de un usuario.
     * @param user el usuario.
     * @return suscripción activa si existe.
     */
    @Query("SELECT s FROM UserSubscription s WHERE s.user = :user AND s.status = 'ACTIVE' AND s.endDate > :now ORDER BY s.endDate DESC")
    Optional<UserSubscription> findActiveSubscriptionByUser(@Param("user") User user, @Param("now") LocalDateTime now);

    /**
     * Encuentra la suscripción activa de un usuario por ID.
     * @param userId el ID del usuario.
     * @return suscripción activa si existe.
     */
    @Query("SELECT s FROM UserSubscription s WHERE s.user.id = :userId AND s.status = 'ACTIVE' AND s.endDate > :now ORDER BY s.endDate DESC")
    Optional<UserSubscription> findActiveSubscriptionByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    /**
     * Encuentra suscripciones de un usuario por estado específico.
     * @param user el usuario.
     * @param status el estado de la suscripción.
     * @return suscripción que coincide con los criterios.
     */
    Optional<UserSubscription> findByUserAndStatus(User user, UserSubscription.SubscriptionStatus status);

    /**
     * Encuentra todas las suscripciones de un usuario.
     * @param user el usuario.
     * @return lista de suscripciones del usuario.
     */
    List<UserSubscription> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Encuentra todas las suscripciones de un usuario por ID.
     * @param userId el ID del usuario.
     * @return lista de suscripciones del usuario.
     */
    List<UserSubscription> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Encuentra suscripciones por estado.
     * @param status el estado de la suscripción.
     * @return lista de suscripciones con el estado específico.
     */
    List<UserSubscription> findByStatus(UserSubscription.SubscriptionStatus status);

    /**
     * Encuentra suscripciones que expiran pronto.
     * @param beforeDate fecha límite.
     * @return lista de suscripciones que expiran antes de la fecha especificada.
     */
    @Query("SELECT s FROM UserSubscription s WHERE s.status = 'ACTIVE' AND s.endDate <= :beforeDate AND s.endDate > :now")
    List<UserSubscription> findExpiringBefore(@Param("beforeDate") LocalDateTime beforeDate, @Param("now") LocalDateTime now);

    /**
     * Encuentra suscripciones de un plan específico.
     * @param plan el plan de hardware.
     * @return lista de suscripciones del plan.
     */
    List<UserSubscription> findByPlan(HardwarePlan plan);

    /**
     * Encuentra suscripciones activas de un plan específico.
     * @param plan el plan de hardware.
     * @return lista de suscripciones activas del plan.
     */
    @Query("SELECT s FROM UserSubscription s WHERE s.plan = :plan AND s.status = 'ACTIVE' AND s.endDate > :now")
    List<UserSubscription> findActiveSubscriptionsByPlan(@Param("plan") HardwarePlan plan, @Param("now") LocalDateTime now);

    /**
     * Cuenta las suscripciones activas por plan.
     * @param planId el ID del plan.
     * @return número de suscripciones activas.
     */
    @Query("SELECT COUNT(s) FROM UserSubscription s WHERE s.plan.id = :planId AND s.status = 'ACTIVE' AND s.endDate > :now")
    long countActiveSubscriptionsByPlanId(@Param("planId") Long planId, @Param("now") LocalDateTime now);

    /**
     * Encuentra suscripciones que necesitan facturación.
     * @param beforeDate fecha límite para la próxima facturación.
     * @return lista de suscripciones que necesitan facturación.
     */
    @Query("SELECT s FROM UserSubscription s WHERE s.status = 'ACTIVE' AND s.nextBillingDate <= :beforeDate")
    List<UserSubscription> findSubscriptionsNeedingBilling(@Param("beforeDate") LocalDateTime beforeDate);

    /**
     * Encuentra suscripciones por tipo de suscripción y estado.
     * @param subscriptionType el tipo de suscripción.
     * @param status el estado de la suscripción.
     * @return lista de suscripciones que coinciden con los criterios.
     */
    List<UserSubscription> findBySubscriptionTypeAndStatus(
            UserSubscription.SubscriptionType subscriptionType, 
            UserSubscription.SubscriptionStatus status);

    /**
     * Verifica si un usuario tiene una suscripción activa.
     * @param userId el ID del usuario.
     * @return true si el usuario tiene una suscripción activa.
     */
    @Query("SELECT COUNT(s) > 0 FROM UserSubscription s WHERE s.user.id = :userId AND s.status = 'ACTIVE' AND s.endDate > :now")
    boolean hasActiveSubscription(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    /**
     * Obtiene estadísticas de recursos utilizados por usuario.
     * @param userId el ID del usuario.
     * @return arreglo con [totalCpu, totalMemory, totalDisk, totalVMs].
     */
    @Query("SELECT SUM(s.usedCpu), SUM(s.usedMemory), SUM(s.usedDisk), SUM(s.currentVMs) FROM UserSubscription s WHERE s.user.id = :userId AND s.status = 'ACTIVE'")
    Object[] getResourceUsageByUserId(@Param("userId") Long userId);

    /**
     * Encuentra suscripciones por rango de fechas de creación.
     * @param startDate fecha de inicio.
     * @param endDate fecha de fin.
     * @return lista de suscripciones creadas en el rango de fechas.
     */
    @Query("SELECT s FROM UserSubscription s WHERE s.createdAt BETWEEN :startDate AND :endDate ORDER BY s.createdAt DESC")
    List<UserSubscription> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Actualiza los recursos utilizados de una suscripción.
     * @param subscriptionId el ID de la suscripción.
     * @param cpuDelta cambio en CPU.
     * @param memoryDelta cambio en memoria.
     * @param diskDelta cambio en disco.
     * @param vmDelta cambio en número de VMs.
     */
    @Query("UPDATE UserSubscription s SET s.usedCpu = s.usedCpu + :cpuDelta, s.usedMemory = s.usedMemory + :memoryDelta, s.usedDisk = s.usedDisk + :diskDelta, s.currentVMs = s.currentVMs + :vmDelta WHERE s.id = :subscriptionId")
    void updateResourceUsage(@Param("subscriptionId") Long subscriptionId, 
                            @Param("cpuDelta") Integer cpuDelta, 
                            @Param("memoryDelta") Integer memoryDelta, 
                            @Param("diskDelta") Integer diskDelta, 
                            @Param("vmDelta") Integer vmDelta);
} 