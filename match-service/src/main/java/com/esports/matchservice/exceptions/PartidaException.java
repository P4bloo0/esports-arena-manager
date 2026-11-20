package com.esports.matchservice.exceptions;


public class PartidaException extends com.esports.common.ArenaException {

    public PartidaException(String message) {
        super(400,message);
    }

    public PartidaException(String message,int status) { super(status,message); }
}
