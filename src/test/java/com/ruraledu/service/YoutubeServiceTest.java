package com.ruraledu.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.hamcrest.Matchers.startsWith;

public class YoutubeServiceTest {

    private YoutubeService youtubeService;
    private MockRestServiceServer mockServer;

    @BeforeEach
    public void setUp() {
        youtubeService = new YoutubeService();
        ReflectionTestUtils.setField(youtubeService, "configApiKey", "TEST_API_KEY");
        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(youtubeService, "restTemplate");
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void testFetchPlaylistVideosApiErrorTriggersFallback() {
        String playlistId = "TEST_PLAYLIST_ID";

        // Mock the Google API call to return a 500 Internal Server Error
        mockServer.expect(requestTo(startsWith("https://www.googleapis.com/youtube/v3/playlistItems")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        // Mock the fallback YouTube playlist page call to return dummy HTML
        String mockHtml = "<html><body><script>var ytInitialData = {\"contents\":{\"twoColumnBrowseResultsRenderer\":{\"tabs\":[{\"tabRenderer\":{\"content\":{\"sectionListRenderer\":{\"contents\":[{\"itemSectionRenderer\":{\"contents\":[{\"playlistVideoListRenderer\":{\"contents\":[{\"playlistVideoRenderer\":{\"videoId\":\"vid1\",\"title\":{\"runs\":[{\"text\":\"Test Video 1\"}]}}}]}}]}}]}}}}]}}};</script></body></html>";

        mockServer.expect(requestTo(startsWith("https://www.youtube.com/playlist?list=" + playlistId)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockHtml, MediaType.TEXT_HTML));

        // Execute
        List<Map<String, Object>> result = youtubeService.fetchPlaylistVideos(playlistId, null);

        // Verify that the mock server was called as expected
        mockServer.verify();

        // Check the results (from the fallback)
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("vid1", result.get(0).get("videoId"));
        assertEquals("Test Video 1", result.get(0).get("title"));
    }
}
