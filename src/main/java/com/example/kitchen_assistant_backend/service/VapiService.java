package com.example.kitchen_assistant_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;

@Service
public class VapiService {

    @Value("${vapi.api.key:}")
    private String vapiApiKey;

    @Value("${vapi.workflow.id:}")
    private String vapiWorkflowId;

    // ✅ Using your ngrok base URL
    @Value("${app.base-url:https://muskox-innocent-sunbeam.ngrok-free.app}")
    private String baseUrl;

    private static final String VAPI_ASSISTANT_URL = "https://api.vapi.ai/assistant";
    private static final String VAPI_WORKFLOW_URL = "https://api.vapi.ai/call/web";

    /**
     * Creates kitchen assistant calling YOUR actual API endpoints
     */
    public Map<String, Object> createKitchenAssistant() {
        System.out.println("🔧 Creating kitchen assistant with YOUR actual API endpoints...");

        if (vapiApiKey == null || vapiApiKey.trim().isEmpty()) {
            throw new RuntimeException("VAPI API key not configured");
        }

        String normalizedBaseUrl = normalizeBaseUrl(baseUrl);
        System.out.println("🌐 Using your ngrok base URL: " + normalizedBaseUrl);

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(15000);
        RestTemplate restTemplate = new RestTemplate(factory);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + vapiApiKey);

        Map<String, Object> assistantConfig = createAssistantConfigWithYourEndpoints(normalizedBaseUrl);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(assistantConfig, headers);

