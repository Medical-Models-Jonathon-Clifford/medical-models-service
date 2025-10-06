package org.jono.medicalmodelsservice.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SearchParamUtilsTest {

    @Nested
    @DisplayName("isSet")
    class IsSet {
        @Test
        void shouldReturnTrueWhenParamIsSet() {
            assertTrue(SearchParamUtils.isSet("test"));
        }

        @Test
        void shouldReturnFalseWhenParamIsNull() {
            assertFalse(SearchParamUtils.isSet(null));
        }

        @ParameterizedTest
        @MethodSource("blankParamValuesIsSet")
        void shouldReturnFalseWhenParamIsBlank(final String paramValue) {
            assertFalse(SearchParamUtils.isSet(paramValue));
        }

        private static Stream<Arguments> blankParamValuesIsSet() {
            return blankParamValues();
        }
    }

    @Nested
    @DisplayName("notSet")
    class NotSet {
        @Test
        void shouldReturnTrueWhenParamIsNull() {
            assertTrue(SearchParamUtils.notSet(null));
        }

        @Test
        void shouldReturnFalseWhenParamIsSet() {
            assertFalse(SearchParamUtils.notSet("test"));
        }

        @ParameterizedTest
        @MethodSource("blankParamValuesNotSet")
        void shouldReturnTrueWhenParamIsBlank(final String paramValue) {
            assertTrue(SearchParamUtils.notSet(paramValue));
        }

        private static Stream<Arguments> blankParamValuesNotSet() {
            return blankParamValues();
        }
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