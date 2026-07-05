package com.esports.authservice.exceptions;

public class AuthException extends RuntimeException {
    public AuthException(String mensaje) {
        super(mensaje);
    }
}
