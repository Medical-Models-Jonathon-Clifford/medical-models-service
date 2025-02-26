package org.jono.medicalmodelsservice.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ListUtilsTest {

    @ParameterizedTest
    @MethodSource("deduplicateStrings")
    void shouldHandleDuplicateStrings(List<String> inputList, List<String> expectedList) {
        List<String> noDuplicates = ListUtils.deduplicate(inputList);
        assertThat(noDuplicates).isEqualTo(expectedList);
    }

    @ParameterizedTest
    @MethodSource("deduplicateIntegers")
    void shouldHandleDuplicateIntegers(List<Integer> inputList, List<Integer> expectedList) {
        List<Integer> noDuplicates = ListUtils.deduplicate(inputList);
        assertThat(noDuplicates).isEqualTo(expectedList);
    }

    private static Stream<Arguments> deduplicateStrings() {
        return Stream.of(
                Arguments.of(List.of("a", "b", "a"), List.of("a", "b")),
                Arguments.of(List.of("a", "b", "a", "b"), List.of("a", "b")),
                Arguments.of(List.of("a", "a"), List.of("a")),
                Arguments.of(List.of("a", "b", "b"), List.of("a", "b")),
                Arguments.of(List.of("a", "A"), List.of("a", "A")),
                Arguments.of(List.of("A", "a"), List.of("A", "a")),
                Arguments.of(List.of("aa", "a"), List.of("aa", "a")),
                Arguments.of(List.of(), List.of())
                );
    }

    private static Stream<Arguments> deduplicateIntegers() {
        return Stream.of(
                Arguments.of(List.of(1, 2, 2), List.of(1, 2)),
                Arguments.of(List.of(1, 2), List.of(1, 2)),
                Arguments.of(List.of(1, 2, 1, 2), List.of(1, 2)),
                Arguments.of(List.of(1, 2, 1), List.of(1, 2)),
                Arguments.of(List.of(1, 2, 3), List.of(1, 2, 3)),
                Arguments.of(List.of(1, 2, 2, 3), List.of(1, 2, 3)),
                Arguments.of(List.of(1), List.of(1)),
                Arguments.of(List.of(), List.of())
                );
    }
}