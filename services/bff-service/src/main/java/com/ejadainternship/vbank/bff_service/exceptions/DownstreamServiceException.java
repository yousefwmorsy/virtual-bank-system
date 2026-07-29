package com.ejadainternship.vbank.bff_service.exceptions;

public class DownstreamServiceException extends RuntimeException {
    public DownstreamServiceException(String service) {
        super("Failed to retrieve dashboard data due to an issue with downstream services." + service);
    }
}
