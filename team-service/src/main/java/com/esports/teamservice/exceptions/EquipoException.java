package com.esports.teamservice.exceptions;

public class EquipoException extends com.esports.common.ArenaException {
    public EquipoException(String message){
        super(400,message);
    }
    public EquipoException(String message,int status) { super(status,message); }
}
