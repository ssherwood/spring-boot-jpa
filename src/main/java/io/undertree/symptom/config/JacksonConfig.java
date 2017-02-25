package io.undertree.symptom.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Customizations to the default Spring Boot Jackson Object Mapper.
 *
 * @author Shawn Sherwood
 */
@Configuration
public class JacksonConfig {

  /**
   * The HibernateX modules are not auto registered with the Jackson Object
   * Mapper by default (not sure why not).  This module is useful in that it
   * helps make Jackson more Hibernate "aware" so as not to trigger lazy
   * loading by accident during JSON marshalling.
   *
   * @return customized with Hibernate 5 "aware" module
   */
  @Bean
  public Module hibernate5Module() {
    Hibernate5Module module = new Hibernate5Module();
    //module.enable(Hibernate5Module.Feature.FORCE_LAZY_LOADING);
    return module;
  }
}
