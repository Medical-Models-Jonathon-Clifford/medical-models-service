package org.jono.medicalmodelsservice.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class ListUtils {
    public static <T> List<T> deduplicate(List<T> withDuplicates) {
        return new ArrayList<>(new HashSet<>(withDuplicates));
    }

    public static <T> Map<String, T> listToMapOfIdToItem(List<T> list, Function<T, String> idFn) {
        if (Objects.isNull(list)) {
            throw new IllegalArgumentException("Input list must not be null");
        }
        Map<String, T> map = new HashMap<>();
        for (T item : list) {
            String mapKey = idFn.apply(item);
            if (Objects.isNull(mapKey)) {
                throw new IllegalArgumentException("Map key must not be null");
            }
            map.put(mapKey, item);
        }
        return map;
    }
}
