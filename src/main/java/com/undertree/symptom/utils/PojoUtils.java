package com.undertree.symptom.utils;

import java.beans.FeatureDescriptor;
import java.util.stream.Stream;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 *
 */
public class PojoUtils {
  private PojoUtils() {
  }

  /**
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
