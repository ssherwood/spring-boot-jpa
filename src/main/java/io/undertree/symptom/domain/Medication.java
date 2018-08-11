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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

/**
 * See also:
 * http://www.openmhealth.org/documentation/#/schema-docs/schema-library/schemas/omh_medication
 * https://rxnav.nlm.nih.gov/.
 *
 * @author Shawn Sherwood
 */
@Entity
public class Medication {

	/**
	 * The resource name for medications.
	 */
	public static final String RESOURCE_PATH = "/medications";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private Integer rxNormCode;

	@NotBlank
	@Size(min = 2)
	@Pattern(regexp = "^[A-Za-z0-9]+$")
	private String genericName;

	@NotBlank
	@Size(min = 2)
	@Pattern(regexp = "^[A-Za-z0-9]+$")
	private String tradeName;

	private DrugStrength strength;

	///

	public Long getId() {
		return this.id;
	}

	public String getTradeName() {
		return this.tradeName;
	}

	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}

	public String getGenericName() {
		return this.genericName;
	}

	public void setGenericName(String genericName) {
		this.genericName = genericName;
	}

	public DrugStrength getStrength() {
		return this.strength;
	}

	public void setStrength(DrugStrength strength) {
		this.strength = strength;
	}

	public Integer getRxNormCode() {
		return this.rxNormCode;
	}

	public void setRxNormCode(Integer rxNormCode) {
		this.rxNormCode = rxNormCode;
	}
}
