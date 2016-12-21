package com.undertree.symptom.domain;

import javax.persistence.Embeddable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Embeddable
public class DrugStrength {
    @NotNull @Min(0)
    private Integer value;
    private String unit;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
