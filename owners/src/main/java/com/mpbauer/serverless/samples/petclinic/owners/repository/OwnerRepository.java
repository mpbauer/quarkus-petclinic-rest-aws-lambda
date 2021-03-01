/*
 * Copyright 2002-2017 the original author or authors.
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
package com.mpbauer.serverless.samples.petclinic.owners.repository;


import com.mpbauer.serverless.samples.petclinic.owners.model.BaseEntity;
import com.mpbauer.serverless.samples.petclinic.owners.model.Owner;

import java.util.Collection;

/**
 * Repository class for <code>Owner</code> domain objects All method names are compliant with Spring Data naming
 * conventions so this interface can easily be extended for Spring Data See here: http://static.springsource.org/spring-data/jpa/docs/current/reference/html/jpa.repositories.html#jpa.query-methods.query-creation
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
public interface OwnerRepository {

    /**
     * Retrieve <code>Owner</code>s from the data store by last name, returning all owners whose last name <i>starts</i>
     * with the given name.
     *
     * @param lastName Value to search for
     * @return a <code>Collection</code> of matching <code>Owner</code>s (or an empty <code>Collection</code> if none
     * found)
     */
    Collection<Owner> findByLastName(String lastName);

    /**
     * Retrieve an <code>Owner</code> from the data store by id.
     *
     * @param id the id to search for
     * @return the <code>Owner</code> if found
     * @throws //org.springframework.dao.DataRetrievalFailureException if not found //TODO
     */
    Owner findById(int id);


    /**
     * Save an <code>Owner</code> to the data store, either inserting or updating it.
     *
     * @param owner the <code>Owner</code> to save
     * @see BaseEntity#isNew
     */
    void save(Owner owner);

    /**
     * Retrieve <code>Owner</code>s from the data store, returning all owners
     *
     * @return a <code>Collection</code> of <code>Owner</code>s (or an empty <code>Collection</code> if none
     * found)
     */
    Collection<Owner> findAll();

    /**
     * Delete an <code>Owner</code> to the data store by <code>Owner</code>.
     *
     * @param owner the <code>Owner</code> to delete
     */
    void delete(Owner owner);


}
