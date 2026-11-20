package com.esports.tournamentservice.exceptions;

public class TorneoException extends com.esports.common.ArenaException{
    public TorneoException(String message){
        super(400,message);
    }
    public TorneoException(String message,int status) { super(status,message); }
}
