package com.esports.common;

import java.util.Map;

public class ArenaException extends RuntimeException {
 private final int status;
 private final Map<String,String> errores;
 public ArenaException(int status, String message) { this(status,message,Map.of()); }
 public ArenaException(int status, String message, Map<String,String> errores) {
  super(message);
  this.status=status;
  this.errores=Map.copyOf(errores);
 }
 public int status() { return status; }
 public Map<String,String> errores() { return errores; }
 public static ArenaException missing(String message) { return new ArenaException(404,message); }
 public static ArenaException conflict(String message) { return new ArenaException(409,message); }
}
