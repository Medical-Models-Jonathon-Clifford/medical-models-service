package org.jono.medicalmodelsservice.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.jono.medicalmodelsservice.model.Comment;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ListUtilsTest {

    private static final LocalDateTime TEST_DATE = LocalDateTime.of(2024, Month.AUGUST, 3, 4, 23);

    @Nested
    class DeduplicateTests {
        @ParameterizedTest
        @MethodSource("deduplicateStrings")
        void duplicateStrings(final List<String> inputList, final List<String> expectedList) {
            final List<String> noDuplicates = ListUtils.deduplicate(inputList);
            assertThat(noDuplicates).isEqualTo(expectedList);
        }

        @ParameterizedTest
        @MethodSource("deduplicateIntegers")
        void duplicateIntegers(final List<Integer> inputList, final List<Integer> expectedList) {
            final List<Integer> noDuplicates = ListUtils.deduplicate(inputList);
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

    @Nested
    class ListToMapOfIdToItemTests {
        public static final Comment TEST_COMMENT_1 = new Comment("1", "100", "1001", "test body", TEST_DATE, TEST_DATE);
        public static final Comment TEST_COMMENT_2 = new Comment("2", "100", "1001", "test body", TEST_DATE, TEST_DATE);

        @Test
        public void listOfLength0() {
            final Map<String, Comment> map = ListUtils.listToMapOfIdToItem(List.of(), Comment::getId);

            assertThat(map.size()).isEqualTo(0);
        }

        @Test
        public void listOfLength1() {
            final Map<String, Comment> map = ListUtils.listToMapOfIdToItem(List.of(TEST_COMMENT_1), Comment::getId);

            assertThat(map.size()).isEqualTo(1);
            assertThat(map.get("1")).isEqualTo(TEST_COMMENT_1);
        }

        @Test
        public void listOfLength2() {
            final Map<String, Comment> map = ListUtils.listToMapOfIdToItem(List.of(TEST_COMMENT_1, TEST_COMMENT_2),
                                                                           Comment::getId);

            assertThat(map.size()).isEqualTo(2);
            assertThat(map.get("1")).isEqualTo(TEST_COMMENT_1);
            assertThat(map.get("2")).isEqualTo(TEST_COMMENT_2);
        }

        @Test
        public void nullList() {
            assertThatThrownBy(() -> ListUtils.listToMapOfIdToItem(null, Comment::getId))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void nullId() {
            final Comment nullIdComment = new Comment(null, "100", "1001", "test body", TEST_DATE, TEST_DATE);

            assertThatThrownBy(() -> ListUtils.listToMapOfIdToItem(List.of(nullIdComment), Comment::getId))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void idFunctionReturnsNull() {
            assertThatThrownBy(() -> ListUtils.listToMapOfIdToItem(List.of(TEST_COMMENT_1), comment -> null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
