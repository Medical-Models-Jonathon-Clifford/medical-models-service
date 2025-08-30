package org.jono.medicalmodelsservice.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class ResourceUtils {

    private ResourceUtils() {
        // Utility class
    }

    public static String loadBase64ResourceFile(final String resourcePath) throws IOException {
        try (final InputStream is = ResourceUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            final byte[] bytes = is.readAllBytes();
            return Base64.getEncoder().encodeToString(bytes);
        }
    }
}
