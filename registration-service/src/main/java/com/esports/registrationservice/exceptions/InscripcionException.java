package com.esports.registrationservice.exceptions;


public class InscripcionException extends com.esports.common.ArenaException {

    public InscripcionException(String message) {
        super(400,message);
    }

    public InscripcionException(String message,int status) { super(status,message); }
}
