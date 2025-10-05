package org.jono.medicalmodelsservice.utils;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import org.jono.medicalmodelsservice.model.User;

public final class UserAdapters {

    private UserAdapters() {
        // Utility class
    }

    public static Map<String, User> createIdToUserMap(final List<User> users) {
        return users.stream()
                .map(user -> Map.entry(user.getId(), user))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
