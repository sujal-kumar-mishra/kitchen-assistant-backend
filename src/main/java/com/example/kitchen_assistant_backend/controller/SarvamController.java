package com.example.kitchen_assistant_backend.controller;

import com.example.kitchen_assistant_backend.service.SarvamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/sarvam")
public class SarvamController {

    private final SarvamService sarvam;

    @Autowired
    public SarvamController(SarvamService sarvam) { this.sarvam = sarvam; }

    @PostMapping("/stt")
    public ResponseEntity<?> speechToText(@RequestParam("audio") MultipartFile audio,
                                          @RequestParam(value="language", required=false) String language) {
        try {
            Map<String,Object> result = sarvam.transcribe(audio, "saarika:v2", language);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/tts")
    public ResponseEntity<?> textToSpeech(@RequestBody Map<String,String> body) {
        try {
            String text = body.get("text");
            String lang = body.getOrDefault("language","en-IN");
            byte[] audio = sarvam.synthesize(text, lang, body.getOrDefault("voice","meera"));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set("Content-Disposition","inline; filename=\"tts.wav\"");
            return new ResponseEntity<>(audio, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}