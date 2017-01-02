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
package com.undertree.symptom.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

// http://www.openmhealth.org/documentation/#/schema-docs/schema-library/schemas/omh_medication
// https://rxnav.nlm.nih.gov/

@Entity
public class Medication {

  public static final String RESOURCE_PATH = "/medication";
  public static final String RESOURCES_PATH = "/medications";

  @Id
  @GeneratedValue
  private Long id;
  private String tradeName;
  private String genericName;
  private DrugStrength strength;
  private Integer rxNormCode;

  ///

  public Long getId() {
    return id;
  }

  public String getTradeName() {
    return tradeName;
  }

  public void setTradeName(String tradeName) {
    this.tradeName = tradeName;
  }

  public String getGenericName() {
    return genericName;
  }

  public void setGenericName(String genericName) {
    this.genericName = genericName;
  }

  public DrugStrength getStrength() {
    return strength;
  }

  public void setStrength(DrugStrength strength) {
    this.strength = strength;
  }

  public Integer getRxNormCode() {
    return rxNormCode;
  }

  public void setRxNormCode(Integer rxNormCode) {
    this.rxNormCode = rxNormCode;
  }
}
