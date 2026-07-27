package com.ejadainternship.vbank.bff_service.utils;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServiceResolver {
    private final DiscoveryClient discoveryClient;

    public ServiceResolver(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    public String resolveBaseUrl(String serviceId) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);

        if (instances.isEmpty()) {
            throw new IllegalStateException("No instances available for service: " + serviceId);
        }

        ServiceInstance instance = instances.getFirst();
        return instance.getUri().toString();
    }
}
