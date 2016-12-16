package com.undertree.symptom.domain;

import com.fasterxml.jackson.annotation.JsonValue;

// https://en.wikipedia.org/wiki/ISO/IEC_5218
public enum  Gender {
    NOT_KNOWN(0),
    MALE(1),
    FEMALE(2),
    NOT_APPLICABLE(9);

    private final int code;

    Gender(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}
