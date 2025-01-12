package org.jono.medicalmodelsservice.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ListUtils {
    public static <T> List<T> deduplicate(List<T> withDuplicates) {
        return new ArrayList<>(new HashSet<>(withDuplicates));
    }

    public static <T> Map<String, T> listToMapFn(List<T> list, Function<T, String> idFn) {
        Map<String, T> map = new HashMap<>();
        for (T item : list) {
            map.put(idFn.apply(item), item);
        }
        return map;
    }
}
