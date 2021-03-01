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
package com.mpbauer.serverless.samples.petclinic.pets.service;

import com.mpbauer.serverless.samples.petclinic.pets.model.Pet;
import com.mpbauer.serverless.samples.petclinic.pets.model.PetType;
import com.mpbauer.serverless.samples.petclinic.pets.repository.PetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Collection;

/**
 * Mostly used as a facade for all Petclinic controllers
 * Also a placeholder for @Transactional and @Cacheable annotations
 *
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
@ApplicationScoped
public class ClinicServiceImpl implements ClinicService {

    private static final Logger LOG = LoggerFactory.getLogger(ClinicServiceImpl.class);


    PetRepository petRepository;

    @Inject
    public ClinicServiceImpl(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Override
    @Transactional
    public Collection<PetType> findPetTypes() {
        return petRepository.findPetTypes();
    }

    @Override
    @Transactional
    public Collection<Pet> findAllPets() {
        return petRepository.findAll();
    }

    @Override
    @Transactional
    public void deletePet(Pet pet) {
        petRepository.delete(pet);
    }

    @Override
    @Transactional
    public Pet findPetById(int id) {
        Pet pet = null;
        try {
            pet = petRepository.findById(id);
        } catch (Exception e) {
            LOG.warn("An unexpected exception occured", e);

            // TODO try to catch a more generic exception to not accidentally swallow important exceptions
            // just ignore not found exceptions for Jdbc/Jpa realization
            return null;
        }
        return pet;
    }

    @Override
    @Transactional
    public void savePet(Pet pet) {
        petRepository.save(pet);
    }
}
