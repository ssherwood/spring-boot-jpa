package com.undertree.symptom.controllers;

import com.undertree.symptom.domain.Patient;
import com.undertree.symptom.repositories.PatientRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(PatientController.class)
public class PatientControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientRepository mockPatientRepository;

    @Test
    public void test_MockPatient_Expect_ThatGuy() throws Exception {
        given(mockPatientRepository.findById(1L)).willReturn(Optional.of(thatGuy()));

        mockMvc.perform(get("/patient/1")
                .accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.givenName", is("Guy")))
                .andExpect(jsonPath("$.familyName", is("Stromboli")))
                .andExpect(jsonPath("$.birthDate", is("1942-11-21")));
    }

    private Patient thatGuy() {
        Patient aPatient = new Patient();
        aPatient.setGivenName("Guy");
        aPatient.setFamilyName("Stromboli");
        aPatient.setBirthDate(LocalDate.of(1942, 11, 21));
        return aPatient;
    }
}
