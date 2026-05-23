package com.esports.sanctionservice.exceptions;

// manda un mensaje personalizado
public class SancionException extends RuntimeException{

    public SancionException(String message){
        super(message);
    }
}
