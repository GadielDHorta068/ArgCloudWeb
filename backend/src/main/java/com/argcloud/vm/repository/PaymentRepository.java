package com.argcloud.vm.repository;

import com.argcloud.vm.entity.Payment;
import com.argcloud.vm.entity.User;
import com.argcloud.vm.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Payment.
 * Proporciona métodos para interactuar con la base de datos para los pagos.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Encuentra un pago por su ID de Mercado Pago.
     * @param mercadoPagoPaymentId el ID del pago en Mercado Pago.
     * @return pago si existe.
     */
    Optional<Payment> findByMercadoPagoPaymentId(String mercadoPagoPaymentId);

    /**
     * Encuentra un pago por su ID de preferencia de Mercado Pago.
     * @param mercadoPagoPreferenceId el ID de preferencia en Mercado Pago.
     * @return pago si existe.
     */
    Optional<Payment> findByMercadoPagoPreferenceId(String mercadoPagoPreferenceId);

    /**
     * Encuentra todos los pagos de un usuario.
     * @param user el usuario.
     * @return lista de pagos del usuario ordenados por fecha de creación descendente.
     */
    List<Payment> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Encuentra todos los pagos de un usuario por ID.
     * @param userId el ID del usuario.
     * @return lista de pagos del usuario ordenados por fecha de creación descendente.
     */
    List<Payment> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Encuentra pagos por estado.
     * @param status el estado del pago.
     * @return lista de pagos con el estado específico.
     */
    List<Payment> findByStatus(Payment.PaymentStatus status);

    /**
     * Encuentra pagos por suscripción.
     * @param subscription la suscripción.
     * @return lista de pagos de la suscripción.
     */
    List<Payment> findBySubscriptionOrderByCreatedAtDesc(UserSubscription subscription);

    /**
     * Encuentra pagos aprobados de un usuario.
     * @param user el usuario.
     * @return lista de pagos aprobados del usuario.
     */
    List<Payment> findByUserAndStatusOrderByCreatedAtDesc(User user, Payment.PaymentStatus status);

    /**
     * Encuentra pagos por método de pago.
     * @param paymentMethod el método de pago.
     * @return lista de pagos con el método específico.
     */
    List<Payment> findByPaymentMethod(Payment.PaymentMethod paymentMethod);

    /**
     * Encuentra pagos por rango de fechas.
     * @param startDate fecha de inicio.
     * @param endDate fecha de fin.
     * @return lista de pagos en el rango de fechas.
     */
    @Query("SELECT p FROM Payment p WHERE p.dateCreated BETWEEN :startDate AND :endDate ORDER BY p.dateCreated DESC")
    List<Payment> findByDateCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Encuentra pagos por rango de montos.
     * @param minAmount monto mínimo.
     * @param maxAmount monto máximo.
     * @return lista de pagos en el rango de montos.
     */
    @Query("SELECT p FROM Payment p WHERE p.amount BETWEEN :minAmount AND :maxAmount ORDER BY p.amount DESC")
    List<Payment> findByAmountBetween(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount);

    /**
     * Encuentra pagos pendientes que necesitan seguimiento.
     * @param beforeDate fecha límite para considerar pagos pendientes.
     * @return lista de pagos pendientes antiguos.
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.dateCreated <= :beforeDate")
    List<Payment> findPendingPaymentsBefore(@Param("beforeDate") LocalDateTime beforeDate);

    /**
     * Calcula el total de pagos aprobados de un usuario.
     * @param userId el ID del usuario.
     * @return total de pagos aprobados.
     */
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.user.id = :userId AND p.status = 'APPROVED'")
    BigDecimal getTotalApprovedPaymentsByUserId(@Param("userId") Long userId);

    /**
     * Calcula el total de pagos aprobados en un período.
     * @param startDate fecha de inicio.
     * @param endDate fecha de fin.
     * @return total de pagos aprobados en el período.
     */
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'APPROVED' AND p.dateApproved BETWEEN :startDate AND :endDate")
    BigDecimal getTotalApprovedPaymentsInPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Cuenta pagos por estado.
     * @param status el estado del pago.
     * @return número de pagos con el estado específico.
     */
    long countByStatus(Payment.PaymentStatus status);

    /**
     * Encuentra el último pago aprobado de una suscripción.
     * @param subscription la suscripción.
     * @return último pago aprobado si existe.
     */
    @Query("SELECT p FROM Payment p WHERE p.subscription = :subscription AND p.status = 'APPROVED' ORDER BY p.dateApproved DESC")
    Optional<Payment> findLastApprovedPaymentBySubscription(@Param("subscription") UserSubscription subscription);

    /**
     * Verifica si existe un pago aprobado para una suscripción.
     * @param subscriptionId el ID de la suscripción.
     * @return true si existe al menos un pago aprobado.
     */
    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE p.subscription.id = :subscriptionId AND p.status = 'APPROVED'")
    boolean hasApprovedPaymentForSubscription(@Param("subscriptionId") Long subscriptionId);

    /**
     * Obtiene estadísticas de pagos por método de pago.
     * @return lista de objetos con [método, cantidad, total].
     */
    @Query("SELECT p.paymentMethod, COUNT(p), SUM(p.amount) FROM Payment p WHERE p.status = 'APPROVED' GROUP BY p.paymentMethod")
    List<Object[]> getPaymentStatsByMethod();

    /**
     * Obtiene estadísticas de pagos por mes.
     * @return lista de objetos con [año, mes, cantidad, total].
     */
    @Query("SELECT YEAR(p.dateApproved), MONTH(p.dateApproved), COUNT(p), SUM(p.amount) FROM Payment p WHERE p.status = 'APPROVED' GROUP BY YEAR(p.dateApproved), MONTH(p.dateApproved) ORDER BY YEAR(p.dateApproved), MONTH(p.dateApproved)")
    List<Object[]> getMonthlyPaymentStats();

    /**
     * Encuentra pagos que necesitan procesamiento.
     * @return lista de pagos aprobados que no han sido procesados.
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'APPROVED' AND p.processedAt IS NULL")
    List<Payment> findUnprocessedApprovedPayments();

    /**
     * Encuentra pagos por referencia externa.
     * @param externalReference la referencia externa.
     * @return lista de pagos con la referencia externa.
     */
    List<Payment> findByExternalReference(String externalReference);

    /**
     * Encuentra pagos de un usuario en un período específico.
     * @param userId el ID del usuario.
     * @param startDate fecha de inicio.
     * @param endDate fecha de fin.
     * @return lista de pagos del usuario en el período.
     */
    @Query("SELECT p FROM Payment p WHERE p.user.id = :userId AND p.dateCreated BETWEEN :startDate AND :endDate ORDER BY p.dateCreated DESC")
    List<Payment> findUserPaymentsInPeriod(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
} 