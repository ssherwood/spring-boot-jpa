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
package io.undertree.symptom.controllers;

import io.undertree.symptom.domain.GivenName;
import io.undertree.symptom.domain.TestPatientBuilder;
import io.undertree.symptom.repositories.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
public class PatientControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PatientRepository mockPatientRepository;

	@Test
	public void test_MockPatient_Expect_ThatGuy() throws Exception {
		given(mockPatientRepository.findByPatientId(UUID.fromString("e7a47ecd-4182-4209-911b-f7574ded1611")))
				.willReturn(Optional.of(new TestPatientBuilder()
						.withGivenName(new GivenName("Guy"))
						.withFamilyName("Stromboli")
						.withBirthDate(LocalDate.of(1942, 11, 21))
						.build()));

		mockMvc.perform(get("/patients/e7a47ecd-4182-4209-911b-f7574ded1611")
				.accept(APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.givenName", is("Guy")))
				.andExpect(jsonPath("$.familyName", is("Stromboli")))
				.andExpect(jsonPath("$.birthDate", is("1942-11-21")));
	}
}
