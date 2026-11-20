package com.esports.rankingservice.exceptions;

// excepcion personalizadaa para los failures...
public class RankingException extends com.esports.common.ArenaException {

    public RankingException(String message) {
        super(400,message);
    }

    public RankingException(String message,int status) { super(status,message); }
}
