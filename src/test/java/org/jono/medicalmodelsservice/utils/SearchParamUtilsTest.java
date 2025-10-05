package org.jono.medicalmodelsservice.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SearchParamUtilsTest {

    @Test
    void shouldReturnTrueWhenParamIsSet() {
        assertTrue(SearchParamUtils.isSet("test"));
    }

    @Test
    void shouldReturnFalseWhenParamIsNull() {
        assertFalse(SearchParamUtils.isSet(null));
    }

    @Test
    void shouldReturnFalseWhenParamIsAnEmptyString() {
        assertFalse(SearchParamUtils.isSet(""));
    }

    @ParameterizedTest
    @MethodSource("blankParamValues")
    void shouldReturnFalseWhenParamIsJustWhitespace(final String paramValue) {
        assertFalse(SearchParamUtils.isSet(paramValue));
    }

    private static Stream<Arguments> blankParamValues() {
        return Stream.of(
                Arguments.argumentSet("Empty", ""),
                Arguments.argumentSet("Space", " "),
                Arguments.argumentSet("Newline", "\n"),
                Arguments.argumentSet("Tab", "\t")
        );
    }
}