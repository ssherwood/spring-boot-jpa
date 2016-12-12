package com.undertree.symptom.repositories;

import com.undertree.symptom.domain.Patient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TestPatientRepository {

    @Autowired
    PatientRepository patientRepository;

    // refer to:
    // https://joel-costigliola.github.io/assertj/
    // http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html
    // https://spring.io/blog/2016/04/15/testing-improvements-in-spring-boot-1-4
    // http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#__sql

    @Test
    public void test_PatientRepository_Save_ExpectNotNull_and_IdIsInitialized() throws Exception {
        Patient aPatient = patientRepository.save(createRandomPatient());

        assertThat(aPatient).isNotNull();
        assertThat(aPatient.getId()).isNotNull()
                                    .isGreaterThan(0);
    }

    @Test
    public void test_PatientRepository_FindById_Expect_Patient1_Found() throws Exception {
        Optional<Patient> aPatient = patientRepository.findById(1L);

        assertThat(aPatient.isPresent());
        assertThat(aPatient.get().getGivenName()).isEqualTo("Phillip");
        assertThat(aPatient.get().getFamilyName()).isEqualTo("Spec");
        assertThat(aPatient.get().getBirthDate()).isEqualTo(LocalDate.of(1972, 5, 5));
    }

    // TODO make this more random
    private Patient createRandomPatient() {
        Patient randomPatient = new Patient();
        randomPatient.setBirthDate(randomDate(1970));
        randomPatient.setGivenName("random");
        randomPatient.setFamilyName("random");
        return randomPatient;
    }

    private LocalDate randomDate(int year) {
        LocalDate start = LocalDate.of(year, Month.JANUARY, 1);
        long days = ChronoUnit.DAYS.between(start, LocalDate.now());
        return start.plusDays(new Random().nextInt((int) days + 1));
    }
}
