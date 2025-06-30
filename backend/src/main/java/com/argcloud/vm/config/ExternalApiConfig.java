package com.argcloud.vm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class ExternalApiConfig {
    
    @Value("${external.api.proxmox.url:https://localhost:8006}")
    private String proxmoxUrl;
    
    @Value("${external.api.proxmox.username:root@pam}")
    private String proxmoxUsername;
    
    @Value("${external.api.proxmox.password:}")
    private String proxmoxPassword;
    
    @Value("${external.api.mikrotik.url:https://localhost}")
    private String mikrotikUrl;
    
    @Value("${external.api.mikrotik.username:admin}")
    private String mikrotikUsername;
    
    @Value("${external.api.mikrotik.password:}")
    private String mikrotikPassword;
    
    @Value("${external.api.timeout:30}")
    private int timeoutSeconds;
    
    /**
     * Cliente HTTP para Proxmox API.
     */
    @Bean("proxmoxWebClient")
    public WebClient proxmoxWebClient() {
        return createWebClient(proxmoxUrl);
    }
    
    /**
     * Cliente HTTP para MikroTik API.
     */
    @Bean("mikrotikWebClient")
    public WebClient mikrotikWebClient() {
        return createWebClient(mikrotikUrl);
    }
    
    /**
     * Cliente HTTP genérico configurado.
     */
    private WebClient createWebClient(String baseUrl) {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(timeoutSeconds))
                .followRedirect(true);
        
        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB
                .build();
    }
    
    // Getters para las propiedades de configuración
    public String getProxmoxUrl() {
        return proxmoxUrl;
    }
    
    public String getProxmoxUsername() {
        return proxmoxUsername;
    }
    
    public String getProxmoxPassword() {
        return proxmoxPassword;
    }
    
    public String getMikrotikUrl() {
        return mikrotikUrl;
    }
    
    public String getMikrotikUsername() {
        return mikrotikUsername;
    }
    
    public String getMikrotikPassword() {
        return mikrotikPassword;
    }
    
    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }
} 