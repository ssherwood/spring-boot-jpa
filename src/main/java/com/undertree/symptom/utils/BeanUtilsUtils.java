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
package com.undertree.symptom.utils;

import java.beans.FeatureDescriptor;
import java.util.stream.Stream;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 *
 */
public class BeanUtilsUtils {

  private BeanUtilsUtils() {
  }

  /**
   * Returns an array of fields from the given object that are null.  Useful in conjunction with
   * BeanUtils.copyProperties() when you only want to copy the non-null values.
   *
   * @param source
   * @return
   */
  public static String[] getNullPropertyNames(final Object source) {
    final BeanWrapper wrappedSource = new BeanWrapperImpl(source);

    return Stream.of(wrappedSource.getPropertyDescriptors())
        .map(FeatureDescriptor::getName)
        .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
        .toArray(String[]::new);
  }
}
