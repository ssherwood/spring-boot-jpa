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
package io.undertree.symptom.repositories;

import com.querydsl.core.types.dsl.StringPath;
import io.undertree.symptom.domain.Medication;
import io.undertree.symptom.domain.QMedication;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

/**
 */
public interface MedicationRepository extends JpaRepository<Medication, Long>,
		QueryDslPredicateExecutor<Medication>, QuerydslBinderCustomizer<QMedication> {

	@Override
	default void customize(QuerydslBindings bindings, QMedication root) {
		bindings.excluding(root.id);
		bindings.bind(String.class)
				.first((StringPath path, String value) ->
						path.containsIgnoreCase(value));
	}

	/**
	 * QueryDsl Predicates for Medication.
	 */
	class Predicates {
		static final QMedication $ = QMedication.medication;

		private Predicates() {
		}

		// TODO
	}
}
