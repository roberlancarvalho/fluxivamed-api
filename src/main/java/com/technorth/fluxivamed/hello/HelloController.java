package com.technorth.fluxivamed.hello;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {
    @GetMapping("/public/ping") public String ping() { return "pong"; }

    @GetMapping("/api/secure")
    public String secure(@AuthenticationPrincipal Jwt jwt) {
        return "ok: " + jwt.getSubject();
    }
}
