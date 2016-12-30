package com.undertree.symptom.domain;

import com.fasterxml.jackson.annotation.JsonValue;

// https://en.wikipedia.org/wiki/ISO/IEC_5218
public enum  Gender {
    NOT_KNOWN(0, "Not Known"),
    MALE(1, "Male"),
    FEMALE(2, "Female"),
    NOT_APPLICABLE(9, "Not Applicable");

    private final int code;
    private final String jsonValue;

    Gender(int code, String jsonValue) {
        this.code = code;
        this.jsonValue = jsonValue;
    }

    public int getCode() {
        return code;
    }

    @JsonValue
    public String toJson() {
        return jsonValue;
    }
}
