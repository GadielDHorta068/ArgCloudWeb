package com.argcloud.vm.repository;

import com.argcloud.vm.entity.HardwarePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad HardwarePlan.
 * Proporciona métodos para interactuar con la base de datos para los planes de hardware.
 */
@Repository
public interface HardwarePlanRepository extends JpaRepository<HardwarePlan, Long> {

    /**
     * Encuentra todos los planes activos.
     * @return lista de planes activos.
     */
    List<HardwarePlan> findByIsActiveTrue();

    /**
     * Encuentra todos los planes activos ordenados por precio mensual.
     * @return lista de planes activos ordenados por precio.
     */
    @Query("SELECT p FROM HardwarePlan p WHERE p.isActive = true ORDER BY p.monthlyPrice ASC")
    List<HardwarePlan> findActiveOrderByPriceAsc();

    /**
     * Encuentra el plan popular (si existe).
     * @return plan popular si existe.
     */
    Optional<HardwarePlan> findByIsPopularTrueAndIsActiveTrue();

    /**
     * Encuentra un plan por nombre.
     * @param name el nombre del plan.
     * @return plan si existe.
     */
    Optional<HardwarePlan> findByName(String name);

    /**
     * Encuentra planes por rango de precio mensual.
     * @param minPrice precio mínimo.
     * @param maxPrice precio máximo.
     * @return lista de planes en el rango de precio.
     */
    @Query("SELECT p FROM HardwarePlan p WHERE p.isActive = true AND p.monthlyPrice BETWEEN :minPrice AND :maxPrice ORDER BY p.monthlyPrice ASC")
    List<HardwarePlan> findByPriceRange(java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice);

    /**
     * Encuentra planes por nivel de soporte.
     * @param supportLevel el nivel de soporte.
     * @return lista de planes con el nivel de soporte específico.
     */
    List<HardwarePlan> findBySupportLevelAndIsActiveTrue(HardwarePlan.SupportLevel supportLevel);

    /**
     * Encuentra planes con recursos mínimos especificados.
     * @param minCpu CPU mínimo requerido.
     * @param minMemory memoria mínima requerida.
     * @param minDisk disco mínimo requerido.
     * @return lista de planes que cumplen con los requisitos mínimos.
     */
    @Query("SELECT p FROM HardwarePlan p WHERE p.isActive = true AND p.totalCpu >= :minCpu AND p.totalMemory >= :minMemory AND p.totalDisk >= :minDisk ORDER BY p.monthlyPrice ASC")
    List<HardwarePlan> findByMinimumResources(Integer minCpu, Integer minMemory, Integer minDisk);

    /**
     * Verifica si existe un plan con el nombre especificado (excluyendo un ID específico).
     * @param name el nombre del plan.
     * @param excludeId ID a excluir de la búsqueda.
     * @return true si existe otro plan con el mismo nombre.
     */
    @Query("SELECT COUNT(p) > 0 FROM HardwarePlan p WHERE p.name = :name AND (:excludeId IS NULL OR p.id != :excludeId)")
    boolean existsByNameAndIdNot(String name, Long excludeId);

    /**
     * Obtiene estadísticas básicas de los planes.
     * @return arreglo con [count, avgPrice, minPrice, maxPrice].
     */
    @Query("SELECT COUNT(p), AVG(p.monthlyPrice), MIN(p.monthlyPrice), MAX(p.monthlyPrice) FROM HardwarePlan p WHERE p.isActive = true")
    Object[] getPlanStatistics();
} 