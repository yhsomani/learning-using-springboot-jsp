package com.ruraledu.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class YoutubeService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Fetches videos from a YouTube playlist using web scraping (no API key required).
     */
    public List<Map<String, Object>> fetchPlaylistVideos(@org.springframework.lang.NonNull String playlistId) {
        String url = "https://www.youtube.com/playlist?list=" + playlistId;
        List<Map<String, Object>> videos = new ArrayList<>();
        
        try {
            String html = restTemplate.getForObject(url, String.class);
            if (html == null) {
                return videos;
            }

            // Extract ytInitialData from the HTML source
            Pattern pattern = Pattern.compile("ytInitialData\\s*=\\s*(\\{.*?\\});");
            Matcher matcher = pattern.matcher(html);
            
            if (matcher.find()) {
                String jsonStr = matcher.group(1);
                JsonNode root = objectMapper.readTree(jsonStr);
                
                JsonNode videoListContents = findVideoList(root);

                if (videoListContents != null && videoListContents.isArray()) {
                    for (int i = 0; i < videoListContents.size(); i++) {
                        JsonNode videoRenderer = videoListContents.get(i).path("playlistVideoRenderer");
                        if (!videoRenderer.isMissingNode()) {
                            String videoId = videoRenderer.path("videoId").asText();
                            JsonNode titleNode = videoRenderer.path("title");
                            String title = "";
                            
                            if (titleNode.has("runs")) {
                                title = titleNode.path("runs").get(0).path("text").asText();
                            } else if (titleNode.has("simpleText")) {
                                title = titleNode.path("simpleText").asText();
                            }

                            String thumb = "https://i.ytimg.com/vi/" + videoId + "/mqdefault.jpg";
                            
                            if (videoId != null && !videoId.isEmpty() && !title.isEmpty() && !title.contains("Private video")) {
                                Map<String, Object> videoData = new HashMap<>();
                                videoData.put("videoId", videoId);
                                videoData.put("title", title.trim());
                                videoData.put("thumbnail", thumb);
                                videoData.put("orderIndex", i);
                                videos.add(videoData);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Silently fail or log as warning, matching expected behavior for scraper
        }

        return videos;
    }

    private JsonNode findVideoList(JsonNode root) {
        // Primary path for YouTube's current desktop layout
        JsonNode contents = root.path("contents")
            .path("twoColumnBrowseResultsRenderer")
            .path("tabs").get(0)
            .path("tabRenderer")
            .path("content")
            .path("sectionListRenderer")
            .path("contents").get(0)
            .path("itemSectionRenderer")
            .path("contents").get(0)
            .path("playlistVideoListRenderer")
            .path("contents");
        
        if (!contents.isMissingNode()) return contents;
        
        // Alternative path for certain UI variations
        JsonNode altContents = root.path("onResponseReceivedActions")
            .get(0)
            .path("appendContinuationItemsAction")
            .path("continuationItems");
            
        return altContents.isMissingNode() ? null : altContents;
    }

    public String extractPlaylistId(String url) {
        if (url == null) return null;
        if (url.contains("list=")) {
            String id = url.split("list=")[1];
            int ampersandIndex = id.indexOf("&");
            if (ampersandIndex != -1) {
                return id.substring(0, ampersandIndex);
            }
            return id;
        }
        return null;
    }
}
