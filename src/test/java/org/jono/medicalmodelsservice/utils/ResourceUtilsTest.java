package org.jono.medicalmodelsservice.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Base64;
import org.junit.jupiter.api.Test;

class ResourceUtilsTest {

    private static final String TEST_TEXT_FILE = "test-text-file.txt";
    private static final String EMPTY_FILE = "empty-file.txt";
    private static final String NON_EXISTENT_FILE = "non-existent-file.txt";
    private static final String TEST_FILE_CONTENT = "This is a test file content for ResourceUtils testing.";

    @Test
    void shouldLoadExistingResourceFile() throws IOException {
        final String base64Content = ResourceUtils.loadBase64ResourceFile(TEST_TEXT_FILE);

        assertNotNull(base64Content);
        assertFalse(base64Content.isEmpty());

        final String decodedContent = decodeBase64(base64Content);
        assertThat(decodedContent).isEqualTo(TEST_FILE_CONTENT);
    }

    private String decodeBase64(final String base64Content) {
        return new String(Base64.getDecoder().decode(base64Content));
    }

    @Test
    void shouldLoadEmptyResourceFile() throws IOException {
        final String base64Content = ResourceUtils.loadBase64ResourceFile(EMPTY_FILE);

        assertNotNull(base64Content);
        assertThat(base64Content).isEmpty();
    }

    @Test
    void shouldThrowWhenLoadingNonExistentResourceFile() {
        final Exception exception = assertThrows(
                IOException.class,
                () -> ResourceUtils.loadBase64ResourceFile(NON_EXISTENT_FILE)
        );

        assertThat(exception.getMessage()).isEqualTo("Resource not found: " + NON_EXISTENT_FILE);
    }
}
