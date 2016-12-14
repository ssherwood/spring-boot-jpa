package com.undertree.symptom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestAttributes;

import java.util.Map;

@SpringBootApplication
public class BootJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootJpaApplication.class, args);
	}

	@Bean
	public ErrorAttributes errorAttributes() {
		return new DefaultErrorAttributes() {

			@Override
			public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {
				Map<String, Object> attributes =  super.getErrorAttributes(requestAttributes, includeStackTrace);
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
}
