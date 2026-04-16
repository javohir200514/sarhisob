package com.example.exseption;

public class AppBadException extends RuntimeException{
    public AppBadException(String message) {
        super(message);
    }
}