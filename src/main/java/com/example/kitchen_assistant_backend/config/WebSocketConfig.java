package com.example.kitchen_assistant_backend.config;

import com.example.kitchen_assistant_backend.websocket.KitchenAssistantHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new KitchenAssistantHandler(), "/ws")
                .setAllowedOrigins("*"); // Allow connections from any origin for now
    }
}