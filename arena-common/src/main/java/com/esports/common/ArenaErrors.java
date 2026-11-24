package com.esports.common;
import java.util.Map;
import java.util.LinkedHashMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
@RestControllerAdvice
@org.springframework.core.annotation.Order(org.springframework.core.Ordered.HIGHEST_PRECEDENCE)
public class ArenaErrors {
 public record ErrorBody(String mensaje,Map<String,String> errores) {}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ErrorBody> validation(MethodArgumentNotValidException ex) {
  Map<String,String> errors=new LinkedHashMap<>(); ex.getBindingResult().getFieldErrors().forEach(e -> errors.putIfAbsent(e.getField(),e.getDefaultMessage()));
  return ResponseEntity.badRequest().body(new ErrorBody("Revisa los datos del formulario.",errors));
 }
 @ExceptionHandler(Exception.class) ResponseEntity<ErrorBody> failure(Exception ex) {
  if(ex instanceof ArenaException a) return ResponseEntity.status(a.status()).body(new ErrorBody(a.getMessage(),a.errores()));
  int code=500; String message="No se pudo completar la operacion.";
  if(ex instanceof AccessDeniedException) { code=403; message="No tienes permiso para esta accion."; }
  else if(ex instanceof DataIntegrityViolationException) { code=409; message="Los datos ya existen o estan relacionados con otros registros."; }
  else if(ex instanceof ResponseStatusException r) { code=r.getStatusCode().value(); message=r.getReason(); }
  else if(ex instanceof feign.FeignException f) { code=f.status()>0?f.status():503; message="Otro servicio no pudo completar la operacion."; }
  else if(ex instanceof org.springframework.http.converter.HttpMessageNotReadableException || ex instanceof org.springframework.web.method.annotation.MethodArgumentTypeMismatchException) { code=400; message="Revisa el formato de los datos enviados."; }
  else if(ex instanceof IllegalArgumentException || ex.getClass().getSimpleName().endsWith("Exception") && ex.getClass().getPackageName().startsWith("com.esports")) { code=400; message=ex.getMessage(); }
  return ResponseEntity.status(code).body(new ErrorBody(message,Map.of()));
 }
}
