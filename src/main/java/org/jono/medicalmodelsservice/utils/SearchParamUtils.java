package org.jono.medicalmodelsservice.utils;

import java.util.Objects;

public class SearchParamUtils {

    private SearchParamUtils() {
        // Utility class
    }

    public static boolean notSet(final String param) {
        return Objects.isNull(param) || param.isBlank();
    }

    public static boolean isSet(final String param) {
        return Objects.nonNull(param) && !param.isBlank();
    }
}
