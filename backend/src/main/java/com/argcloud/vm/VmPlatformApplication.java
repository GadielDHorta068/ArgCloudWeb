package com.argcloud.vm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Spring Boot.
 */
@SpringBootApplication
public class VmPlatformApplication {

    /**
     * Punto de entrada principal para la aplicación.
     * @param args argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        SpringApplication.run(VmPlatformApplication.class, args);
    }
} 