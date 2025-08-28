package com.example.kitchen_assistant_backend.controller;

import com.example.kitchen_assistant_backend.service.VapiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@CrossOrigin
@RequestMapping("/api/v1")
public class VapiController {

    @Autowired
    private VapiService vapiService;

    @Value("${vapi.workflow.id:}")
    private String vapiWorkflowId;

    /**
     * Get VAPI session configuration for frontend
     * Now returns assistant ID instead of workflow ID for proper tool integration
     */
    @GetMapping("/vapi-session")
    public ResponseEntity<?> getVapiSession() {
        try {
            System.out.println("🚀 Creating VAPI kitchen assistant session...");

            // Try to create/get assistant first (recommended approach)
            try {
                Map<String, Object> assistant = vapiService.createKitchenAssistant();

                Map<String, Object> response = new HashMap<>();
                response.put("assistantId", assistant.get("id"));
                response.put("type", "assistant");
                response.put("success", true);
                response.put("message", "Kitchen Assistant ready with full functionality");

                System.out.println("✅ Assistant created with ID: " + assistant.get("id"));
                return ResponseEntity.ok(response);

            } catch (Exception assistantError) {
                System.err.println("⚠️ Failed to create assistant, falling back to workflow: " + assistantError.getMessage());

                // Fallback to workflow if assistant creation fails
                if (vapiWorkflowId != null && !vapiWorkflowId.trim().isEmpty()) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("workflowId", vapiWorkflowId);
                    response.put("type", "workflow");
                    response.put("success", true);
                    response.put("message", "Kitchen Assistant ready (workflow mode - limited functionality)");

                    return ResponseEntity.ok(response);
                } else {
                    throw new RuntimeException("No assistant or workflow configuration available");
                }
            }

        } catch (Exception e) {
            System.err.println("❌ VAPI session creation failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Failed to create VAPI session: " + e.getMessage(),
                            "success", false
                    ));
        }
    }

    /**
     * Create a web call session (mainly for workflow-based calls)
     */
    @PostMapping("/vapi-call")
    public ResponseEntity<?> createVapiCall() {
        try {
            Map<String, Object> sessionInfo = vapiService.createVapiSession();
            return ResponseEntity.ok(sessionInfo);

        } catch (Exception e) {
            System.err.println("❌ VAPI call creation failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create VAPI call: " + e.getMessage()));
        }
    }

    /**
     * Health check endpoint for VAPI integration
     */
    @GetMapping("/vapi-health")
    public ResponseEntity<?> getVapiHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "healthy");
        health.put("hasWorkflowId", vapiWorkflowId != null && !vapiWorkflowId.trim().isEmpty());
        health.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(health);
    }
}