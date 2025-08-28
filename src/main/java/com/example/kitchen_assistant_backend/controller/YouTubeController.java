package com.example.kitchen_assistant_backend.controller;

import com.example.kitchen_assistant_backend.model.VideoResult;
import com.example.kitchen_assistant_backend.service.YouTubeService;
import com.example.kitchen_assistant_backend.websocket.KitchenAssistantHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/youtube")
public class YouTubeController {

    private final YouTubeService youTubeService;

    @Autowired
    public YouTubeController(YouTubeService youTubeService) {
        this.youTubeService = youTubeService;
    }

    /**
     * UPDATED: Changed from GET to POST to work with Retell AI's tool calling.
     * It now accepts a JSON body with a "query" field.
     */
    @PostMapping("/search")
    public ResponseEntity<List<VideoResult>> search(@RequestBody Map<String, String> payload) {
        String query = payload.get("query");
        if (query == null || query.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<VideoResult> results = youTubeService.searchVideos(query);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/playback")
    public ResponseEntity<String> handlePlayback(@RequestBody Map<String, String> payload) {
        String command = payload.get("command");
        String videoId = payload.get("videoId");
        String message;

        switch (command) {
            case "PLAY":
                if (videoId == null || videoId.isEmpty()) {
                    return ResponseEntity.badRequest().body("Missing videoId for PLAY command");
                }
                message = "{\"command\":\"PLAY_VIDEO\", \"videoId\":\"" + videoId + "\"}";
                KitchenAssistantHandler.sendMessageToAll(message);
                return ResponseEntity.ok("Command to play video sent.");
            case "PAUSE":
                message = "{\"command\":\"PAUSE_VIDEO\"}";
                KitchenAssistantHandler.sendMessageToAll(message);
                return ResponseEntity.ok("Command to pause video sent.");
            case "RESUME":
                message = "{\"command\":\"RESUME_VIDEO\"}";
                KitchenAssistantHandler.sendMessageToAll(message);
                return ResponseEntity.ok("Command to resume video sent.");
            default:
                return ResponseEntity.badRequest().body("Invalid command.");
        }
    }
}