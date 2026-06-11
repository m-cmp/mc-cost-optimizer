package com.mcmp.costbe.llm_recommender.service;

public class ApiKeyNotRegisteredException extends RuntimeException {
    public ApiKeyNotRegisteredException(String message) {
        super(message);
    }
}
