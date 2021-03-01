/*
 * Copyright 2016-2017 the original author or authors.
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

package com.mpbauer.serverless.samples.petclinic.pets.repository;

import com.mpbauer.serverless.samples.petclinic.pets.model.Pet;
import com.mpbauer.serverless.samples.petclinic.pets.model.PetType;
import com.mpbauer.serverless.samples.petclinic.pets.model.Visit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.List;

/**
 * @author Vitaliy Fedoriv
 */

@ApplicationScoped
public class PetTypeRepositoryImpl implements PetTypeRepository {

    @Inject
    @PersistenceContext
    EntityManager em;

    @Override
    public PetType findById(int id) {
        return this.em.find(PetType.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<PetType> findAll() {
        return this.em.createQuery("SELECT ptype FROM PetType ptype").getResultList();
    }

    @Override
    public void save(PetType petType) {
        if (petType.getId() == null) {
            this.em.persist(petType);
        } else {
            this.em.merge(petType);
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public void delete(PetType petType) {
        this.em.remove(this.em.contains(petType) ? petType : this.em.merge(petType));
        Integer petTypeId = petType.getId();

        List<Pet> pets = this.em.createQuery("SELECT pet FROM Pet pet WHERE type_id=" + petTypeId).getResultList();
        for (Pet pet : pets) {
            List<Visit> visits = pet.getVisits();
            for (Visit visit : visits) {
                this.em.createQuery("DELETE FROM Visit visit WHERE id=" + visit.getId()).executeUpdate();
            }
            this.em.createQuery("DELETE FROM Pet pet WHERE id=" + pet.getId()).executeUpdate();
        }
        this.em.createQuery("DELETE FROM PetType pettype WHERE id=" + petTypeId).executeUpdate();
    }

}
