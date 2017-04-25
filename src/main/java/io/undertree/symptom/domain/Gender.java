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

package io.undertree.symptom.domain;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represent Gender for Entities.
 *
 * See also https://en.wikipedia.org/wiki/ISO/IEC_5218.
 *
 * @author Shawn Sherwood
 */
public enum Gender {
	/** Not known. */
	NOT_KNOWN(0, "Not Known"),
	/** Male. */
	MALE(1, "Male"),
	/** Female. */
	FEMALE(2, "Female"),
	/** Not applicable. */
	NOT_APPLICABLE(9, "Not Applicable");

	private final int code;
	private final String jsonValue;

	Gender(int code, String jsonValue) {
		this.code = code;
		this.jsonValue = jsonValue;
	}

	public int getCode() {
		return this.code;
	}

	@JsonValue
	public String toJson() {
		return this.jsonValue;
	}
}
