package org.jono.medicalmodelsservice.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ListUtils {
    public static <T> List<T> deduplicate(List<T> withDuplicates) {
        return new ArrayList<>(new HashSet<>(withDuplicates));
    }
}
