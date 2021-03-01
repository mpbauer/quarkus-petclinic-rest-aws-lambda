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
package com.mpbauer.serverless.samples.petclinic.vets.service;

import com.mpbauer.serverless.samples.petclinic.vets.config.QuarkusDataSourceProvider;
import com.mpbauer.serverless.samples.petclinic.vets.model.Vet;
import com.mpbauer.serverless.samples.petclinic.vets.repository.PetTypeRepository;
import com.mpbauer.serverless.samples.petclinic.vets.repository.VetRepository;
import com.mpbauer.serverless.samples.petclinic.vets.service.VetService;
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
 * <p> Base class for {@link VetService} integration tests.
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
class VetServiceTests {

    @Inject
    protected VetService vetService;

    @Inject
    PetTypeRepository petTypeRepository;

    @Inject
    VetRepository vetRepository;


    @Test
    void shouldFindVets() {
        Collection<Vet> vets = this.vetService.findVets();

        Vet vet = vetRepository.findById(3);
        assertThat(vet.getLastName()).isEqualTo("Douglas");
        assertThat(vet.getNrOfSpecialties()).isEqualTo(2);
        assertThat(vet.getSpecialties().get(0).getName()).isEqualTo("dentistry");
        assertThat(vet.getSpecialties().get(1).getName()).isEqualTo("surgery");
    }

    @Test
    void shouldFindVetDyId() {
        Vet vet = this.vetService.findVetById(1);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
    }

    @Test
    @Transactional
    void shouldInsertVet() {
        Collection<Vet> vets = this.vetService.findAllVets();
        int found = vets.size();

        Vet vet = new Vet();
        vet.setFirstName("John");
        vet.setLastName("Dow");

        this.vetService.saveVet(vet);
        assertThat(vet.getId().longValue()).isNotEqualTo(0);

        vets = this.vetService.findAllVets();
        assertThat(vets.size()).isEqualTo(found + 1);
    }

    @Test
    @Transactional
    void shouldUpdateVet() {
        Vet vet = this.vetService.findVetById(1);
        String oldLastName = vet.getLastName();
        String newLastName = oldLastName + "X";
        vet.setLastName(newLastName);
        this.vetService.saveVet(vet);
        vet = this.vetService.findVetById(1);
        assertThat(vet.getLastName()).isEqualTo(newLastName);
    }

    @Test
    @Transactional
    void shouldDeleteVet() {
        Vet vet = this.vetService.findVetById(1);
        this.vetService.deleteVet(vet);
        try {
            vet = this.vetService.findVetById(1);
        } catch (Exception e) {
            vet = null;
        }
        assertThat(vet).isNull();
    }
}
