package com.nastrsoft.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@AllArgsConstructor
public enum Type {
    DB("DB"),
    PATCH("PATCH");

    @Getter
    private String value;

    public static Type fromValue(final String t) {
        return Arrays.stream(Type.values())
            .filter(v -> StringUtils.containsIgnoreCase(t, v.value))
            .findFirst().orElse(null);
    }
}
