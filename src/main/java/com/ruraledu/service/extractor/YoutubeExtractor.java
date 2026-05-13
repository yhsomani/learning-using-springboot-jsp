package com.ruraledu.service.extractor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruraledu.dto.VideoMetadata;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Primary YouTube data extractor using multiple strategies.
 * Implements fallback architecture for reliability.
 */
@Component
public class YoutubeExtractor {

    private static final Logger logger = LoggerFactory.getLogger(YoutubeExtractor.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final String[] USER_AGENTS = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    };

    private static final Random random = new Random();

    /**
     * Extracts playlist videos using primary strategy with fallbacks.
     */
    public List<VideoMetadata> extractPlaylistVideos(String playlistId) {
        List<VideoMetadata> videos = new ArrayList<>();
        
        // Try primary strategy first
        try {
            videos = extractWithPrimaryStrategy(playlistId);
            if (!videos.isEmpty()) {
                logger.info("Successfully extracted {} videos from playlist {} using primary strategy", 
                        videos.size(), playlistId);
                return videos;
            }
        } catch (Exception e) {
            logger.warn("Primary strategy failed for playlist {}: {}", playlistId, e.getMessage());
        }

        // Fallback to Jsoup-based extraction
        try {
            videos = extractWithJsoupFallback(playlistId);
            if (!videos.isEmpty()) {
                logger.info("Successfully extracted {} videos from playlist {} using Jsoup fallback", 
                        videos.size(), playlistId);
                return videos;
            }
        } catch (Exception e) {
            logger.warn("Jsoup fallback failed for playlist {}: {}", playlistId, e.getMessage());
        }

        // Final fallback: Invidious instance
        try {
            videos = extractWithInvidiousFallback(playlistId);
            logger.info("Successfully extracted {} videos from playlist {} using Invidious fallback", 
                    videos.size(), playlistId);
        } catch (Exception e) {
            logger.error("All extraction strategies failed for playlist {}: {}", playlistId, e.getMessage());
        }

        return videos;
    }

    /**
     * Primary extraction strategy using HTTP client and JSON parsing.
     */
    private List<VideoMetadata> extractWithPrimaryStrategy(String playlistId) throws IOException, InterruptedException {
        List<VideoMetadata> videos = new ArrayList<>();
        String url = "https://www.youtube.com/playlist?list=" + playlistId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", getRandomUserAgent())
                .header("Accept-Language", "en-US,en;q=0.9")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            logger.warn("Received non-200 status code: {}", response.statusCode());
            throw new IOException("Failed to fetch playlist page: " + response.statusCode());
        }

        String html = response.body();
        
        // Check for error states
        if (html.contains("This playlist does not exist") || 
            html.contains("unavailable") || 
            html.contains("Private video")) {
            logger.warn("Playlist {} appears to be unavailable or private", playlistId);
            return videos;
        }

        // Extract ytInitialData JSON
        Pattern pattern = Pattern.compile("ytInitialData\\s*=\\s*(\\{.*?\\});", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        
        if (!matcher.find()) {
            logger.warn("Could not find ytInitialData in HTML");
            return videos;
        }

        String jsonStr = matcher.group(1);
        JsonNode root = objectMapper.readTree(jsonStr);
        
        // Navigate to video list
        JsonNode videoListContents = findVideoListContents(root);
        
        if (videoListContents == null || !videoListContents.isArray()) {
            logger.warn("Could not find video list contents in JSON");
            return videos;
        }

        int index = 0;
        for (JsonNode item : videoListContents) {
            JsonNode videoRenderer = item.path("playlistVideoRenderer");
            if (videoRenderer.isMissingNode()) {
                continue;
            }

            VideoMetadata metadata = parseVideoRenderer(videoRenderer, index++);
            if (metadata != null && metadata.isAvailable()) {
                videos.add(metadata);
            }
        }

        return videos;
    }

