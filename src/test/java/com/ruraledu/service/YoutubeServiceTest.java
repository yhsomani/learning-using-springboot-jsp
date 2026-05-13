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
import com.ruraledu.dto.VideoMetadata;

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
        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(youtubeService, "restTemplate");
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void testFetchPlaylistVideosScraperSuccess() {
        String playlistId = "TEST_PLAYLIST_ID";

        // Mock the YouTube playlist page call to return dummy HTML
        String mockHtml = "<html><body><script>var ytInitialData = {\"contents\":{\"twoColumnBrowseResultsRenderer\":{\"tabs\":[{\"tabRenderer\":{\"content\":{\"sectionListRenderer\":{\"contents\":[{\"itemSectionRenderer\":{\"contents\":[{\"playlistVideoListRenderer\":{\"contents\":[{\"playlistVideoRenderer\":{\"videoId\":\"vid1\",\"title\":{\"runs\":[{\"text\":\"Test Video 1\"}]}}}]}}]}}]}}}}]}}};</script></body></html>";

        mockServer.expect(requestTo(startsWith("https://www.youtube.com/playlist?list=" + playlistId)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockHtml, MediaType.TEXT_HTML));

        // Execute
        List<VideoMetadata> result = youtubeService.fetchPlaylistVideos(playlistId);

        // Verify that the mock server was called as expected
        mockServer.verify();

        // Check the results
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("vid1", result.get(0).getVideoId());
        assertEquals("Test Video 1", result.get(0).getTitle());
    }

    @Test
    public void testExtractPlaylistId() {
        String url = "https://www.youtube.com/playlist?list=PLTjRvDozrdlx82aIrsHY_Ndr3mKGK4tgq";
        String id = youtubeService.extractPlaylistId(url);
        assertEquals("PLTjRvDozrdlx82aIrsHY_Ndr3mKGK4tgq", id);

        String urlWithParams = "https://www.youtube.com/playlist?list=PLTjRvDozrdlx82aIrsHY_Ndr3mKGK4tgq&si=T8ZR-lClNPZP0LbZ";
        String idWithParams = youtubeService.extractPlaylistId(urlWithParams);
        assertEquals("PLTjRvDozrdlx82aIrsHY_Ndr3mKGK4tgq", idWithParams);
    }
}
