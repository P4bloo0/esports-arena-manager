package com.esports.common;
public final class ArenaTime {
 private ArenaTime() {}
 public static java.time.LocalDateTime now() { return java.time.LocalDateTime.now(java.time.ZoneId.of("America/Santiago")); }
}
