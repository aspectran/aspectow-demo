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

import app.petclinic.common.validation.DefaultValidator;
import app.petclinic.common.validation.ValidationResult;
import com.aspectran.core.activity.Translet;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.component.bean.annotation.Dispatch;
import com.aspectran.core.component.bean.annotation.Redirect;
import com.aspectran.core.component.bean.annotation.RequestToGet;
import com.aspectran.core.component.bean.annotation.RequestToPost;
import com.aspectran.core.component.bean.annotation.Required;
import com.aspectran.utils.annotation.jsr305.NonNull;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Dave Syer
 */
@Component("/owners/${ownerId}/pets/${petId}/visits")
public class VisitController {

	private final OwnerDao ownerDao;

    private final DefaultValidator validator;

	public VisitController(OwnerDao ownerDao, DefaultValidator validator) {
		this.ownerDao = ownerDao;
        this.validator = validator;
	}

	@RequestToGet("/new")
    @Dispatch("pets/createOrUpdateVisitForm")
	public void initNewVisitForm(@NonNull Translet translet, @Required Integer ownerId, @Required Integer petId) {
        Owner owner = findOwner(ownerId, petId);
        Pet pet = owner.getPet(petId);
        Visit visit = new Visit();

        translet.setAttribute("owner", owner);
        translet.setAttribute("pet", pet);
        translet.setAttribute("visit", visit);
	}

	@RequestToPost("/new")
    @Redirect("/owners/${ownerId}")
	public void processNewVisitForm(@NonNull Translet translet, @Required Integer ownerId, @Required Integer petId, Visit visit) {
        Owner owner = findOwner(ownerId, petId);
        Pet pet = owner.getPet(petId);

        ValidationResult result = validator.validate(visit);
        if (result.hasErrors()) {
            translet.setAttribute("owner", owner);
            translet.setAttribute("pet", pet);
            translet.setAttribute("visit", visit);
            translet.setAttribute("errors", result.getErrors());
            translet.dispatch("pets/createOrUpdateVisitForm");
            return;
        }

        pet.addVisit(visit);
		this.ownerDao.save(owner);

		translet.getOutputFlashMap().put("message", "Your visit has been saved");
	}

    @NonNull
    private Owner findOwner(int ownerId, int petId) {
        Owner owner = ownerDao.findById(ownerId, petId);
        if (owner == null) {
            throw new IllegalArgumentException("Owner ID not found: " + ownerId);
        }
        if (owner.getPet(petId) == null) {
            throw new IllegalArgumentException("Pet ID not found: " + petId);
        }
        return owner;
    }

}
