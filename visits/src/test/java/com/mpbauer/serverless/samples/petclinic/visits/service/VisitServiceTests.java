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
package com.mpbauer.serverless.samples.petclinic.visits.service;

import com.mpbauer.serverless.samples.petclinic.visits.config.QuarkusDataSourceProvider;
import com.mpbauer.serverless.samples.petclinic.visits.model.Pet;
import com.mpbauer.serverless.samples.petclinic.visits.model.Visit;
import com.mpbauer.serverless.samples.petclinic.visits.repository.PetRepository;
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
 * <p> Base class for {@link VisitService} integration tests.
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
class VisitServiceTests {

    @Inject
    protected VisitService visitService;

    @Inject
    protected PetRepository petRepository;

    @Test
    @Transactional
    void shouldAddNewVisitForPet() {
        Pet pet7 = this.petRepository.findById(7);
        int found = pet7.getVisits().size();
        Visit visit = new Visit();
        pet7.addVisit(visit);
        visit.setDescription("test");
        this.visitService.saveVisit(visit);
        this.petRepository.save(pet7);

        pet7 = this.petRepository.findById(7);
        assertThat(pet7.getVisits().size()).isEqualTo(found + 1);
        assertThat(visit.getId()).isNotNull();
    }

    @Test
    void shouldFindVisitsByPetId() throws Exception {
        Collection<Visit> visits = this.visitService.findVisitsByPetId(7);
        assertThat(visits.size()).isEqualTo(2);
        Visit[] visitArr = visits.toArray(new Visit[visits.size()]);
        assertThat(visitArr[0].getPet()).isNotNull();
        assertThat(visitArr[0].getDate()).isNotNull();
        assertThat(visitArr[0].getPet().getId()).isEqualTo(7);
    }

    @Test
    void shouldFindVisitDyId() {
        Visit visit = this.visitService.findVisitById(1);
        assertThat(visit.getId()).isEqualTo(1);
        assertThat(visit.getPet().getName()).isEqualTo("Samantha");
    }

    @Test
    void shouldFindAllVisits() {
        Collection<Visit> visits = this.visitService.findAllVisits();
        Visit visit1 = visits.stream().filter(e -> e.getId() == 1).findFirst().get();
        assertThat(visit1.getPet().getName()).isEqualTo("Samantha");
        Visit visit3 = visits.stream().filter(e -> e.getId() == 3).findFirst().get();
        assertThat(visit3.getPet().getName()).isEqualTo("Max");
    }

    @Test
    @Transactional
    void shouldInsertVisit() {
        Collection<Visit> visits = this.visitService.findAllVisits();
        int found = visits.size();

        Pet pet = this.petRepository.findById(1);

        Visit visit = new Visit();
        visit.setPet(pet);
        visit.setDate(new Date());
        visit.setDescription("new visit");


        this.visitService.saveVisit(visit);
        assertThat(visit.getId().longValue()).isNotEqualTo(0);

        visits = this.visitService.findAllVisits();
        assertThat(visits.size()).isEqualTo(found + 1);
    }

    @Test
    @Transactional
    void shouldUpdateVisit() {
        Visit visit = this.visitService.findVisitById(1);
        String oldDesc = visit.getDescription();
        String newDesc = oldDesc + "X";
        visit.setDescription(newDesc);
        this.visitService.saveVisit(visit);
        visit = this.visitService.findVisitById(1);
        assertThat(visit.getDescription()).isEqualTo(newDesc);
    }

    @Test
    @Transactional
    void shouldDeleteVisit() {
        Visit visit = this.visitService.findVisitById(1);
        this.visitService.deleteVisit(visit);
        try {
            visit = this.visitService.findVisitById(1);
        } catch (Exception e) {
            visit = null;
        }
        assertThat(visit).isNull();
    }
}
