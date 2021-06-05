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

import com.mpbauer.serverless.samples.petclinic.pets.config.QuarkusDataSourceProvider;
import com.mpbauer.serverless.samples.petclinic.pets.model.Owner;
import com.mpbauer.serverless.samples.petclinic.pets.model.Pet;
import com.mpbauer.serverless.samples.petclinic.pets.repository.OwnerRepository;
import com.mpbauer.serverless.samples.petclinic.pets.repository.PetTypeRepository;
import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p> Base class for {@link ClinicService} integration tests.
 *
 * @author Ken Krebs
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
@FlywayTest(@DataSource(QuarkusDataSourceProvider.class))
@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
class ClinicServiceTests {

    @Inject
    protected ClinicService clinicService;

    @Inject
    PetTypeRepository petTypeRepository;

    @Inject
    OwnerRepository ownerRepository;


    @Test
    void shouldFindPetWithCorrectId() {
        Pet pet7 = this.clinicService.findPetById(7);
        assertThat(pet7.getName()).startsWith("Samantha");
        assertThat(pet7.getOwner().getFirstName()).isEqualTo("Jean");

    }

    @Test
    @Transactional
    void shouldInsertPetIntoDatabaseAndGenerateId() {
        Owner owner6 = ownerRepository.findById(6);
        int found = owner6.getPets().size();

        Pet pet = new Pet();
        pet.setName("bowser");
        pet.setType(petTypeRepository.findById(2));
        pet.setBirthDate(new Date());
        owner6.addPet(pet);
        assertThat(owner6.getPets().size()).isEqualTo(found + 1);

        this.clinicService.savePet(pet);
        ownerRepository.save(owner6);

        owner6 = ownerRepository.findById(6);
        assertThat(owner6.getPets().size()).isEqualTo(found + 1);
        // checks that id has been generated
        assertThat(pet.getId()).isNotNull();
    }

    @Test
    @Transactional
    void shouldUpdatePetName() throws Exception {
        Pet pet7 = this.clinicService.findPetById(7);
        String oldName = pet7.getName();

        String newName = oldName + "X";
        pet7.setName(newName);
        this.clinicService.savePet(pet7);

        pet7 = this.clinicService.findPetById(7);
        assertThat(pet7.getName()).isEqualTo(newName);
    }

    @Test
    void shouldFindAllPets() {
        Collection<Pet> pets = this.clinicService.findAllPets();
        Pet pet1 = pets.stream().filter(pet -> pet.getId() == 1).findFirst().get();
        assertThat(pet1.getName()).isEqualTo("Leo");
        Pet pet3 = pets.stream().filter(pet -> pet.getId() == 3).findFirst().get();
        assertThat(pet3.getName()).isEqualTo("Rosy");
    }

    @Test
    @Transactional
    void shouldDeletePet() {
        Pet pet = this.clinicService.findPetById(1);
        this.clinicService.deletePet(pet);
        try {
            pet = this.clinicService.findPetById(1);
        } catch (Exception e) {
            pet = null;
        }
        assertThat(pet).isNull();
    }
}
