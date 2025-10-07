package com.technorth.fluxivamed.auth.dto;

public record AuthResponse(String access_token, String token_type, long expires_in) {
    public static AuthResponse bearer(String token, long expiresInSeconds) {
        return new AuthResponse(token, "Bearer", expiresInSeconds);
    }
}
