package com.ruraledu.service.extractor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link YoutubeExtractor}.
 */
public class YoutubeExtractorTest {

    private YoutubeExtractor extractor;

    /**
     * Sets up the test environment.
     */
    @BeforeEach
    public void setUp() {
        extractor = new YoutubeExtractor();
    }

    /**
     * Tests validation of empty or whitespace URLs.
     * @param url the URL to test
     */
    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    public void validateYoutubeUrl_EmptyOrWhitespace(String url) {
        YoutubeExtractor.ValidationResult result = extractor.validateYoutubeUrl(url);

        assertFalse(result.isValid());
        assertEquals("URL cannot be empty", result.getMessage());
        assertNull(result.getId());
        assertNull(result.getType());
    }

    /**
     * Tests validation of a null URL.
     */
    @Test
    public void validateYoutubeUrl_NullUrl() {
        YoutubeExtractor.ValidationResult result = extractor.validateYoutubeUrl(null);

        assertFalse(result.isValid());
        assertEquals("URL cannot be empty", result.getMessage());
        assertNull(result.getId());
        assertNull(result.getType());
    }

    /**
     * Tests validation of valid playlist URLs.
     * @param url the URL to test
     */
    @ParameterizedTest
    @ValueSource(strings = {
        "https://www.youtube.com/playlist?list=PLTjRvDozrdlx82aIrsHY_Ndr3mKGK4tgq",
        "https://youtube.com/playlist?list=PLTjRvDozrdlx82aIrsHY_Ndr3mKGK4tgq",
        "https://www.youtube.com/watch?v=VIDEO_ID&list=PLTjRvDozrdlx82aIrsHY_Ndr3mKGK4tgq",
        "youtube.com/playlist?list=PLTjRvDozrdlx82aIrsHY_Ndr3mKGK4tgq"
    })
    public void validateYoutubeUrl_ValidPlaylistUrls(String url) {
        YoutubeExtractor.ValidationResult result = extractor.validateYoutubeUrl(url);

        assertTrue(result.isValid());
        assertEquals("Valid playlist URL", result.getMessage());
        assertEquals("PLTjRvDozrdlx82aIrsHY_Ndr3mKGK4tgq", result.getId());
        assertEquals("playlist", result.getType());
    }

    /**
     * Tests validation of invalid playlist URLs.
     * @param url the URL to test
     */
    @ParameterizedTest
    @ValueSource(strings = {
        "https://www.youtube.com/playlist", // missing list parameter
        "https://www.youtube.com/playlist?list=" // empty list parameter
    })
    public void validateYoutubeUrl_InvalidPlaylistUrls(String url) {
        YoutubeExtractor.ValidationResult result = extractor.validateYoutubeUrl(url);

        assertFalse(result.isValid());
        assertEquals("Invalid playlist URL format", result.getMessage());
        assertNull(result.getId());
        assertNull(result.getType());
    }

    /**
     * Tests validation of valid video URLs.
     * @param url the URL to test
     */
    @ParameterizedTest
    @ValueSource(strings = {
        "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
        "https://youtube.com/watch?v=dQw4w9WgXcQ",
        "https://youtu.be/dQw4w9WgXcQ",
        "https://www.youtube.com/embed/dQw4w9WgXcQ?autoplay=1"
    })
    public void validateYoutubeUrl_ValidVideoUrls(String url) {
        YoutubeExtractor.ValidationResult result = extractor.validateYoutubeUrl(url);

        assertTrue(result.isValid(), "URL should be valid: " + url);
        assertEquals("Valid video URL", result.getMessage());
        assertEquals("dQw4w9WgXcQ", result.getId());
        assertEquals("video", result.getType());
    }

    /**
     * Tests validation of invalid video URLs.
     * @param url the URL to test
     */
    @ParameterizedTest
    @ValueSource(strings = {
        "https://www.youtube.com/watch", // missing v parameter
        "https://www.youtube.com/watch?v=", // empty v parameter
        "https://youtu.be/" // empty short id
    })
    public void validateYoutubeUrl_InvalidVideoUrls(String url) {
        YoutubeExtractor.ValidationResult result = extractor.validateYoutubeUrl(url);

        assertFalse(result.isValid(), "URL should be invalid: " + url);
        assertEquals("Invalid video URL format", result.getMessage());
        assertNull(result.getId());
        assertNull(result.getType());
    }

    /**
     * Tests validation of unsupported URLs.
     */
    @Test
    public void validateYoutubeUrl_UnsupportedFormat() {
        YoutubeExtractor.ValidationResult result = extractor.validateYoutubeUrl("https://example.com/video");

        assertFalse(result.isValid());
        assertEquals("Unsupported YouTube URL format", result.getMessage());
        assertNull(result.getId());
        assertNull(result.getType());
    }
}
