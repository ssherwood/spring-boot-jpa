package com.undertree.symptom.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import java.time.LocalDate;

// https://health-lifesci.schema.org/Patient

@Entity
public class Patient {
    @Id
    @GeneratedValue
    private Long id;
    @NotBlank @Size(min = 2)
    private String givenName;
    @NotBlank @Size(min = 2)
    private String familyName;
    //@Past https://stackoverflow.com/questions/30249829/error-no-validator-could-be-found-for-type-java-time-localdate
    private LocalDate birthDate;

    public Long getId() {
        return id;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
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
    }

    // TODO discuss equals and hashcode
}
