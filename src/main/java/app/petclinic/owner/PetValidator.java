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

import app.petclinic.common.validation.CustomValidator;
import app.petclinic.common.validation.ValidationResult;
import com.aspectran.core.activity.Translet;
import com.aspectran.core.component.bean.annotation.Bean;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.utils.StringUtils;
import org.jspecify.annotations.NonNull;

/**
 * <code>Validator</code> for <code>Pet</code> forms.
 * <p>
 * We're not using Bean Validation annotations here because it is easier to define such
 * validation rule in Java.
 * </p>
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 */
@Component
@Bean
public class PetValidator extends CustomValidator<Pet> {

	private static final String REQUIRED = "required";

	@Override
    public void validate(@NonNull Translet translet, @NonNull Pet pet, ValidationResult result) {
		String name = pet.getName();
		// name validation
		if (!StringUtils.hasText(name)) {
            result.putError("name", translet.getMessage(REQUIRED, REQUIRED));
		}

		// type validation
		if (pet.isNew() && pet.getType() == null) {
            result.putError("type", translet.getMessage(REQUIRED, REQUIRED));
		}

		// birth date validation
		if (pet.getBirthDate() == null) {
            result.putError("birthDate", translet.getMessage(REQUIRED, REQUIRED));
		}
	}

}
