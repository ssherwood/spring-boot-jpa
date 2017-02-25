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
package io.undertree.symptom.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMappingJacksonResponseBodyAdvice;
import org.springframework.web.util.UriComponentsBuilder;

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

  public static final String QUERY_PARAM_PAGE = "page";
  public static final String PAGE_METADATA_FMT = "page-number=%d,page-size=%d,total-elements=%d,total-pages=%d,first-page=%b,last-page=%b";
  public static final String LINK_STANDARD_FMT = "<%s>; rel=\"%s\"";
  public static final String LINK_HEADER_FIRST = "first";
  public static final String LINK_HEADER_PREVIOUS = "prev";
  public static final String LINK_HEADER_NEXT = "next";
  public static final String LINK_HEADER_LAST = "last";
  public static final String CUSTOM_HEADER_META_PAGINATION = "X-Meta-Pagination";

  @Override
  public boolean supports(MethodParameter returnType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return super.supports(returnType, converterType) &&
        Page.class.isAssignableFrom(returnType.getParameterType());
  }

  @Override
  protected void beforeBodyWriteInternal(MappingJacksonValue bodyContainer,
      MediaType contentType, MethodParameter returnType,
      ServerHttpRequest request, ServerHttpResponse response) {

    Page<?> page = ((Page<?>)bodyContainer.getValue());

    response.getHeaders().add(CUSTOM_HEADER_META_PAGINATION,
        String.format(PAGE_METADATA_FMT, page.getNumber(), page.getSize(),
            page.getTotalElements(), page.getTotalPages(), page.isFirst(),
            page.isLast()));

    getHttpHeaderLinksString(request, page)
        .filter(StringUtils::isNotEmpty)
        .ifPresent(s -> response.getHeaders().add(HttpHeaders.LINK, s));

    // finally, strip out the actual content and replace it as the body value
    bodyContainer.setValue(page.getContent());
  }

  /*
   */
  private Optional<String> getHttpHeaderLinksString(ServerHttpRequest request, Page<?> page) {
    List<String> headerLinks = new ArrayList<>();

    if (!page.isFirst()) {
      headerLinks.add(String.format(LINK_STANDARD_FMT, UriComponentsBuilder.fromHttpRequest(request)
          .replaceQueryParam(QUERY_PARAM_PAGE, 0)
          .build(), LINK_HEADER_FIRST));
    }

    if (page.hasPrevious()) {
      headerLinks.add(String.format(LINK_STANDARD_FMT, UriComponentsBuilder.fromHttpRequest(request)
              .replaceQueryParam(QUERY_PARAM_PAGE, page.previousPageable().getPageNumber())
              .build(), LINK_HEADER_PREVIOUS));
    }

    if (page.hasNext()) {
      headerLinks.add(String.format(LINK_STANDARD_FMT, UriComponentsBuilder.fromHttpRequest(request)
              .replaceQueryParam(QUERY_PARAM_PAGE, page.nextPageable().getPageNumber())
              .build(), LINK_HEADER_NEXT));
    }

    if (!page.isLast()) {
      headerLinks.add(String.format(LINK_STANDARD_FMT, UriComponentsBuilder.fromHttpRequest(request)
          .replaceQueryParam(QUERY_PARAM_PAGE, page.getTotalPages() - 1)
          .build(), LINK_HEADER_LAST));
    }

    return Optional.of(StringUtils.join(headerLinks, ", "));
  }
}
