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

import java.util.Objects;

import javax.persistence.Embeddable;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * https://stackoverflow.com/questions/20958/list-of-standard-lengths-for-database-fields.
 *
 * having issues with jsoncreator working.
 *
 * @author Shawn Sherwood
 */
@Embeddable
public class GivenName {
	@Size(min = 2, max = 50)
	@Pattern(regexp = "^([a-zA-Z]+[,.]?[ ]?|[a-zA-Z]+['-]?)+$", message = "{Pattern.givenName}")
	private String givenName;

	public GivenName() {
	}

	@JsonCreator
	public GivenName(@JsonProperty("givenName") String givenName) {
		this.givenName = givenName;
	}

	@JsonValue
	public String getGivenName() {
		return this.givenName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof GivenName)) {
			return false;
		}

		GivenName that = (GivenName) o;
		return Objects.equals(this.getGivenName(), that.getGivenName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getGivenName());
	}
}
