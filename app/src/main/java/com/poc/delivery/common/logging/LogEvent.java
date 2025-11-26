package com.poc.delivery.common.logging;

public enum LogEvent {

    HTTP_REQUEST_COMPLETED("HTTP-001"),
    ORDER_REQUEST_RECEIVED("ORD-000"),
    ORDER_CREATED("ORD-001"),
    ORDER_RETRIEVED("ORD-002"),
    ORDER_NOT_FOUND("ORD-404"),
    ORDER_VALIDATION_FAILED("ORD-010"),
    ORDER_UNEXPECTED_ERROR("ORD-500");

    private final String code;

    LogEvent(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
