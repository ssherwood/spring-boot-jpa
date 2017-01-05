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
package com.undertree.symptom;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestAttributes;

/**
 * Basic Spring Boot Application originally generated from the Spring Initializr.
 */
@SpringBootApplication
public class BootJpaApplication {

  /**
   * Default application main to bootstrap the Spring Boot application container.
   *
   * @param args default command line args
   */
  public static void main(final String[] args) {
    SpringApplication.run(BootJpaApplication.class, args);
  }

  // TODO move this to a more appropriate config class
  @Bean
  public ErrorAttributes errorAttributes() {
    return new DefaultErrorAttributes() {

      @Override
      public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes,
          boolean includeStackTrace) {
        Map<String, Object> attributes = super
            .getErrorAttributes(requestAttributes, includeStackTrace);
        Throwable error = getError(requestAttributes);
        if (error instanceof MethodArgumentNotValidException) {
          MethodArgumentNotValidException ex = ((MethodArgumentNotValidException) error);
          // todo need to find a cleaner way of handling these error messages
          attributes.put("errors", ex.getMessage());
        }
        return attributes;
      }
    };
  }

  // TODO move this to a more appropriate config class
  // this module is not auto-registered with the Jackson Object Mapper by default
  // this causes Jackson to be Hibernate "aware"
  // this is useful to keep the Hibernate object graph from being auto-populated even for "lazy"
  // collections (meaning you now have to navigate to the lazy collection with actual code
  // before triggering the lazy fetch)
  @Bean
  public Module hibernateModule() {
    Hibernate5Module module = new Hibernate5Module();
    //module.enable(Hibernate5Module.Feature.FORCE_LAZY_LOADING);
    return module;
  }
}
