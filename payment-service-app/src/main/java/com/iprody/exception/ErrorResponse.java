package com.iprody.exception;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ErrorResponse {

    private final String error;
    private final Instant timestamp;
    private final String operation;
    private final UUID entityId;

    public ErrorResponse(String error, String operation, UUID entityId) {
        this.error = error;
        this.timestamp = Instant.now();
        this.operation = operation;
        this.entityId = entityId;
    }

}