package com.esports.sanctionservice.exceptions;

//mensaje custom
public class SancionException extends com.esports.common.ArenaException{

    public SancionException(String message){
        super(400,message);
    }
    public SancionException(String message,int status) { super(status,message); }
}