    /**
     * Fallback extraction using Jsoup for better HTML parsing.
     */
    private List<VideoMetadata> extractWithJsoupFallback(String playlistId) throws IOException {
        List<VideoMetadata> videos = new ArrayList<>();
        String url = "https://www.youtube.com/playlist?list=" + playlistId;

        Document doc = Jsoup.connect(url)
                .userAgent(getRandomUserAgent())
                .timeout(15000)
                .followRedirects(true)
                .get();

        // Look for video items in the HTML
        Elements videoElements = doc.select("ytd-playlist-video-renderer");
        
        int index = 0;
        for (Element videoEl : videoElements) {
            String videoId = videoEl.attr("data-video-id");
            if (videoId == null || videoId.isEmpty()) {
                // Try alternative selector
                Element linkEl = videoEl.selectFirst("a#video-title");
                if (linkEl != null) {
                    String href = linkEl.attr("href");
                    videoId = extractVideoIdFromUrl(href);
                }
            }

            if (videoId != null && !videoId.isEmpty()) {
                String title = videoEl.selectFirst("#video-title").text();
                if (title == null || title.isEmpty()) {
                    title = "Untitled Video";
                }

                // Skip private/deleted videos
                if (title.contains("[Private video]") || title.contains("[Deleted video]")) {
                    logger.debug("Skipping private/deleted video: {}", videoId);
                    continue;
                }

                VideoMetadata metadata = new VideoMetadata();
                metadata.setVideoId(videoId);
                metadata.setTitle(title.trim());
                metadata.setThumbnail("https://i.ytimg.com/vi/" + videoId + "/mqdefault.jpg");
                metadata.setOrderIndex(index++);
                metadata.setAvailable(true);
                videos.add(metadata);
            }
        }

        return videos;
    }

    /**
     * Final fallback using Invidious instance (privacy-friendly YouTube frontend).
     */
    private List<VideoMetadata> extractWithInvidiousFallback(String playlistId) throws IOException, InterruptedException {
        List<VideoMetadata> videos = new ArrayList<>();
        
        // Try multiple Invidious instances
        String[] instances = {
            "https://inv.tux.pizza",
            "https://invidious.io.lol",
            "https://yewtu.be"
        };

        for (String instance : instances) {
            try {
                String apiUrl = instance + "/api/v1/playlists/" + playlistId;
                
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .header("User-Agent", getRandomUserAgent())
                        .timeout(Duration.ofSeconds(10))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200) {
                    JsonNode playlist = objectMapper.readTree(response.body());
                    JsonNode videosArray = playlist.path("videos");
                    
                    int index = 0;
                    for (JsonNode videoNode : videosArray) {
                        VideoMetadata metadata = new VideoMetadata();
                        metadata.setVideoId(videoNode.path("videoId").asText(""));
                        metadata.setTitle(videoNode.path("title").asText("Untitled"));
                        metadata.setThumbnail(videoNode.path("videoThumbnails").path(0).path("url").asText(""));
                        metadata.setDuration(formatDuration(videoNode.path("lengthSeconds").asInt(0)));
                        metadata.setOrderIndex(index++);
                        metadata.setAvailable(true);
                        
                        if (!metadata.getVideoId().isEmpty()) {
                            videos.add(metadata);
                        }
                    }
                    
                    if (!videos.isEmpty()) {
                        logger.info("Successfully extracted from Invidious instance: {}", instance);
                        break;
                    }
                }
            } catch (Exception e) {
                logger.debug("Invidious instance {} failed: {}", instance, e.getMessage());
                videos.clear(); // Clear failed attempt
            }
        }

