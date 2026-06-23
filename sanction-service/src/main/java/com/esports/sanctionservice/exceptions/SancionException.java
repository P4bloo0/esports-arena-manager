package com.esports.sanctionservice.exceptions;

//mensaje custom
public class SancionException extends RuntimeException{

    public SancionException(String message){
        super(message);
    }
}
