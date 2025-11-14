package com.technorth.fluxivamed.security;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    public JwtChannelInterceptor(JwtDecoder jwtDecoder, JwtAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtDecoder = jwtDecoder;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // Verifica se é um comando CONNECT
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Tenta pegar o header "Authorization"
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7); // Remove o "Bearer "

                try {
                    // Decodifica e valida o token
                    Jwt jwt = jwtDecoder.decode(token);

                    // Converte o JWT em um objeto Authentication do Spring Security
                    Authentication authentication = jwtAuthenticationConverter.convert(jwt);

                    // Define o usuário como autenticado para esta sessão WebSocket
                    accessor.setUser(authentication);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                } catch (Exception e) {
                    // Token inválido (expirado, assinatura errada, etc.)
                    System.err.println("Falha ao autenticar WebSocket com token: " + e.getMessage());
                    // A conexão continuará como anônima e será bloqueada pelo WebSocketSecurityConfig
                }
            }
        }
        return message;
    }
}