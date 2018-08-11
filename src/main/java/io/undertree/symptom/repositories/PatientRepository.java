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

package io.undertree.symptom.repositories;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import io.undertree.symptom.domain.Patient;
import io.undertree.symptom.domain.QPatient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for the Patient entity with QueryDsl support.
 *
 * @author Shawn Sherwood
 */
public interface PatientRepository extends JpaRepository<Patient, Long>,
		QuerydslPredicateExecutor<Patient>, QuerydslBinderCustomizer<QPatient> {

	/**
	 * Standard reference on QueryDsl QPatient.
	 * (FYI - I got the $ idea from
	 * http://blog.mysema.com/2011/08/querydsl-in-wild.html
	 * and I'm still not sure I'm okay with it).
	 */
	QPatient $ = QPatient.patient;

	/**
	 * Constructs a BooleanExpression for matching on a String with any name
	 * the Patient has (ignoring case).
	 *
	 * @param name String to match on
	 * @return Expression combining all Patient name fields
	 */
	static BooleanExpression hasAnyNameContaining(final String name) {
		return $.familyName.containsIgnoreCase(name).or(
				$.givenName.givenName.containsIgnoreCase(name).or(
						$.additionalName.containsIgnoreCase(name)));
	}

	/**
	 * A BooleanExpression for matching an Entity with a specific Month and
	 * Day of any give year to test for a birth day.  Specifically, this
	 * ignores the given year as a wider birthday test.
	 *
	 * @param date a given LocalDate to mach with
	 * @return Expression to match on the month/day for a possible birth day
	 */
	static BooleanExpression hasBirthdayOn(final LocalDate date) {
		return $.birthDate.month().eq(date.getMonthValue()).and(
				$.birthDate.dayOfMonth().eq(date.getDayOfMonth()));
	}

	/**
	 * Return an Optional instance of a Patient with the given UUID.  Optional
	 * helps clean up some of the uglier null checking due to the fact that the
	 * lower level code returns null when no match is found.
	 *
	 * @param patientId the unique UUID of the patient to return
	 * @return a Patient with the UUID provided
	 */
	//@Cacheable(value = "patients", key = "#patientId.toString()")
	Optional<Patient> findByPatientId(UUID patientId);

	/**
	 * Override default QueryDsl bindings.
	 *
	 * @param bindings a QueryDslBindings to use
	 * @param root the QPatient root
	 */
	@Override
	default void customize(QuerydslBindings bindings, QPatient root) {
		bindings.excluding(root.id);
		//bindings.excluding(root.patientId);
		bindings.bind(String.class)
				.first((StringPath path, String value) ->
						path.containsIgnoreCase(value));
	}
}