        try {
            System.out.println("⏳ Sending request to VAPI with YOUR endpoints...");

            ResponseEntity<Map> response = restTemplate.exchange(
                    VAPI_ASSISTANT_URL, HttpMethod.POST, request, Map.class);

            System.out.println("✅ Assistant created with YOUR API endpoints: " + response.getBody());
            return response.getBody();

        } catch (HttpClientErrorException e) {
            System.err.println("❌ HTTP Error: " + e.getStatusCode());
            System.err.println("📄 Response body: " + e.getResponseBodyAsString());
            throw new RuntimeException("Assistant creation failed: " + e.getResponseBodyAsString());
        }
    }

    /**
     * ✅ URL normalization
     */
    private String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new RuntimeException("Base URL not configured");
        }

        String normalized = baseUrl.trim();

        if (!normalized.startsWith("http://") && !normalized.startsWith("https://")) {
            normalized = "https://" + normalized;
        }

        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        normalized = normalized + "/";

        return normalized;
    }

    /**
     * Creates assistant config using YOUR actual API endpoints
     */
    private Map<String, Object> createAssistantConfigWithYourEndpoints(String baseUrl) {
        Map<String, Object> config = new HashMap<>();

        config.put("name", "Kitchen Assistant");
        config.put("firstMessage", "Hello! I'm your kitchen assistant. I can help you search for recipes, set timers, and convert measurements. What would you like to cook today?");

        Map<String, Object> model = new HashMap<>();
        model.put("provider", "openai");
        model.put("model", "gpt-4");
        model.put("temperature", 0.7);
        model.put("maxTokens", 500);

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", createSystemPrompt());
        messages.add(systemMessage);
        model.put("messages", messages);

        // ✅ Tools pointing to YOUR actual endpoints
        model.put("tools", createToolsWithYourEndpoints(baseUrl));
        config.put("model", model);

        Map<String, Object> voice = new HashMap<>();
        voice.put("provider", "11labs");
        voice.put("voiceId", "sarah");
        config.put("voice", voice);

        Map<String, Object> transcriber = new HashMap<>();
        transcriber.put("provider", "deepgram");
        transcriber.put("model", "nova-2");
        transcriber.put("language", "en-US");
        config.put("transcriber", transcriber);

        return config;
    }

    /**
     * ✅ Creates tools that call YOUR actual API endpoints
     */
    private List<Map<String, Object>> createToolsWithYourEndpoints(String baseUrl) {
        List<Map<String, Object>> tools = new ArrayList<>();

        // ⚠️ NOTE: VAPI tools expect POST with JSON, but your YouTube API is GET with query param
        // We'll need a bridge endpoint or modify your YouTube API to accept POST

        // 1. YouTube Search Tool → YOUR /api/v1/youtube/search
        // Problem: Your API is GET with query param, VAPI needs POST with JSON
        // Solution: Create bridge endpoint /api/tools/youtube-search that converts to your format
        String youtubeUrl = baseUrl + "api/tools/youtube-search";
        System.out.println("🔍 YouTube tool URL (bridge needed): " + youtubeUrl);

        Map<String, Object> youtubeTool = new HashMap<>();
        youtubeTool.put("type", "function");

        Map<String, Object> youtubeFunction = new HashMap<>();
        youtubeFunction.put("name", "search_youtube_recipes");
        youtubeFunction.put("description", "Search for cooking and recipe videos on YouTube");

        Map<String, Object> youtubeParams = new HashMap<>();
        youtubeParams.put("type", "object");
        youtubeParams.put("properties", Map.of(
                "query", Map.of(
                        "type", "string",
                        "description", "Recipe or dish name to search for"
                )
        ));
        youtubeParams.put("required", List.of("query"));
        youtubeFunction.put("parameters", youtubeParams);
        youtubeTool.put("function", youtubeFunction);

        Map<String, Object> youtubeServer = new HashMap<>();
        youtubeServer.put("url", youtubeUrl);
        youtubeTool.put("server", youtubeServer);
        tools.add(youtubeTool);

        // 2. Timer Tool → YOUR /api/v1/timer/start
        // Good: Your API is already POST with JSON body
        String timerUrl = baseUrl + "api/v1/timer/start";
        System.out.println("⏲️ Timer tool URL (direct): " + timerUrl);

        Map<String, Object> timerTool = new HashMap<>();
        timerTool.put("type", "function");

        Map<String, Object> timerFunction = new HashMap<>();
        timerFunction.put("name", "set_cooking_timer");
        timerFunction.put("description", "Set a cooking timer with a custom name");

        Map<String, Object> timerParams = new HashMap<>();
        timerParams.put("type", "object");
        timerParams.put("properties", Map.of(
                "name", Map.of(
                        "type", "string",
                        "description", "Timer name or purpose"
                ),
                "duration", Map.of(
                        "type", "number",
                        "description", "Duration in minutes"
                ),
                "unit", Map.of(
                        "type", "string",
                        "description", "Time unit (always 'minutes')"
                )
        ));
        timerParams.put("required", List.of("name", "duration", "unit"));
        timerFunction.put("parameters", timerParams);
        timerTool.put("function", timerFunction);

        Map<String, Object> timerServer = new HashMap<>();
        timerServer.put("url", timerUrl);
        timerTool.put("server", timerServer);
        tools.add(timerTool);

        // 3. Measurement Conversion Tool → YOUR /api/v1/measurement/convert
        // Good: Your API is already POST with JSON body
        String convertUrl = baseUrl + "api/v1/measurement/convert";
        System.out.println("🔄 Convert tool URL (direct): " + convertUrl);

        Map<String, Object> convertTool = new HashMap<>();
        convertTool.put("type", "function");

        Map<String, Object> convertFunction = new HashMap<>();
        convertFunction.put("name", "convert_cooking_measurements");
        convertFunction.put("description", "Convert between different cooking measurements");

        Map<String, Object> convertParams = new HashMap<>();
        convertParams.put("type", "object");
        convertParams.put("properties", Map.of(
                "type", Map.of(
                        "type", "string",
                        "description", "Conversion type (volume or weight)"
                ),
                "quantity", Map.of(
                        "type", "number",
                        "description", "Amount to convert"
                ),
                "sourceUnit", Map.of(
                        "type", "string",
                        "description", "Source unit"
                ),
                "targetUnit", Map.of(
                        "type", "string",
                        "description", "Target unit"
                )
        ));
        convertParams.put("required", List.of("type", "quantity", "sourceUnit", "targetUnit"));
        convertFunction.put("parameters", convertParams);
        convertTool.put("function", convertFunction);

        Map<String, Object> convertServer = new HashMap<>();
        convertServer.put("url", convertUrl);
        convertTool.put("server", convertServer);
        tools.add(convertTool);

        // ✅ SUMMARY
        System.out.println("✅ Created " + tools.size() + " tools with YOUR endpoints:");
        System.out.println("   1. YouTube: " + youtubeUrl + " (needs bridge endpoint)");
        System.out.println("   2. Timer: " + timerUrl + " (direct call)");
        System.out.println("   3. Measurement: " + convertUrl + " (direct call)");

        return tools;
    }

    private String createSystemPrompt() {
        return """
            You are a helpful kitchen assistant AI. You can:

            🍳 Search for recipe videos on YouTube
            ⏲️ Set cooking timers with custom names  
            🔄 Convert cooking measurements between units

            Guidelines:
            - Be enthusiastic and helpful about cooking
            - Use the tools when users ask for recipes, timers, or conversions
            - Provide clear, step-by-step guidance
            - Always confirm timer durations and measurement conversions

            Remember: You're here to make cooking easier and more enjoyable!
            """;
    }

    /**
     * Workflow session creation
     */
    public Map<String, Object> createVapiSession() {
        if (vapiWorkflowId == null || vapiWorkflowId.trim().isEmpty()) {
            throw new RuntimeException("No workflow ID configured");
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + vapiApiKey);

        String payload = String.format("{\"workflowId\": \"%s\"}", vapiWorkflowId);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    VAPI_WORKFLOW_URL, HttpMethod.POST, request, Map.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Workflow session failed: " + e.getMessage());
        }
    }
}