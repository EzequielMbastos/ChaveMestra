package com.ChaveMestra.Application.exception;

public class IaRateLimitException extends RuntimeException {

    private final long segundosParaRetry;

    public IaRateLimitException(String message, long segundosParaRetry) {
        super(message);
        this.segundosParaRetry = segundosParaRetry;
    }

    public long getSegundosParaRetry() {
        return segundosParaRetry;
    }
}
