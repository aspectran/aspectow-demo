/*
 * Copyright (c) 2012-present-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.petclinic.owner;

import app.petclinic.common.pagination.PageInfo;
import app.petclinic.common.validation.DefaultValidator;
import app.petclinic.common.validation.ValidationResult;
import com.aspectran.core.activity.Translet;
import com.aspectran.core.component.bean.annotation.Autowired;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.component.bean.annotation.Dispatch;
import com.aspectran.core.component.bean.annotation.Request;
import com.aspectran.core.component.bean.annotation.RequestToGet;
import com.aspectran.core.component.bean.annotation.RequestToPost;
import com.aspectran.core.component.bean.annotation.Required;
import com.aspectran.utils.StringUtils;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.web.support.http.HttpStatusSetter;

import java.util.List;
import java.util.Map;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 */
@Component
public class OwnerController {

    private final OwnerDao ownerDao;

    private final DefaultValidator validator;

	@Autowired
    public OwnerController(OwnerDao ownerDao, DefaultValidator validator) {
        this.ownerDao = ownerDao;
        this.validator = validator;
	}

	@Request("/owners/find")
    @Dispatch("owners/findOwners")
	public void initFindForm(@NonNull Translet translet, Integer ownerId) {
        Owner owner = (ownerId == null ? new Owner() : ownerDao.findById(ownerId));
        translet.setAttribute("owner", owner);
	}

	@Request("/owners")
    @Dispatch("owners/ownersList")
	public void processFindForm(@NonNull Translet translet, String lastName) {
		// find owners by last name
        PageInfo pageInfo = PageInfo.of(translet, 5);
        List<Owner> listOwners = ownerDao.findByLastName(StringUtils.nullToEmpty(lastName), pageInfo);
		if (listOwners.isEmpty()) {
			// no owners found
            ValidationResult result = new ValidationResult();
            result.putError("lastName", translet.getMessage("notFound", "Not found"));
            translet.setAttribute("errors", result.getErrors());
            translet.setAttribute("owner", new Owner());
            translet.dispatch("owners/findOwners");
			return;
		}

		if (listOwners.size() == 1) {
			// 1 owner found
			Owner owner = listOwners.get(0);
            translet.redirect("/owners/" + owner.getId());
			return;
		}

        // multiple owners found
        translet.setAttribute("listOwners", listOwners);
        translet.setAttribute("pageInfo", pageInfo);
	}

    @RequestToGet("/owners/new")
    @Dispatch("owners/createOrUpdateOwnerForm")
    public void initCreationForm(@NonNull Translet translet) {
        Owner owner = new Owner();
        translet.setAttribute("owner", owner);
    }

    @RequestToPost("/owners/new")
    public void processCreationForm(@NonNull Translet translet, Owner owner, Integer page) {
        ValidationResult result = validator.validate(owner);
        if (result.hasErrors()) {
            translet.setAttribute("errors", result.getErrors());
            translet.setAttribute("owner", owner);
            translet.getOutputFlashMap().put("error", "There was an error in creating the owner.");
            translet.dispatch("owners/createOrUpdateOwnerForm");
            return;
        }

        ownerDao.save(owner);
        assert owner.getId() != null;

        translet.getOutputFlashMap().put("message", "New Owner Created");
        translet.redirect("/owners/" + owner.getId(),
                (page != null ? Map.of("page", Integer.toString(page)) : null));
    }

    @Request("/owners/${ownerId}/edit")
    @Dispatch("owners/createOrUpdateOwnerForm")
	public void initUpdateOwnerForm(@NonNull Translet translet, @Required int ownerId) {
        showOwner(translet, ownerId);
	}

    @RequestToPost("/owners/${ownerId}/edit")
	public void processUpdateOwnerForm(@NonNull Translet translet, Owner owner, @Required int ownerId, Integer page) {
        ValidationResult result = validator.validate(owner);
        if (result.hasErrors()) {
            translet.setAttribute("errors", result.getErrors());
            translet.setAttribute("owner", owner);
            translet.getOutputFlashMap().put("error", "There was an error in updating the owner.");
            translet.dispatch("owners/createOrUpdateOwnerForm");
            return;
        }

        Owner existingOwner = findOwner(ownerId);
        existingOwner.updateOwner(owner);
        ownerDao.save(existingOwner);

        translet.getOutputFlashMap().put("message", "Owner information Updated");
        translet.redirect("/owners/" + ownerId,
                (page != null ? Map.of("page", Integer.toString(page)) : null));
	}

	/**
	 * Custom handler for displaying an owner.
	 * @param ownerId the ID of the owner to display
	 */
	@Request("/owners/${ownerId}")
    @Dispatch("owners/ownerDetails")
	public void showOwner(@NonNull Translet translet, @Required int ownerId) {
        Owner owner = ownerDao.findById(ownerId);
        if (owner == null) {
            translet.setAttribute("error", "The owner with id " + ownerId + " doesn't exist.");
            HttpStatusSetter.notFound(translet);
            return;
        }
        translet.setAttribute("owner", owner);
	}

    @NonNull
    private Owner findOwner(int ownerId) {
        Owner owner = ownerDao.findById(ownerId);
        if (owner == null) {
            throw new IllegalArgumentException("Owner ID not found: " + ownerId);
        }
        return owner;
    }

}
