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

import app.petclinic.common.db.DefaultEntityQuery;
import app.petclinic.common.pagination.PageInfo;
import com.aspectran.core.component.bean.annotation.Autowired;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.jpa.querydsl.EntityQuery;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.querydsl.core.Fetchable;

import java.util.List;

/**
 * Repository class for <code>Owner</code> domain objects All method names are compliant
 * with Spring Data naming conventions so this interface can easily be extended for Spring
 * Data. See:
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 */
@Component
public class OwnerDao {

    private final EntityQuery entityQuery;

    @Autowired
    public OwnerDao(DefaultEntityQuery entityQuery) {
        this.entityQuery = entityQuery;
    }

	/**
	 * Retrieve all {@link PetType}s from the data store.
	 * @return a Collection of {@link PetType}s.
	 */
	public List<PetType> findPetTypes() {
        QPetType petType = QPetType.petType;
        return entityQuery
                .selectFrom(petType)
                .orderBy(petType.name.asc())
                .fetch();
    }

	/**
	 * Retrieve {@link Owner}s from the data store by last name, returning all owners
	 * whose last name <i>starts</i> with the given name.
	 * @param lastName Value to search for
	 * @return a Collection of matching {@link Owner}s (or an empty Collection if none
	 * found)
	 */
	public List<Owner> findByLastName(String lastName, @NonNull PageInfo pageInfo) {
        QOwner owner = QOwner.owner;
        QPet pet = QPet.pet;
        List<Owner> listOwners = entityQuery
                .selectDistinct(owner)
                .from(owner)
                .leftJoin(owner.pets, pet)
                .where(owner.lastName.startsWith(lastName))
                .offset(pageInfo.getOffset())
                .limit(pageInfo.getSize())
                .fetch();

        Fetchable<Long> countQuery = entityQuery
                .select(owner.count())
                .from(owner);

        pageInfo.setTotalElements(listOwners.size(), countQuery::fetchOne);
        return listOwners;
    }

	/**
	 * Retrieve an {@link Owner} from the data store by id.
	 * @param id the id to search for
	 * @return the {@link Owner} if found
	 */
	public Owner findById(int id) {
        QOwner owner = QOwner.owner;
        QPet pet = QPet.pet;
        return entityQuery
                .selectFrom(owner)
                .leftJoin(owner.pets, pet)
                .where(owner.id.eq(id))
                .fetchOne();
    }

	/**
	 * Retrieve an {@link Owner} from the data store by id.
	 * @param ownerId the id to search for
	 * @param petId the id to search for
	 * @return the {@link Owner} if found
	 */
	public Owner findById(int ownerId, int petId) {
        QOwner owner = QOwner.owner;
        QPet pet = QPet.pet;
        return entityQuery
                .selectFrom(owner)
                .leftJoin(owner.pets, pet)
                .where(owner.id.eq(ownerId).and(owner.pets.any().id.eq(petId)))
                .fetchOne();
    }

	/**
	 * Save an {@link Owner} to the data store, either inserting or updating it.
	 * @param owner the {@link Owner} to save
	 */
	public void save(@NonNull Owner owner) {
        if (owner.isNew()) {
            entityQuery.persist(owner);
        } else {
            entityQuery.merge(owner);
        }
    }

	/**
	 * Returns all the owners from data store.
	 **/
	public List<Owner> findAll(@NonNull PageInfo pageInfo) {
        QOwner owner = QOwner.owner;
        QPet pet = QPet.pet;
        List<Owner> listOwners = entityQuery
                .selectFrom(owner)
                .leftJoin(owner.pets, pet)
                .offset(pageInfo.getOffset())
                .limit(pageInfo.getSize())
                .fetch();

        Fetchable<Long> countQuery = entityQuery
                .select(owner.count())
                .from(owner);

        pageInfo.setTotalElements(listOwners.size(), countQuery::fetchOne);
        return listOwners;
    }

}
