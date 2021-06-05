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
package com.mpbauer.serverless.samples.petclinic.pettypes.service;

import com.mpbauer.serverless.samples.petclinic.pettypes.config.QuarkusDataSourceProvider;
import com.mpbauer.serverless.samples.petclinic.pettypes.model.PetType;
import com.mpbauer.serverless.samples.petclinic.pettypes.repository.PetTypeRepository;
import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p> Base class for {@link PetTypeService} integration tests.
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
class PetTypeServiceTests {

    @Inject
    protected PetTypeService petTypeService;

    @Inject
    PetTypeRepository petTypeRepository;


    @Test
    void shouldFindPetTypeById() {
        PetType petType = this.petTypeService.findPetTypeById(1);
        assertThat(petType.getName()).isEqualTo("cat");
    }

    @Test
    void shouldFindAllPetTypes() {
        Collection<PetType> petTypes = this.petTypeService.findAllPetTypes();
        PetType petType1 = petTypes.stream().filter(e -> e.getId() == 1).findFirst().get();
        assertThat(petType1.getName()).isEqualTo("cat");
        PetType petType3 = petTypes.stream().filter(e -> e.getId() == 3).findFirst().get();
        assertThat(petType3.getName()).isEqualTo("lizard");
    }

    @Test
    @Transactional
    void shouldInsertPetType() {
        Collection<PetType> petTypes = this.petTypeService.findAllPetTypes();
        int found = petTypes.size();

        PetType petType = new PetType();
        petType.setName("tiger");

        this.petTypeService.savePetType(petType);
        assertThat(petType.getId().longValue()).isNotEqualTo(0);

        petTypes = this.petTypeService.findAllPetTypes();
        assertThat(petTypes.size()).isEqualTo(found + 1);
    }

    @Test
    @Transactional
    void shouldUpdatePetType() {
        PetType petType = this.petTypeService.findPetTypeById(1);
        String oldLastName = petType.getName();
        String newLastName = oldLastName + "X";
        petType.setName(newLastName);
        this.petTypeService.savePetType(petType);
        petType = this.petTypeService.findPetTypeById(1);
        assertThat(petType.getName()).isEqualTo(newLastName);
    }

    @Test
    @Transactional
    void shouldDeletePetType() {
        PetType petType = this.petTypeService.findPetTypeById(1);
        this.petTypeService.deletePetType(petType);
        try {
            petType = this.petTypeService.findPetTypeById(1);
        } catch (Exception e) {
            petType = null;
        }
        assertThat(petType).isNull();
    }
}
