package com.esports.userservice.exceptions;

import java.util.Map;

public class UsuarioException extends com.esports.common.ArenaException {

    public UsuarioException(String message){
        super(400,message);
    }

    public UsuarioException(String message,int status) { super(status,message); }
    public UsuarioException(String message, int status, Map<String,String> errores) {
        super(status, message, errores);
    }
}
