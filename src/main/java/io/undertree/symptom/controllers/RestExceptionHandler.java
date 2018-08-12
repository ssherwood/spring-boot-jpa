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

package io.undertree.symptom.controllers;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * WIP.
 *
 * @author Shawn Sherwood
 */
// @RestControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler({MethodArgumentNotValidException.class})
	public void handleException(MethodArgumentNotValidException ex,
			WebRequest request, HttpServletResponse response) throws Exception {
		response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getBindingResult().getModel().toString());
	}

	/*
	public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                                                     WebRequest request, HttpServletResponse response) {
        // TODO needs work to figure out how to better map this for output
        int i = 0;
        Map<String, String> errorBlock = new HashMap<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            errorBlock.put(error.getCode() + i++, error.toString());
        }

        return new ResponseEntity<>(errorBlock, HttpStatus.BAD_REQUEST);
    }
    */
}
