package com.esports.common;
public class ArenaException extends RuntimeException {
 private final int status;
 public ArenaException(int status, String message) { super(message); this.status=status; }
 public int status() { return status; }
 public static ArenaException missing(String message) { return new ArenaException(404,message); }
 public static ArenaException conflict(String message) { return new ArenaException(409,message); }
}
