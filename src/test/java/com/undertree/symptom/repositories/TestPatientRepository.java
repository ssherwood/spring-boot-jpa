package com.undertree.symptom.repositories;

import com.undertree.symptom.domain.Patient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestPatientRepository {

    @Autowired
    PatientRepository patientRepository;

    @Test
    public void test_PatientRepository_Save_ExpectNotNull_and_IdIsInitialized() throws Exception {
        Patient aPatient = patientRepository.save(new Patient());
        assertThat(aPatient).isNotNull();
        assertThat(aPatient.getId()).isNotNull()
                                    .isGreaterThan(0);
    }

    @Test
    public void test_PatientRepository_FindById_Expect_() throws Exception {
        Patient pPatient = new Patient();
        pPatient.setGivenName("Frank");
        pPatient.setFamilyName("Ferter");
        pPatient.setBirthDate(LocalDate.of(1972, 9, 19));
        Patient savedPatient = patientRepository.saveAndFlush(pPatient);

        Optional<Patient> aPatient = patientRepository.findById(savedPatient.getId());

        assertThat(aPatient.isPresent());
        assertThat(aPatient.get().getGivenName()).isEqualTo(pPatient.getGivenName());
        assertThat(aPatient.get().getFamilyName()).isEqualTo(pPatient.getFamilyName());
        assertThat(aPatient.get().getBirthDate()).isEqualTo(pPatient.getBirthDate());
    }
}
