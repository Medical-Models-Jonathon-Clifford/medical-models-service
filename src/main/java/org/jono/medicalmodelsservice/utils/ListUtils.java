package org.jono.medicalmodelsservice.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class ListUtils {

    private ListUtils() {
        // Utility class
    }

    public static <T> List<T> deduplicate(final List<T> withDuplicates) {
        return new ArrayList<>(new HashSet<>(withDuplicates));
    }

    public static <T> Map<String, T> listToMapOfIdToItem(final List<T> list, final Function<T, String> idFn) {
        if (Objects.isNull(list)) {
            throw new IllegalArgumentException("Input list must not be null");
        }
        final Map<String, T> map = new HashMap<>();
        for (T item : list) {
            final String mapKey = idFn.apply(item);
            if (Objects.isNull(mapKey)) {
                throw new IllegalArgumentException("Map key must not be null");
            }
            map.put(mapKey, item);
        }
        return map;
    }
}
