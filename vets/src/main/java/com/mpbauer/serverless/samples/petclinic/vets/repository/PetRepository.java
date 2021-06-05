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
package com.mpbauer.serverless.samples.petclinic.vets.repository;


import com.mpbauer.serverless.samples.petclinic.vets.model.BaseEntity;
import com.mpbauer.serverless.samples.petclinic.vets.model.Pet;
import com.mpbauer.serverless.samples.petclinic.vets.model.PetType;

import java.util.Collection;
import java.util.List;

/**
 * Repository class for <code>Pet</code> domain objects All method names are compliant with Spring Data naming
 * conventions so this interface can easily be extended for Spring Data See here: http://static.springsource.org/spring-data/jpa/docs/current/reference/html/jpa.repositories.html#jpa.query-methods.query-creation
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
public interface PetRepository {

    /**
     * Retrieve all <code>PetType</code>s from the data store.
     *
     * @return a <code>Collection</code> of <code>PetType</code>s
     */
    List<PetType> findPetTypes();

    /**
     * Retrieve a <code>Pet</code> from the data store by id.
     *
     * @param id the id to search for
     * @return the <code>Pet</code> if found
     * @throws //org.springframework.dao.DataRetrievalFailureException if not found //TODO
     */
    Pet findById(int id);

    /**
     * Save a <code>Pet</code> to the data store, either inserting or updating it.
     *
     * @param pet the <code>Pet</code> to save
     * @see BaseEntity#isNew
     */
    void save(Pet pet);

    /**
     * Retrieve <code>Pet</code>s from the data store, returning all owners
     *
     * @return a <code>Collection</code> of <code>Pet</code>s (or an empty <code>Collection</code> if none
     * found)
     */
    Collection<Pet> findAll();

    /**
     * Delete an <code>Pet</code> to the data store by <code>Pet</code>.
     *
     * @param pet the <code>Pet</code> to delete
     */
    void delete(Pet pet);

}
