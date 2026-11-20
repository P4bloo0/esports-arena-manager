package com.esports.notificationservice.exceptions;


public class NotificacionException extends com.esports.common.ArenaException {

    public NotificacionException(String message) {
        super(400,message);
    }

    public NotificacionException(String message,int status) { super(status,message); }
}
