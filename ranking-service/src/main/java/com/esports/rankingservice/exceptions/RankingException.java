package com.esports.rankingservice.exceptions;

// excepcion personalizadaa para los failures...
public class RankingException extends RuntimeException {

    public RankingException(String message) {
        super(message);
    }

}
