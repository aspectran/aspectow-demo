/*
 * Copyright (c) 2018-present The Aspectran Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.petclinic.owner;

import app.petclinic.model.NamedEntity;
import com.aspectran.utils.annotation.jsr305.NonNull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Simple business object representing a pet.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
@Entity
@Table(name = "pets")
@SuppressWarnings("serial")
public class Pet extends NamedEntity {

	@Column(name = "birth_date")
	private LocalDate birthDate;

	@ManyToOne
	@JoinColumn(name = "type_id")
	private PetType type;

    @Transient
    private Integer typeId;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "pet_id")
	@OrderBy("date ASC")
	private Set<Visit> visits = new LinkedHashSet<>();

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public LocalDate getBirthDate() {
		return this.birthDate;
	}

	public PetType getType() {
		return this.type;
	}

	public void setType(PetType type) {
		this.type = type;
	}

    public Integer getTypeId() {
        return typeId;
    }

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public Collection<Visit> getVisits() {
		return this.visits;
	}

	public void addVisit(Visit visit) {
		getVisits().add(visit);
	}

    public void updatePet(@NonNull Pet pet) {
        this.setName(pet.getName());
        this.setBirthDate(pet.getBirthDate());
        this.setType(pet.getType());
    }

}
