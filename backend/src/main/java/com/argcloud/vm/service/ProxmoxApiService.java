package com.argcloud.vm.service;

import com.argcloud.vm.config.ExternalApiConfig;
import com.argcloud.vm.dto.VirtualMachineRequest;
import com.argcloud.vm.entity.VirtualMachine;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProxmoxApiService implements ExternalApiService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProxmoxApiService.class);
    
    private final WebClient webClient;
    private final ExternalApiConfig config;
    private final ObjectMapper objectMapper;
    private String authTicket;
    private String csrfToken;
    
    public ProxmoxApiService(@Qualifier("proxmoxWebClient") WebClient webClient, 
                           ExternalApiConfig config, 
                           ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.config = config;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public Mono<String> authenticate() {
        return webClient.post()
                .uri("/api2/json/access/ticket")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("username=" + config.getProxmoxUsername() + 
                          "&password=" + config.getProxmoxPassword())
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        JsonNode jsonNode = objectMapper.readTree(response);
                        JsonNode data = jsonNode.get("data");
                        this.authTicket = data.get("ticket").asText();
                        this.csrfToken = data.get("CSRFPreventionToken").asText();
                        return authTicket;
                    } catch (Exception e) {
                        logger.error("Error al autenticar con Proxmox: {}", e.getMessage());
                        throw new RuntimeException("Error de autenticación", e);
                    }
                })
                .doOnError(error -> logger.error("Error en autenticación Proxmox: {}", error.getMessage()));
    }
    
    @Override
    public Mono<String> createVirtualMachine(VirtualMachineRequest request) {
        return ensureAuthenticated()
                .then(getNextVmId())
                .flatMap(vmId -> {
                    String node = request.getNodeName() != null ? request.getNodeName() : "pve";
                    
                    String body = String.format(
                        "vmid=%s&name=%s&memory=%d&cores=%d&scsi0=local-lvm:%d&net0=virtio,bridge=vmbr0&ostype=l26&boot=order=scsi0",
                        vmId, request.getName(), request.getMemory(), request.getCpu(), request.getDisk()
                    );
                    
                    return webClient.post()
                            .uri("/api2/json/nodes/{node}/qemu", node)
                            .header("Cookie", "PVEAuthCookie=" + authTicket)
                            .header("CSRFPreventionToken", csrfToken)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .bodyValue(body)
                            .retrieve()
                            .bodyToMono(String.class)
                            .map(response -> vmId)
                            .doOnSuccess(id -> logger.info("VM creada con ID: {}", id))
                            .doOnError(error -> logger.error("Error creando VM: {}", error.getMessage()));
                });
    }
    
    @Override
    public Mono<Boolean> startVirtualMachine(String externalId) {
        return ensureAuthenticated()
                .then(getNodeForVm(externalId))
                .flatMap(node -> webClient.post()
                        .uri("/api2/json/nodes/{node}/qemu/{vmid}/status/start", node, externalId)
                        .header("Cookie", "PVEAuthCookie=" + authTicket)
                        .header("CSRFPreventionToken", csrfToken)
                        .retrieve()
                        .bodyToMono(String.class)
                        .map(response -> true)
                        .doOnError(error -> logger.error("Error iniciando VM {}: {}", externalId, error.getMessage()))
                        .onErrorReturn(false));
    }
    
    @Override
    public Mono<Boolean> stopVirtualMachine(String externalId) {
        return ensureAuthenticated()
                .then(getNodeForVm(externalId))
                .flatMap(node -> webClient.post()
                        .uri("/api2/json/nodes/{node}/qemu/{vmid}/status/stop", node, externalId)
                        .header("Cookie", "PVEAuthCookie=" + authTicket)
                        .header("CSRFPreventionToken", csrfToken)
                        .retrieve()
                        .bodyToMono(String.class)
                        .map(response -> true)
                        .doOnError(error -> logger.error("Error deteniendo VM {}: {}", externalId, error.getMessage()))
                        .onErrorReturn(false));
    }
    
    @Override
    public Mono<Boolean> restartVirtualMachine(String externalId) {
        return ensureAuthenticated()
                .then(getNodeForVm(externalId))
                .flatMap(node -> webClient.post()
                        .uri("/api2/json/nodes/{node}/qemu/{vmid}/status/reboot", node, externalId)
                        .header("Cookie", "PVEAuthCookie=" + authTicket)
                        .header("CSRFPreventionToken", csrfToken)
                        .retrieve()
                        .bodyToMono(String.class)
                        .map(response -> true)
                        .doOnError(error -> logger.error("Error reiniciando VM {}: {}", externalId, error.getMessage()))
                        .onErrorReturn(false));
    }
    
    @Override
    public Mono<Boolean> deleteVirtualMachine(String externalId) {
        return ensureAuthenticated()
                .then(getNodeForVm(externalId))
                .flatMap(node -> webClient.delete()
                        .uri("/api2/json/nodes/{node}/qemu/{vmid}", node, externalId)
                        .header("Cookie", "PVEAuthCookie=" + authTicket)
                        .header("CSRFPreventionToken", csrfToken)
                        .retrieve()
                        .bodyToMono(String.class)
                        .map(response -> true)
                        .doOnError(error -> logger.error("Error eliminando VM {}: {}", externalId, error.getMessage()))
                        .onErrorReturn(false));
    }
    
    @Override
    public Mono<VirtualMachine.VMStatus> getVirtualMachineStatus(String externalId) {
        return ensureAuthenticated()
                .then(getNodeForVm(externalId))
                .flatMap(node -> webClient.get()
                        .uri("/api2/json/nodes/{node}/qemu/{vmid}/status/current", node, externalId)
                        .header("Cookie", "PVEAuthCookie=" + authTicket)
                        .retrieve()
                        .bodyToMono(String.class)
                        .map(response -> {
                            try {
                                JsonNode jsonNode = objectMapper.readTree(response);
                                String status = jsonNode.get("data").get("status").asText();
                                return mapProxmoxStatus(status);
                            } catch (Exception e) {
                                logger.error("Error parseando estado de VM: {}", e.getMessage());
                                return VirtualMachine.VMStatus.ERROR;
                            }
                        }));
    }
    
    @Override
    public Mono<VirtualMachineInfo> getVirtualMachineInfo(String externalId) {
        return ensureAuthenticated()
                .then(getNodeForVm(externalId))
                .flatMap(node -> webClient.get()
                        .uri("/api2/json/nodes/{node}/qemu/{vmid}/config", node, externalId)
                        .header("Cookie", "PVEAuthCookie=" + authTicket)
                        .retrieve()
                        .bodyToMono(String.class)
                        .map(response -> parseVmInfo(response, externalId, node)));
    }
    
    @Override
    public Mono<List<VirtualMachineInfo>> listVirtualMachines() {
        return ensureAuthenticated()
                .then(getAvailableNodes())
                .flatMap(nodes -> {
                    List<Mono<List<VirtualMachineInfo>>> nodeVms = new ArrayList<>();
                    for (String node : nodes) {
                        nodeVms.add(getVmsFromNode(node));
                    }
                    return Mono.zip(nodeVms, objects -> {
                        List<VirtualMachineInfo> allVms = new ArrayList<>();
                        for (Object obj : objects) {
                            allVms.addAll((List<VirtualMachineInfo>) obj);
                        }
                        return allVms;
                    });
                });
    }
    
    @Override
    public Mono<List<String>> getAvailableNodes() {
        return ensureAuthenticated()
                .then(webClient.get()
                        .uri("/api2/json/nodes")
                        .header("Cookie", "PVEAuthCookie=" + authTicket)
                        .retrieve()
                        .bodyToMono(String.class)
                        .map(response -> {
                            try {
                                JsonNode jsonNode = objectMapper.readTree(response);
                                JsonNode data = jsonNode.get("data");
                                List<String> nodes = new ArrayList<>();
                                for (JsonNode node : data) {
                                    nodes.add(node.get("node").asText());
                                }
                                return nodes;
                            } catch (Exception e) {
                                logger.error("Error obteniendo nodos: {}", e.getMessage());
                                return List.of("pve"); // nodo por defecto
                            }
                        }));
    }
    
    // Métodos auxiliares
    private Mono<String> ensureAuthenticated() {
        if (authTicket == null || authTicket.isEmpty()) {
            return authenticate();
        }
        return Mono.just(authTicket);
    }
    
    private Mono<String> getNextVmId() {
        return webClient.get()
                .uri("/api2/json/cluster/nextid")
                .header("Cookie", "PVEAuthCookie=" + authTicket)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        JsonNode jsonNode = objectMapper.readTree(response);
                        return jsonNode.get("data").asText();
                    } catch (Exception e) {
                        logger.error("Error obteniendo siguiente ID: {}", e.getMessage());
                        return "100"; // ID por defecto
                    }
                });
    }
    
    private Mono<String> getNodeForVm(String vmId) {
        return Mono.just("pve"); // Simplificado, en producción buscar el nodo real
    }
    
    private Mono<List<VirtualMachineInfo>> getVmsFromNode(String node) {
        return webClient.get()
                .uri("/api2/json/nodes/{node}/qemu", node)
                .header("Cookie", "PVEAuthCookie=" + authTicket)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        JsonNode jsonNode = objectMapper.readTree(response);
                        JsonNode data = jsonNode.get("data");
                        List<VirtualMachineInfo> vms = new ArrayList<>();
                        for (JsonNode vm : data) {
                            VirtualMachineInfo vmInfo = new VirtualMachineInfo();
                            vmInfo.setId(vm.get("vmid").asText());
                            vmInfo.setName(vm.get("name").asText());
                            vmInfo.setStatus(vm.get("status").asText());
                            vmInfo.setNode(node);
                            vms.add(vmInfo);
                        }
                        return vms;
                    } catch (Exception e) {
                        logger.error("Error parseando VMs del nodo {}: {}", node, e.getMessage());
                        return new ArrayList<>();
                    }
                });
    }
    
    private VirtualMachineInfo parseVmInfo(String response, String vmId, String node) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode data = jsonNode.get("data");
            
            VirtualMachineInfo vmInfo = new VirtualMachineInfo();
            vmInfo.setId(vmId);
            vmInfo.setNode(node);
            vmInfo.setName(data.has("name") ? data.get("name").asText() : "");
            vmInfo.setCpu(data.has("cores") ? data.get("cores").asInt() : 0);
            vmInfo.setMemory(data.has("memory") ? data.get("memory").asInt() : 0);
            
            return vmInfo;
        } catch (Exception e) {
            logger.error("Error parseando información de VM: {}", e.getMessage());
            return new VirtualMachineInfo(vmId, "", "unknown", node);
        }
    }
    
    private VirtualMachine.VMStatus mapProxmoxStatus(String proxmoxStatus) {
        return switch (proxmoxStatus.toLowerCase()) {
            case "running" -> VirtualMachine.VMStatus.RUNNING;
            case "stopped" -> VirtualMachine.VMStatus.STOPPED;
            case "paused" -> VirtualMachine.VMStatus.STOPPED;
            default -> VirtualMachine.VMStatus.ERROR;
        };
    }
} 