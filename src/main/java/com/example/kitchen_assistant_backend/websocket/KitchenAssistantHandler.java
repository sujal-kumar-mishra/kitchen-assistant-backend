package com.example.kitchen_assistant_backend.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class KitchenAssistantHandler extends TextWebSocketHandler {

    // Store all active WebSocket connections in a thread-safe set
    private static final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("WebSocket connection established with session ID: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        sessions.remove(session);
        System.out.println("WebSocket connection closed with session ID: " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // This method handles messages from the frontend to the backend, if needed.
        System.out.println("Received message from frontend: " + message.getPayload());
    }

    // --- NEW METHOD FOR BACKEND-TO-FRONTEND COMMUNICATION ---
    public static void sendMessageToAll(String message) {
        sessions.forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                System.err.println("Failed to send message to session: " + session.getId());
            }
        });
    }
}
