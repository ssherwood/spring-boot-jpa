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

package io.undertree.symptom.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The request could not be completed due to a conflict with the current state
 * of the target resource. This code is used in situations where the user might
 * be able to resolve the conflict and resubmit the request.
 *
 * See HTTP Status <a href="https://httpstatuses.com/409>409</a>.
 *
 * @author Shawn Sherwood
 */
@ResponseStatus(code = HttpStatus.CONFLICT)
public class ConflictException extends HttpException {

	static final long serialVersionUID = 20170307L;

	public ConflictException() {
		super(HttpStatus.CONFLICT.getReasonPhrase());
	}

	public ConflictException(String resource, String message) {
		super(resource, message);
	}

	public ConflictException(String resource, String message, Throwable cause) {
		super(resource, message, cause);
	}
}
