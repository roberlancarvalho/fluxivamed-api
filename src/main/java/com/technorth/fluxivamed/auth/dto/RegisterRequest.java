package com.technorth.fluxivamed.auth.dto;

import java.util.Set;

public record RegisterRequest(String name, String email, String password, Set<String> roles) {}
