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

package com.mpbauer.serverless.samples.petclinic.vets.repository;

import com.mpbauer.serverless.samples.petclinic.vets.model.Specialty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;

/**
 * @author Vitaliy Fedoriv
 */

@ApplicationScoped
public class SpecialtyRepositoryImpl implements SpecialtyRepository {

    @Inject
    @PersistenceContext
    EntityManager em;

    @Override
    public Specialty findById(int id) {
        return this.em.find(Specialty.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Specialty> findAll() {
        return this.em.createQuery("SELECT s FROM Specialty s").getResultList();
    }

    @Override
    public void save(Specialty specialty) {
        if (specialty.getId() == null) {
            this.em.persist(specialty);
        } else {
            this.em.merge(specialty);
        }
    }

    @Override
    public void delete(Specialty specialty) {
        this.em.remove(this.em.contains(specialty) ? specialty : this.em.merge(specialty));
        Integer specId = specialty.getId();
        this.em.createNativeQuery("DELETE FROM vet_specialties WHERE specialty_id=" + specId).executeUpdate();
        this.em.createQuery("DELETE FROM Specialty specialty WHERE id=" + specId).executeUpdate();
    }

}
