package com.ruraledu.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class YoutubeService {

    @Value("${youtube.api.key:YOUR_API_KEY}")
    private String configApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings({"unchecked", "null"})
    public List<Map<String, Object>> fetchPlaylistVideos(@org.springframework.lang.NonNull String playlistId, String customApiKey) {
        String finalKey = (customApiKey != null && !customApiKey.isEmpty()) ? customApiKey : configApiKey;
        
        if (finalKey == null || finalKey.isEmpty() || finalKey.equals("YOUR_API_KEY_HERE")) {
            return fetchPlaylistVideosNoKey(playlistId);
        }

        List<Map<String, Object>> videos = new ArrayList<>();
        String nextPageToken = "";
        
        try {
            do {
                String url = String.format(
                    "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&playlistId=%s&key=%s",
                    playlistId, finalKey
                );
                
                if (nextPageToken != null && !nextPageToken.isEmpty()) {
                    url += "&pageToken=" + nextPageToken;
                }

                Map<String, Object> response = restTemplate.getForObject(url, Map.class);
                if (response != null && response.containsKey("items")) {
                    List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
                    for (Map<String, Object> item : items) {
                        Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
                        Map<String, Object> resourceId = (Map<String, Object>) snippet.get("resourceId");
                        
                        Map<String, Object> videoData = new java.util.HashMap<>();
                        videoData.put("videoId", resourceId.get("videoId"));
                        videoData.put("title", snippet.get("title"));
                        
                        Map<String, Object> thumbnails = (Map<String, Object>) snippet.get("thumbnails");
                        if (thumbnails != null) {
                            Map<String, Object> medium = (Map<String, Object>) thumbnails.get("medium");
                            if (medium != null) {
                                videoData.put("thumbnail", medium.get("url"));
                            }
                        }
                        videoData.put("orderIndex", snippet.get("position"));
                        
                        videos.add(videoData);
                    }
                    nextPageToken = (String) response.get("nextPageToken");
                } else {
                    nextPageToken = null;
                }
            } while (nextPageToken != null && !nextPageToken.isEmpty());
        } catch (Exception e) {
            System.err.println("Error fetching YouTube playlist with key: " + e.getMessage());
            if (videos.isEmpty()) {
                return fetchPlaylistVideosNoKey(playlistId); // Fallback on total failure
            }
        }
        
        return videos;
    }

    @SuppressWarnings("null")
    private List<Map<String, Object>> fetchPlaylistVideosNoKey(@org.springframework.lang.NonNull String playlistId) {
        String url = "https://www.youtube.com/playlist?list=" + playlistId;
        List<Map<String, Object>> videos = new ArrayList<>();
        
        try {
            String html = restTemplate.getForObject(url, String.class);
            if (html == null) {
                System.err.println("Failed to fetch HTML for playlist: " + playlistId);
                return videos;
            }

            // More flexible regex for ytInitialData
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("ytInitialData\\s*=\\s*(\\{.*?\\});");
            java.util.regex.Matcher matcher = pattern.matcher(html);
            
            if (matcher.find()) {
                String jsonStr = matcher.group(1);
                // System.out.println("JSON found: " + jsonStr.substring(0, 100) + "...");
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(jsonStr);
                    
                    // Navigate through the complex JSON tree
                    JsonNode videoListContents = findVideoList(root);

                    if (videoListContents != null && videoListContents.isArray()) {
                        for (int i = 0; i < videoListContents.size(); i++) {
                            JsonNode videoRenderer = videoListContents.get(i).path("playlistVideoRenderer");
                            if (!videoRenderer.isMissingNode()) {
                                String videoId = videoRenderer.path("videoId").asText();
                                String title = videoRenderer.path("title").path("runs").get(0).path("text").asText();
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
                } catch (Exception e) {
                    System.err.println("Jackson parsing failed: " + e.getMessage());
                }
            } else {
                System.err.println("ytInitialData not found in HTML");
            }
        } catch (Exception e) {
            System.err.println("Fallback scraper failed: " + e.getMessage());
        }

        if (videos.isEmpty()) {
            System.err.println("WARNING: No videos could be extracted for playlist: " + playlistId + ". Ensure the playlist is public and accessible.");
        }
        return videos;
    }

    private JsonNode findVideoList(JsonNode root) {
        // Try multiple common paths as YouTube UI is dynamic
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
        
        // Alternative path
        return root.path("sidebar")
            .path("playlistSidebarRenderer")
            .path("items"); // This might be wrong, but just an example of trying multiple paths
    }

    private String extractValue(String source, String key) {
        int start = source.indexOf(key);
        if (start == -1) return null;
        start += key.length();
        int end = source.indexOf("\"", start);
        if (end == -1) return null;
        return source.substring(start, end);
    }

    public String extractPlaylistId(String url) {
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
