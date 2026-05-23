package com.esports.gameservice.controllers;

import com.esports.gameservice.exceptions.JuegoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

// esto captura los errores y los devuelve como JSON entendible
@RestControllerAdvice
public class ApiExceptionHandler {

    // esto capturara errores de validacion como notblank o min
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidacion(MethodArgumentNotValidException ex){
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()){
            errores.put(error.getField(), error.getDefaultMessage());

        }
        return ResponseEntity.badRequest().body(errores);
    }

    // esto atrapa la excepcion personalizada JuegoException
    @ExceptionHandler(JuegoException.class)
    public ResponseEntity<Map<String,String>> handleJuegoException(JuegoException ex){
        Map<String,String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

}
