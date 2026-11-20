package com.esports.authservice.dtos;
import java.util.Set;
public record AuthResponse(String token,String tokenType,String nickname,Set<String> roles,Long userId) {
 public String getToken() { return token; } public String getNickname() { return nickname; } public Set<String> getRoles() { return roles; }
}
