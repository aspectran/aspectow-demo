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
package com.aspectran.aspectow.demo.petclinic.vet;

import com.aspectran.aspectow.demo.petclinic.common.pagination.PageInfo;
import com.aspectran.aspectow.demo.petclinic.common.validation.DefaultValidator;
import com.aspectran.aspectow.demo.petclinic.common.validation.ValidationResult;
import com.aspectran.core.activity.Translet;
import com.aspectran.core.component.bean.annotation.Autowired;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.component.bean.annotation.Dispatch;
import com.aspectran.core.component.bean.annotation.Request;
import com.aspectran.core.component.bean.annotation.RequestToGet;
import com.aspectran.core.component.bean.annotation.RequestToPost;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Ken Krebs
 * @author Arjen Poutsma
 */
@Component
public class VetController {

    private final VetDao vetDao;

    private final DefaultValidator validator;

    @Autowired
    public VetController(VetDao vetDao, DefaultValidator validator) {
        this.vetDao = vetDao;
        this.validator = validator;
    }

    @Request("/vets.html")
    @Dispatch("vets/vetList")
    public void vetList(@NonNull Translet translet) {
        PageInfo pageInfo = PageInfo.of(translet);
        List<Vet> listVets = vetDao.findAll(pageInfo);
        if (listVets.isEmpty() && pageInfo.getNumber() > 1) {
            pageInfo = PageInfo.of(pageInfo.getNumber() - 1, pageInfo.getSize());
            listVets = vetDao.findAll(pageInfo);
        }

        translet.setAttribute("listVets", listVets);
        translet.setAttribute("pageInfo", pageInfo);
    }

    @RequestToGet("/vets/new")
    @Dispatch("vets/createOrUpdateVetForm")
    public void initCreationForm(@NonNull Translet translet) {
        Vet vet = new Vet();
        translet.setAttribute("vet", vet);
        translet.setAttribute("specialties", populateSpecialties());
    }

    @RequestToPost("/vets/new")
    public void processCreationForm(@NonNull Translet translet, @NonNull Vet vet, Integer[] specialtyIds, Integer page) {
        Collection<Specialty> allSpecialties = populateSpecialties();
        bindSpecialties(vet, specialtyIds, allSpecialties);

        ValidationResult result = validator.validate(vet);
        if (result.hasErrors()) {
            translet.setAttribute("errors", result.getErrors());
            translet.setAttribute("vet", vet);
            translet.setAttribute("specialties", allSpecialties);
            translet.getOutputFlashMap().put("error", "There was an error in creating the veterinarian.");
            translet.dispatch("vets/createOrUpdateVetForm");
            return;
        }

        vetDao.save(vet);
        assert vet.getId() != null;

        translet.getOutputFlashMap().put("message", "New Veterinarian Created");
        translet.redirect("/vets.html",
                (page != null ? Map.of("page", Integer.toString(page)) : null));
    }

    @RequestToGet("/vets/${vetId}/edit")
    @Dispatch("vets/createOrUpdateVetForm")
    public void initUpdateVetForm(@NonNull Translet translet, @NonNull Integer vetId) {
        Vet vet = findVet(vetId);
        translet.setAttribute("vet", vet);
        translet.setAttribute("specialties", populateSpecialties());
    }

    @RequestToPost("/vets/${vetId}/edit")
    public void processUpdateVetForm(@NonNull Translet translet, @NonNull Vet vet, @NonNull Integer vetId,
                                     Integer[] specialtyIds, Integer page) {
        Collection<Specialty> allSpecialties = populateSpecialties();
        bindSpecialties(vet, specialtyIds, allSpecialties);

        ValidationResult result = validator.validate(vet);
        if (result.hasErrors()) {
            vet.setId(vetId);
            translet.setAttribute("errors", result.getErrors());
            translet.setAttribute("vet", vet);
            translet.setAttribute("specialties", allSpecialties);
            translet.getOutputFlashMap().put("error", "There was an error in updating the veterinarian.");
            translet.dispatch("vets/createOrUpdateVetForm");
            return;
        }

        Vet existingVet = findVet(vetId);
        existingVet.updateVet(vet);
        vetDao.save(existingVet);

        translet.getOutputFlashMap().put("message", "Veterinarian Information Updated");
        translet.redirect("/vets.html",
                (page != null ? Map.of("page", Integer.toString(page)) : null));
    }

    @RequestToPost("/vets/${vetId}/delete")
    public void deleteVet(@NonNull Translet translet, @NonNull Integer vetId, Integer page) {
        Vet existingVet = findVet(vetId);
        vetDao.delete(existingVet);

        translet.getOutputFlashMap().put("message", "Veterinarian Deleted");
        translet.redirect("/vets.html",
                (page != null ? Map.of("page", Integer.toString(page)) : null));
    }

    private Collection<Specialty> populateSpecialties() {
        return vetDao.findSpecialties();
    }

    private void bindSpecialties(@NonNull Vet vet, Integer[] specialtyIds, Collection<Specialty> allSpecialties) {
        vet.clearSpecialties();
        if (specialtyIds != null) {
            for (Integer specialtyId : specialtyIds) {
                if (specialtyId != null) {
                    for (Specialty specialty : allSpecialties) {
                        if (specialtyId.equals(specialty.getId())) {
                            vet.addSpecialty(specialty);
                            break;
                        }
                    }
                }
            }
        }
    }

    @NonNull
    private Vet findVet(int vetId) {
        Vet vet = vetDao.findById(vetId);
        if (vet == null) {
            throw new IllegalArgumentException("Vet ID not found: " + vetId);
        }
        return vet;
    }

}
