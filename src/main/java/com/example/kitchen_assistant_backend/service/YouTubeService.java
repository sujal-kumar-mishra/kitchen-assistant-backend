package com.example.kitchen_assistant_backend.service;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import com.example.kitchen_assistant_backend.model.VideoResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class YouTubeService {

    @Value("${youtube.api.key}")
    private String apiKey;

    private final YouTube youtube;

    public YouTubeService() {
        this.youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request -> {})
                .setApplicationName("kitchen-assistant")
                .build();
    }

    // In YouTubeService.java
    public List<VideoResult> searchVideos(String query) {
        try {
            // Correctly pass "id,snippet" as a single String
            YouTube.Search.List search = youtube.search().list("id,snippet");

            search.setKey(apiKey);
            search.setQ(query);

            // Correctly pass "video" as a single String
            search.setType("video");

            search.setMaxResults(5L);

            List<SearchResult> searchResults = search.execute().getItems();

            return searchResults.stream()
                    .map(result -> new VideoResult(
                            result.getId().getVideoId(),
                            result.getSnippet().getTitle(),
                            result.getSnippet().getThumbnails().getDefault().getUrl()
                    ))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}