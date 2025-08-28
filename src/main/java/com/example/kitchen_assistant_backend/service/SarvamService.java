package com.example.kitchen_assistant_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;

@Service
public class SarvamService {

    @Value("${sarvam.api.key:}")
    private String sarvamApiKey;

    // Base endpoints (Sarvam docs: REST endpoints under https://api.sarvam.ai)
    private static final String STT_URL = "https://api.sarvam.ai/speech-to-text";
    private static final String STT_TRANSLATE_URL = "https://api.sarvam.ai/speech-to-text-translate";
    private static final String TTS_URL = "https://api.sarvam.ai/text-to-speech";

    private final RestTemplate rest = new RestTemplate();

    private HttpHeaders baseHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("api-subscription-key", sarvamApiKey); // per Sarvam auth docs
        return headers;
    }

    /**
     * Transcribe multipart audio (short audio). Returns JSON body as Map.
     */
    public Map<String,Object> transcribe(MultipartFile audioFile, String model, String languageCode) throws Exception {
        HttpHeaders headers = baseHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        org.springframework.util.LinkedMultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();
        body.add("model", model == null ? "saarika:v2" : model);
        body.add("language_code", languageCode == null ? "unknown" : languageCode);
        // Add file as byte[] Resource
        org.springframework.core.io.ByteArrayResource res = new org.springframework.core.io.ByteArrayResource(audioFile.getBytes()){
            @Override
            public String getFilename(){ return audioFile.getOriginalFilename(); }
        };
        body.add("audio", res);

        HttpEntity<org.springframework.util.MultiValueMap<String,Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = rest.postForEntity(STT_URL, request, Map.class);
        return response.getBody();
    }

    /**
     * Text to Speech: send text and target language, returns raw audio bytes (wave/mp3)
     * The Sarvam API returns audio binary — we call it and return bytes to caller.
     */
    public byte[] synthesize(String text, String targetLanguageCode, String voice) throws Exception {
        HttpHeaders headers = baseHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String,Object> payload = Map.of(
                "text", text,
                "target_language_code", targetLanguageCode == null ? "en-IN" : targetLanguageCode,
                "voice", voice == null ? "meera" : voice
        );

        HttpEntity<Map<String,Object>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<byte[]> response = rest.exchange(TTS_URL, HttpMethod.POST, request, byte[].class);
        return response.getBody();
    }
}