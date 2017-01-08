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
package com.undertree.symptom.repositories;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.undertree.symptom.domain.Patient;
import com.undertree.symptom.domain.QPatient;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

/**
 * Spring Data JPA Repository for the Patient entity with QueryDsl support.
 */
public interface PatientRepository extends JpaRepository<Patient, Long>,
    QueryDslPredicateExecutor<Patient>, QuerydslBinderCustomizer<QPatient> {

  Optional<Patient> findByPatientId(UUID patientId);

  @Override
  default void customize(QuerydslBindings bindings, QPatient root) {
    bindings.excluding(root.id);
    //bindings.excluding(root.patientId);
    bindings.bind(String.class)
        .first((StringPath path, String value) ->
            path.containsIgnoreCase(value));
  }

  /**
   * QueryDsl Predicates for Patient.
   */
  class Predicates {
    static final QPatient $ = QPatient.patient;

    private Predicates() {
    }

    // match string being contained on any "name" field
    public static BooleanExpression hasAnyNameContaining(final String name) {
      return $.familyName.containsIgnoreCase(name)
          .or($.givenName.containsIgnoreCase(name)
              .or($.additionalName.containsIgnoreCase(name)));
    }

    public static BooleanExpression hasBirthdayOn(final LocalDate date) {
      return $.birthDate.month().eq(date.getMonthValue()).and(
          $.birthDate.dayOfMonth().eq(date.getDayOfMonth()));
    }
  }
}
