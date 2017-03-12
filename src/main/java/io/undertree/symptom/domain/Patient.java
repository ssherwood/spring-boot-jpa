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
package io.undertree.symptom.domain;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

// https://health-lifesci.schema.org/Patient
// http://www.openmhealth.org/documentation/#/schema-docs/schema-library

// Additional Notes:
// We decided to not store the age attribute and instead calculate it.
// We also decided to cache the calculation which meant that we are only going to
// change the value when a birthDay is set.  This required two changes to the
// entity - defining property level @Access for the birthDay field (that forces
// JPA to use the getter/setter methods provided, allowing us to introduce the
// age calculation, and marking the age field @Transient so that it is not
// considered as part of the actual persistence object.
@Entity
@JsonPropertyOrder({"_id"})
public class Patient {

	public static final String RESOURCE_PATH = "/patients";

	// TODO need to research further possible performance impact of using a UUID instead
	// the info so far indicates not using a UUID on clustered indexes since UUIDs are not sequential
	// some databases seem to have "native" support for UUIDs and others do not so this could cause
	// issues with some.
	// https://dba.stackexchange.com/questions/322/what-are-the-drawbacks-with-using-uuid-or-guid-as-a-primary-key
	// http://blog.xebia.com/jpa-implementation-patterns-using-uuids-as-primary-keys/
	// https://vladmihalcea.com/2014/07/01/hibernate-and-uuid-identifiers/
	// https://mariadb.com/kb/en/mariadb/guiduuid-performance/
	// http://www.thoughts-on-java.org/generate-uuids-primary-keys-hibernate/
	// http://www.starkandwayne.com/blog/uuid-primary-keys-in-postgresql/
	// https://www.clever-cloud.com/blog/engineering/2015/05/20/why-auto-increment-is-a-terrible-idea/  <- good read
	@OneToMany
	Set<Prescription> prescriptions = new HashSet<>();
	@Id
	@GeneratedValue
	private Long id;

	// @Version
	// private Long version;
	@Column(columnDefinition = "uuid", unique = true, updatable = false)
	private UUID patientId;
	@NotBlank
	@Size(min = 2)
	@Pattern(regexp = "^[A-Za-z0-9]+$")
	private String givenName;
	@Pattern(regexp = "^[A-Za-z0-9]+$")
	private String additionalName;
	@NotBlank
	@Size(min = 2)
	@Pattern(regexp = "^[A-Za-z0-9]+$")
	private String familyName;
	//@Past https://stackoverflow.com/questions/30249829/error-no-validator-could-be-found-for-type-java-time-localdate
	@Access(AccessType.PROPERTY)
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate birthDate;
	@Transient
	private Integer age;
	private Gender gender;
	@Email
	private String email;
	@Min(0)
	private Short height; // height in cm
	@Min(0)
	private Short weight; // weight in kg

	/**
	 * Default constructor sets the patientId to a random UUID
	 */
	public Patient() {
		this(UUID.randomUUID());
	}

	/**
	 * Optional constructor that allows UUID to be supplied
	 *
	 * @param patientId the explicit UUID to use
	 */
	public Patient(final UUID patientId) {
		this.patientId = patientId;
	}

	@JsonIgnore
	public Long getId() {
		return id;
	}

	@JsonIgnore
	public UUID getPatientId() {
		return patientId;
	}

	public String get_id() {
		return RESOURCE_PATH + "/" + patientId;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getAdditionalName() {
		return additionalName;
	}

	public void setAdditionalName(String additionalName) {
		this.additionalName = additionalName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
		this.age = birthDate == null ? null : Period.between(birthDate, LocalDate.now()).getYears();
	}

	public Integer getAge() {
		return age;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Short getHeight() {
		return height;
	}

	public void setHeight(Short height) {
		this.height = height;
	}

	public Short getWeight() {
		return weight;
	}

	public void setWeight(Short weight) {
		this.weight = weight;
	}

	public Set<Prescription> getPrescriptions() {
		return prescriptions;
	}

	public void setPrescriptions(Set<Prescription> prescriptions) {
		this.prescriptions = prescriptions;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Patient)) {
			return false;
		}
		Patient patient = (Patient) o;
		return Objects.equals(getPatientId(), patient.getPatientId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getPatientId());
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
	}
}
