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
package app.petclinic.common.db;

import com.aspectran.core.component.bean.annotation.Bean;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.jpa.querydsl.EntityQuery;
import com.querydsl.jpa.JPQLTemplates;

/**
 * Default QueryDSL-based JPA query executor for the PetClinic application.
 *
 * <p>This class provides a unified API for executing type-safe JPA queries using QueryDSL,
 * combining the functionality of:
 * <ul>
 *     <li>{@link jakarta.persistence.EntityManager} - for direct JPA operations</li>
 *     <li>{@link com.querydsl.jpa.JPQLQueryFactory} - for type-safe JPQL query construction</li>
 * </ul>
 *
 * <p><b>Transaction Management:</b><br>
 * This query executor automatically participates in Aspectran's AOP-based transaction
 * management through the aspect identified by {@code "defaultEntityManagerAspect"}.
 * All query and data modification operations are executed within the context of
 * the current transaction managed by {@link com.aspectran.jpa.EntityManagerAdvice}.
 *
 * <p><b>QueryDSL Integration:</b><br>
 * Uses {@link JPQLTemplates#DEFAULT} to provide standard JPQL syntax compatibility
 * across different JPA providers. The QueryDSL Q-types (metamodel classes) enable
 * compile-time type safety and IDE code completion for query construction.
 *
 * <p><b>Usage Example:</b>
 * <pre>{@code
 * @Autowired
 * private DefaultEntityQuery entityQuery;
 *
 * public List<Owner> findOwnersByLastName(String lastName) {
 *     QOwner owner = QOwner.owner;
 *     return entityQuery
 *         .selectFrom(owner)
 *         .where(owner.lastName.eq(lastName))
 *         .fetch();
 * }
 * }</pre>
 *
 * @see EntityQuery
 * @see com.aspectran.jpa.EntityManagerAdvice
 * @see JPQLTemplates
 *
 * <p>Created: 2025-05-02</p>
 */
@Component
@Bean(lazyDestroy = true)
public class DefaultEntityQuery extends EntityQuery {

    /**
     * Constructs a new DefaultEntityQuery instance.
     * <p>This constructor configures the query executor to:
     * <ul>
     *     <li>Use the aspect identified by {@code "defaultEntityManagerAspect"} for
     *         transaction management and EntityManager lifecycle</li>
     *     <li>Use default JPQL templates for standard JPA compatibility</li>
     * </ul>
     * <p>The specified aspect ID must correspond to a registered
     * {@link com.aspectran.jpa.EntityManagerAdvice} aspect in the Aspectran context.
     */
    public DefaultEntityQuery() {
        super("defaultEntityManagerAspect");
        setTemplates(JPQLTemplates.DEFAULT);
    }

}
