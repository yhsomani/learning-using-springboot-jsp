package com.ruraledu.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.ArrayList;
import com.ruraledu.dto.VideoMetadata;
import com.ruraledu.service.extractor.YoutubeExtractor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class YoutubeServiceTest {

    @Mock
    private YoutubeExtractor youtubeExtractor;

    @InjectMocks
    private YoutubeService youtubeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFetchPlaylistVideosScraperSuccess() {
        String playlistId = "TEST_PLAYLIST_ID";

        List<VideoMetadata> mockVideos = new ArrayList<>();
        VideoMetadata video = new VideoMetadata();
        video.setVideoId("vid1");
        video.setTitle("Test Video 1");
        mockVideos.add(video);

        when(youtubeExtractor.extractPlaylistVideos(playlistId)).thenReturn(mockVideos);

        // Execute
        List<VideoMetadata> result = youtubeService.fetchPlaylistVideos(playlistId);

        // Verify that the extractor was called
        verify(youtubeExtractor, times(1)).extractPlaylistVideos(playlistId);

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
