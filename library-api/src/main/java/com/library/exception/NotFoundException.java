package com.library.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final String resourceType;
    private final Object resourceId;

    public NotFoundException(String resourceType, Object resourceId) {
        super(resourceType + " com ID '" + resourceId + "' não encontrado(a).");
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

}
