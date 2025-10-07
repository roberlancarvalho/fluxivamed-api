package com.technorth.fluxivamed.auth.dto;

import java.util.Set;

public record UserMeResponse(Long id, String name, String email, Set<String> roles) {}
