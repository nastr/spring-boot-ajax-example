package com.nastrsoft.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Type {
    DB("DB"),
    PATCH("PATCH");

    @Getter
    private String value;
}