        return videos;
    }

    /**
     * Extracts metadata for a single video.
     */
    public VideoMetadata extractSingleVideo(String videoId) {
        VideoMetadata metadata = new VideoMetadata();
        metadata.setVideoId(videoId);
        
        try {
            String url = "https://www.youtube.com/watch?v=" + videoId;
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", getRandomUserAgent())
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                metadata.setAvailable(false);
                metadata.setDeleted(true);
                metadata.setErrorMessage("Video returned status code: " + response.statusCode());
                return metadata;
            }

            String html = response.body();
            
            // Check for availability issues
            if (html.contains("This video is private") || html.contains("private video")) {
                metadata.setAvailable(false);
                metadata.setPrivate(true);
                metadata.setErrorMessage("This video is private");
                return metadata;
            }
            
            if (html.contains("This video is no longer available") || 
                html.contains("deleted video") ||
                html.contains("unavailable")) {
                metadata.setAvailable(false);
                metadata.setDeleted(true);
                metadata.setErrorMessage("This video is no longer available");
                return metadata;
            }

            if (html.contains("age-restricted") || html.contains("age verification")) {
                metadata.setAvailable(false);
                metadata.setAgeRestricted(true);
                metadata.setErrorMessage("This video is age-restricted");
                return metadata;
            }

            // Extract metadata from JSON
            Pattern pattern = Pattern.compile("ytInitialPlayerResponse\\s*=\\s*(\\{.*?\\});", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(html);
            
            if (matcher.find()) {
                String jsonStr = matcher.group(1);
                JsonNode root = objectMapper.readTree(jsonStr);
                
                JsonNode videoDetails = root.path("videoDetails");
                if (!videoDetails.isMissingNode()) {
                    metadata.setTitle(videoDetails.path("title").asText("Untitled"));
                    metadata.setDescription(videoDetails.path("shortDescription").asText(""));
                    metadata.setThumbnail(videoDetails.path("thumbnail").path("thumbnails").path(0).path("url").asText(
                            "https://i.ytimg.com/vi/" + videoId + "/mqdefault.jpg"));
                    metadata.setDuration(formatDuration(videoDetails.path("lengthSeconds").asInt(0)));
                    metadata.setAvailable(true);
                } else {
                    // Fallback to og tags
                    metadata.setTitle(extractOgTag(html, "og:title"));
                    metadata.setDescription(extractOgTag(html, "og:description"));
                    metadata.setThumbnail(extractOgTag(html, "og:image"));
                    metadata.setAvailable(true);
                }
            } else {
                // Ultimate fallback to OG tags
                metadata.setTitle(extractOgTag(html, "og:title"));
                metadata.setDescription(extractOgTag(html, "og:description"));
                metadata.setThumbnail(extractOgTag(html, "og:image"));
                metadata.setAvailable(!metadata.getTitle().isEmpty());
            }

        } catch (Exception e) {
            logger.error("Error extracting video {}: {}", videoId, e.getMessage());
            metadata.setAvailable(false);
            metadata.setErrorMessage("Failed to extract video metadata: " + e.getMessage());
        }

        return metadata;
    }

    /**
     * Validates a YouTube URL and extracts the ID.
     */
    public ValidationResult validateYoutubeUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return new ValidationResult(false, "URL cannot be empty", null, null);
        }

        url = url.trim();
        
        // Check if it's a playlist URL
        if (url.contains("/playlist") || url.contains("list=")) {
            String playlistId = extractPlaylistId(url);
            if (playlistId == null || playlistId.isEmpty()) {
                return new ValidationResult(false, "Invalid playlist URL format", null, null);
            }
            return new ValidationResult(true, "Valid playlist URL", playlistId, "playlist");
        }
        
        // Check if it's a video URL
        if (url.contains("/watch") || url.contains("youtu.be")) {
            String videoId = extractVideoIdFromUrl(url);
            if (videoId == null || videoId.isEmpty()) {
                return new ValidationResult(false, "Invalid video URL format", null, null);
            }
            return new ValidationResult(true, "Valid video URL", videoId, "video");
        }

        return new ValidationResult(false, "Unsupported YouTube URL format", null, null);
    }

    /**
     * Checks if a video already exists in the system to prevent duplicates.
     */
    public boolean isDuplicateVideo(List<VideoMetadata> existingVideos, String videoId) {
        return existingVideos.stream()
                .anyMatch(v -> v.getVideoId().equals(videoId));
    }

    // Helper methods

    private JsonNode findVideoListContents(JsonNode root) {
        // Primary path - desktop layout
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

        if (!contents.isMissingNode()) {
            return contents;
        }

        // Alternative path - mobile/app layout
        JsonNode altContents = root.path("onResponseReceivedActions")
                .get(0)
                .path("appendContinuationItemsAction")
                .path("continuationItems");

        if (!altContents.isMissingNode()) {
            return altContents;
        }

        // Another alternative path
        JsonNode onResponseReceivedEndpoints = root.path("onResponseReceivedEndpoints")
                .get(0)
                .path("appendContinuationItemsAction")
                .path("continuationItems");

        return onResponseReceivedEndpoints.isMissingNode() ? null : onResponseReceivedEndpoints;
    }

    private VideoMetadata parseVideoRenderer(JsonNode renderer, int index) {
        String videoId = renderer.path("videoId").asText("");
        if (videoId.isEmpty()) {
            return null;
        }

        JsonNode titleNode = renderer.path("title");
        String title = "";
        
        if (titleNode.has("runs")) {
            title = titleNode.path("runs").get(0).path("text").asText("");
        } else if (titleNode.has("simpleText")) {
            title = titleNode.path("simpleText").asText("");
        }

        // Skip private/deleted videos
        if (title.contains("[Private video]") || title.contains("[Deleted video]")) {
            logger.debug("Skipping private/deleted video during parsing: {}", videoId);
            VideoMetadata metadata = new VideoMetadata();
            metadata.setVideoId(videoId);
            metadata.setTitle(title);
            metadata.setAvailable(false);
            metadata.setDeleted(title.contains("[Deleted video]"));
            metadata.setPrivate(title.contains("[Private video]"));
            return metadata;
        }

        VideoMetadata metadata = new VideoMetadata();
        metadata.setVideoId(videoId);
        metadata.setTitle(title.trim());
        metadata.setThumbnail("https://i.ytimg.com/vi/" + videoId + "/mqdefault.jpg");
        metadata.setOrderIndex(index);
        metadata.setAvailable(true);

        // Extract duration if available
        JsonNode lengthText = renderer.path("lengthText");
        if (!lengthText.isMissingNode()) {
            metadata.setDuration(lengthText.asText(""));
        }

        return metadata;
    }

    private String extractVideoIdFromUrl(String url) {
        if (url == null) return null;
        
        // Handle youtu.be short URLs
        if (url.contains("youtu.be/")) {
            String[] parts = url.split("youtu.be/");
            if (parts.length > 1) {
                return parts[1].split("[?&]")[0];
            }
        }
        
        // Handle standard watch URLs
        if (url.contains("v=")) {
            String[] parts = url.split("v=");
            if (parts.length > 1) {
                return parts[1].split("&")[0];
            }
        }
        
        // Handle embed URLs
        if (url.contains("/embed/")) {
            String[] parts = url.split("/embed/");
            if (parts.length > 1) {
                return parts[1].split("[?\"/]")[0];
            }
        }

        return null;
    }

    private String extractPlaylistId(String url) {
        if (url == null || !url.contains("list=")) {
            return null;
        }
        
        String id = url.split("list=")[1];
        int ampersandIndex = id.indexOf("&");
        if (ampersandIndex != -1) {
            id = id.substring(0, ampersandIndex);
        }
        
        // Remove any trailing slashes or quotes
        id = id.replaceAll("[/\"']+$", "");
        
        return id.isEmpty() ? null : id;
    }

    private String extractOgTag(String html, String tagName) {
        Pattern pattern = Pattern.compile(
                "<meta[^>]*property=[\"']" + tagName + "[\"'][^>]*content=[\"']([^\"']*)[\"']",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        return matcher.find() ? matcher.group(1) : "";
    }

    private String formatDuration(int seconds) {
        if (seconds <= 0) return "";
        
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format("%d:%02d", minutes, secs);
        }
    }

    private String getRandomUserAgent() {
        return USER_AGENTS[random.nextInt(USER_AGENTS.length)];
    }

    /**
     * Result of URL validation.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        private final String extractedId;
        private final String type;

        public ValidationResult(boolean valid, String message, String extractedId, String type) {
            this.valid = valid;
            this.message = message;
            this.extractedId = extractedId;
            this.type = type;
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
        public String getExtractedId() { return extractedId; }
        public String getType() { return type; }
    }
}
