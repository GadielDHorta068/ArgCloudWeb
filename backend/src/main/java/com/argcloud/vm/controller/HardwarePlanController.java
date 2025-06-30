package com.argcloud.vm.controller;

import com.argcloud.vm.dto.HardwarePlanResponse;
import com.argcloud.vm.entity.HardwarePlan;
import com.argcloud.vm.repository.HardwarePlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador REST para manejar planes de hardware.
 * Proporciona endpoints para listar, filtrar y obtener información de planes.
 */
@RestController
@RequestMapping("/api/plans")
@CrossOrigin(origins = "*")
public class HardwarePlanController {

    @Autowired
    private HardwarePlanRepository hardwarePlanRepository;

    /**
     * Obtiene todos los planes de hardware activos.
     * 
     * @return Lista de planes activos ordenados por precio
     */
    @GetMapping
    public ResponseEntity<List<HardwarePlanResponse>> getActivePlans() {
        try {
            List<HardwarePlan> plans = hardwarePlanRepository.findActiveOrderByPriceAsc();
            List<HardwarePlanResponse> planResponses = plans.stream()
                .map(HardwarePlanResponse::new)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(planResponses);
        } catch (Exception e) {
            System.err.println("Error al obtener planes activos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtiene un plan específico por ID.
     * 
     * @param id ID del plan
     * @return Plan solicitado si existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<HardwarePlanResponse> getPlanById(@PathVariable Long id) {
        try {
            Optional<HardwarePlan> plan = hardwarePlanRepository.findById(id);
            
            if (plan.isPresent() && plan.get().getIsActive()) {
                return ResponseEntity.ok(new HardwarePlanResponse(plan.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error al obtener plan por ID: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtiene el plan más popular (destacado).
     * 
     * @return Plan popular si existe
     */
    @GetMapping("/popular")
    public ResponseEntity<HardwarePlanResponse> getPopularPlan() {
        try {
            Optional<HardwarePlan> popularPlan = hardwarePlanRepository.findByIsPopularTrueAndIsActiveTrue();
            
            if (popularPlan.isPresent()) {
                return ResponseEntity.ok(new HardwarePlanResponse(popularPlan.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error al obtener plan popular: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Busca planes por rango de precio mensual.
     * 
     * @param min Precio mínimo
     * @param max Precio máximo
     * @return Lista de planes en el rango especificado
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<HardwarePlanResponse>> getPlansByPriceRange(
            @RequestParam BigDecimal min, 
            @RequestParam BigDecimal max) {
        try {
            List<HardwarePlan> plans = hardwarePlanRepository.findByPriceRange(min, max);
            List<HardwarePlanResponse> planResponses = plans.stream()
                .map(HardwarePlanResponse::new)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(planResponses);
        } catch (Exception e) {
            System.err.println("Error al buscar planes por rango de precio: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Busca planes que cumplan con recursos mínimos.
     * 
     * @param cpu CPU mínimo requerido
     * @param memory Memoria mínima requerida en MB
     * @param disk Disco mínimo requerido en GB
     * @return Lista de planes que cumplen los requisitos
     */
    @GetMapping("/minimum-resources")
    public ResponseEntity<List<HardwarePlanResponse>> getPlansByMinimumResources(
            @RequestParam Integer cpu,
            @RequestParam Integer memory,
            @RequestParam Integer disk) {
        try {
            List<HardwarePlan> plans = hardwarePlanRepository.findByMinimumResources(cpu, memory, disk);
            List<HardwarePlanResponse> planResponses = plans.stream()
                .map(HardwarePlanResponse::new)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(planResponses);
        } catch (Exception e) {
            System.err.println("Error al buscar planes por recursos mínimos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Busca planes por nivel de soporte.
     * 
     * @param level Nivel de soporte (basic, standard, premium)
     * @return Lista de planes con el nivel de soporte especificado
     */
    @GetMapping("/support-level/{level}")
    public ResponseEntity<List<HardwarePlanResponse>> getPlansBySupportLevel(@PathVariable String level) {
        try {
            HardwarePlan.SupportLevel supportLevel;
            
            switch (level.toLowerCase()) {
                case "basic":
                    supportLevel = HardwarePlan.SupportLevel.BASIC;
                    break;
                case "standard":
                    supportLevel = HardwarePlan.SupportLevel.STANDARD;
                    break;
                case "premium":
                    supportLevel = HardwarePlan.SupportLevel.PREMIUM;
                    break;
                default:
                    return ResponseEntity.badRequest().build();
            }
            
            List<HardwarePlan> plans = hardwarePlanRepository.findBySupportLevelAndIsActiveTrue(supportLevel);
            List<HardwarePlanResponse> planResponses = plans.stream()
                .map(HardwarePlanResponse::new)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(planResponses);
        } catch (Exception e) {
            System.err.println("Error al buscar planes por nivel de soporte: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtiene estadísticas básicas de los planes.
     * 
     * @return Objeto con estadísticas de los planes
     */
    @GetMapping("/statistics")
    public ResponseEntity<Object> getPlanStatistics() {
        try {
            Object[] stats = hardwarePlanRepository.getPlanStatistics();
            
            if (stats != null && stats.length >= 4) {
                return ResponseEntity.ok(new Object() {
                    public final Long totalPlans = (Long) stats[0];
                    public final Double averagePrice = (Double) stats[1];
                    public final BigDecimal minPrice = (BigDecimal) stats[2];
                    public final BigDecimal maxPrice = (BigDecimal) stats[3];
                });
            } else {
                return ResponseEntity.ok(new Object() {
                    public final Long totalPlans = 0L;
                    public final Double averagePrice = 0.0;
                    public final BigDecimal minPrice = BigDecimal.ZERO;
                    public final BigDecimal maxPrice = BigDecimal.ZERO;
                });
            }
        } catch (Exception e) {
            System.err.println("Error al obtener estadísticas de planes: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint de salud para verificar que el servicio funciona.
     * 
     * @return Estado del servicio
     */
    @GetMapping("/health")
    public ResponseEntity<Object> healthCheck() {
        try {
            long totalPlans = hardwarePlanRepository.count();
            long activePlans = hardwarePlanRepository.findByIsActiveTrue().size();
            
            final long totalPlansCount = totalPlans;
            final long activePlansCount = activePlans;
            
            return ResponseEntity.ok(new Object() {
                public final String status = "OK";
                public final String message = "Hardware Plan Service is running";
                public final long totalPlans = totalPlansCount;
                public final long activePlans = activePlansCount;
                public final String timestamp = java.time.LocalDateTime.now().toString();
            });
        } catch (Exception e) {
            System.err.println("Error en health check: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
} 