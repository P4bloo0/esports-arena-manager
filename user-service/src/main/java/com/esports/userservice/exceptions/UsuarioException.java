package com.esports.userservice.exceptions;

public class UsuarioException extends com.esports.common.ArenaException {

    public UsuarioException(String message){
        super(400,message);
    }

    public UsuarioException(String message,int status) { super(status,message); }
}
