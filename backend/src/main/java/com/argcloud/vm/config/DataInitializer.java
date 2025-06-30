package com.argcloud.vm.config;

import com.argcloud.vm.entity.HardwarePlan;
import com.argcloud.vm.repository.HardwarePlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Inicializador de datos para cargar planes de hardware de ejemplo.
 * Se ejecuta automáticamente al iniciar la aplicación si no hay datos existentes.
 */
@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private HardwarePlanRepository hardwarePlanRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Solo inicializar si no hay planes existentes
        if (hardwarePlanRepository.count() == 0) {
            initializeHardwarePlans();
        }
    }

    /**
     * Crea planes de hardware de ejemplo con diferentes niveles de recursos y precios.
     */
    private void initializeHardwarePlans() {
        System.out.println("Inicializando planes de hardware de ejemplo...");

        // Plan Básico - Ideal para empezar
        HardwarePlan basicPlan = new HardwarePlan(
            "Básico",
            "Perfecto para proyectos personales y desarrollo. Recursos esenciales para comenzar tu aventura en la nube.",
            new BigDecimal("15000.00"),      // $15,000 mensual
            new BigDecimal("144000.00"),     // $144,000 anual (20% descuento)
            2,                               // 2 vCPU
            2048,                           // 2 GB RAM
            50,                             // 50 GB SSD
            3,                              // Máximo 3 VMs
            100,                            // 100 GB ancho de banda
            HardwarePlan.SupportLevel.BASIC,
            "primary",
            "fas fa-rocket"
        );
        basicPlan.setFeatures(Arrays.asList(
            "2 vCPU dedicados",
            "2 GB RAM garantizada",
            "50 GB almacenamiento SSD",
            "Hasta 3 máquinas virtuales",
            "100 GB transferencia mensual",
            "Panel de control web",
            "Soporte por email",
            "Backups automáticos diarios",
            "99.5% uptime garantizado"
        ));
        basicPlan.setIsPopular(false);

        // Plan Estándar - Más popular
        HardwarePlan standardPlan = new HardwarePlan(
            "Estándar",
            "La elección más popular. Balance perfecto entre recursos y precio para empresas pequeñas y medianas.",
            new BigDecimal("35000.00"),      // $35,000 mensual
            new BigDecimal("336000.00"),     // $336,000 anual (20% descuento)
            4,                               // 4 vCPU
            8192,                           // 8 GB RAM
            200,                            // 200 GB SSD
            8,                              // Máximo 8 VMs
            500,                            // 500 GB ancho de banda
            HardwarePlan.SupportLevel.STANDARD,
            "success",
            "fas fa-server"
        );
        standardPlan.setFeatures(Arrays.asList(
            "4 vCPU de alto rendimiento",
            "8 GB RAM DDR4",
            "200 GB almacenamiento SSD NVMe",
            "Hasta 8 máquinas virtuales",
            "500 GB transferencia mensual"
        ));
        standardPlan.setIsPopular(true);  // Plan más popular

        // Plan Premium - Para empresas exigentes
        HardwarePlan premiumPlan = new HardwarePlan(
            "Premium",
            "Máximo rendimiento para aplicaciones críticas. Recursos premium con soporte técnico especializado.",
            new BigDecimal("75000.00"),      // $75,000 mensual
            new BigDecimal("720000.00"),     // $720,000 anual (20% descuento)
            8,                               // 8 vCPU
            16384,                          // 16 GB RAM
            500,                            // 500 GB SSD
            15,                             // Máximo 15 VMs
            1000,                           // 1 TB ancho de banda
            HardwarePlan.SupportLevel.PREMIUM,
            "warning",
            "fas fa-crown"
        );
        premiumPlan.setFeatures(Arrays.asList(
            "8 vCPU de última generación",
            "16 GB RAM ECC",
            "500 GB almacenamiento SSD NVMe",
            "Hasta 15 máquinas virtuales",
            "1 TB transferencia mensual"
        ));
        premiumPlan.setIsPopular(false);

        // Plan Enterprise - Para grandes organizaciones
        HardwarePlan enterprisePlan = new HardwarePlan(
            "Enterprise",
            "Solución completa para grandes organizaciones. Recursos ilimitados con soporte premium dedicado.",
            new BigDecimal("150000.00"),     // $150,000 mensual
            new BigDecimal("1440000.00"),    // $1,440,000 anual (20% descuento)
            16,                              // 16 vCPU
            32768,                          // 32 GB RAM
            1000,                           // 1 TB SSD
            30,                             // Máximo 30 VMs
            2000,                           // 2 TB ancho de banda
            HardwarePlan.SupportLevel.PREMIUM,
            "danger",
            "fas fa-building"
        );
        enterprisePlan.setFeatures(Arrays.asList(
            "16 vCPU Xeon de alto rendimiento",
            "32 GB RAM ECC registrada",
            "1 TB almacenimiento SSD NVMe",
            "Hasta 30 máquinas virtuales",
            "2 TB transferencia mensual"
        ));
        enterprisePlan.setIsPopular(false);

        // Plan Starter - Para pruebas
        HardwarePlan starterPlan = new HardwarePlan(
            "Starter",
            "Plan de entrada perfecto para probar la plataforma. Recursos limitados pero suficientes para experimentar.",
            new BigDecimal("8000.00"),       // $8,000 mensual
            new BigDecimal("76800.00"),      // $76,800 anual (20% descuento)
            1,                               // 1 vCPU
            1024,                           // 1 GB RAM
            25,                             // 25 GB SSD
            2,                              // Máximo 2 VMs
            50,                             // 50 GB ancho de banda
            HardwarePlan.SupportLevel.BASIC,
            "info",
            "fas fa-seedling"
        );
        starterPlan.setFeatures(Arrays.asList(
            "1 vCPU compartido",
            "1 GB RAM",
            "25 GB almacenamiento SSD",
            "Hasta 2 máquinas virtuales",
            "50 GB transferencia mensual",
            "Panel de control básico",
            "Soporte por email",
            "Backups semanales",
            "99% uptime garantizado",
            "Ideal para testing"
        ));
        starterPlan.setIsPopular(false);

        // Plan Developer - Para desarrolladores
        HardwarePlan developerPlan = new HardwarePlan(
            "Developer",
            "Diseñado específicamente para desarrolladores. Herramientas y recursos optimizados para desarrollo.",
            new BigDecimal("25000.00"),      // $25,000 mensual
            new BigDecimal("240000.00"),     // $240,000 anual (20% descuento)
            3,                               // 3 vCPU
            4096,                           // 4 GB RAM
            100,                            // 100 GB SSD
            5,                              // Máximo 5 VMs
            200,                            // 200 GB ancho de banda
            HardwarePlan.SupportLevel.STANDARD,
            "secondary",
            "fas fa-code"
        );
        developerPlan.setFeatures(Arrays.asList(
            "3 vCPU optimizados para desarrollo",
            "4 GB RAM",
            "100 GB almacenamiento SSD",
            "Hasta 5 máquinas virtuales",
            "200 GB transferencia mensual"
        ));
        developerPlan.setIsPopular(false);

        // Guardar todos los planes
        hardwarePlanRepository.saveAll(Arrays.asList(
            starterPlan,
            basicPlan,
            developerPlan,
            standardPlan,
            premiumPlan,
            enterprisePlan
        ));

        System.out.println("¡Planes de hardware inicializados exitosamente!");
        System.out.println("Se crearon " + hardwarePlanRepository.count() + " planes de ejemplo.");
    }
} 