package com.undertree.symptom.repositories;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.undertree.symptom.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static com.undertree.symptom.domain.QPatient.patient;

public interface PatientRepository extends JpaRepository<Patient, UUID>,
        QueryDslPredicateExecutor<Patient> {
    Optional<Patient> findById(UUID id);
    //Optional<Patient> findByIdAndVersion(UUID id, Long version);

    class Predicates {
        public static BooleanExpression hasBirthdayOn(LocalDate date) {
            return patient.birthDate.month().eq(date.getMonthValue()).and(
                    patient.birthDate.dayOfMonth().eq(date.getDayOfMonth()));
        }
    }
}
