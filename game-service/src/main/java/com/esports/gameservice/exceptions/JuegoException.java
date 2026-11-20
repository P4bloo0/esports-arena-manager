package com.esports.gameservice.exceptions;


public class JuegoException extends com.esports.common.ArenaException {
    public JuegoException(String message) {
        super(400,message);
    }
    public JuegoException(String message,int status) { super(status,message); }
}
