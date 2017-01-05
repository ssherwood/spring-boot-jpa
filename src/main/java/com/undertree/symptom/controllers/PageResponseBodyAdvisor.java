/*
 * Copyright 2017 Shawn Sherwood
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
package com.undertree.symptom.controllers;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMappingJacksonResponseBodyAdvice;

/**
 * Used to customize the response body prior to JSON marshalling in Jackson.  In this case by
 * intercepting responses of type Page<?> type which indicate a collection response that is
 * currently contained in a Page object's content.
 *
 * This Advisor unwraps the Page container and allows the raw collection to be marshalled but adds
 * a custom HTTP header with the original page metadata.
 */
@ControllerAdvice
public class PageResponseBodyAdvisor extends AbstractMappingJacksonResponseBodyAdvice {

  public static final String PAGE_METADATA_FMT = "page-number=%d,page-size=%d,total-elements=%d,total-pages=%d,first-page=%b,last-page=%b";

  @Override
  public boolean supports(MethodParameter returnType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return super.supports(returnType, converterType) &&
        Page.class.isAssignableFrom(returnType.getParameterType());
  }

  @Override
  protected void beforeBodyWriteInternal(MappingJacksonValue bodyContainer, MediaType contentType,
      MethodParameter returnType, ServerHttpRequest request, ServerHttpResponse response) {

    Page<?> page = ((Page<?>)bodyContainer.getValue());

    response.getHeaders().add("X-Meta-Pagination",
        String.format(PAGE_METADATA_FMT, page.getNumber(), page.getSize(), page.getTotalElements(),
            page.getTotalPages(), page.isFirst(), page.isLast()));

    if (page.hasPrevious()) {
      response.getHeaders().add(HttpHeaders.LINK, "prev " + page.previousPageable());
    }

    if (page.hasNext()) {
      response.getHeaders().add(HttpHeaders.LINK, "next " + page.nextPageable());
    }

    // finally, strip out the actual content and replace it as the body value
    bodyContainer.setValue(page.getContent());
  }
}
