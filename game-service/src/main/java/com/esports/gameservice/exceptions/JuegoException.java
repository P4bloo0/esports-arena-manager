package com.esports.gameservice.exceptions;

// excepcion personalizada que se lanza cuando algo sale mal
public class JuegoException extends RuntimeException {
    public JuegoException(String message) {
        super(message);
    }
}
