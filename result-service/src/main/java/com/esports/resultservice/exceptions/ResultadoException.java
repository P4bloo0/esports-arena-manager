package com.esports.resultservice.exceptions;


public class ResultadoException extends com.esports.common.ArenaException {

    public ResultadoException(String message) {
        super(400,message);
    }

    public ResultadoException(String message,int status) { super(status,message); }
}
