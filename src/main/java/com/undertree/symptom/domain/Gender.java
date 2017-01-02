/*
 * Copyright 2016-2017 Shawn Sherwood
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.undertree.symptom.domain;

import com.fasterxml.jackson.annotation.JsonValue;

// https://en.wikipedia.org/wiki/ISO/IEC_5218
public enum Gender {
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
