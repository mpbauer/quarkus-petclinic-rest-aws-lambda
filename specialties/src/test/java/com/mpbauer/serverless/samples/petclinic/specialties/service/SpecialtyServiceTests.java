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
package com.mpbauer.serverless.samples.petclinic.specialties.service;

import com.mpbauer.serverless.samples.petclinic.specialties.config.QuarkusDataSourceProvider;
import com.mpbauer.serverless.samples.petclinic.specialties.model.Specialty;
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
 * <p> Base class for {@link SpecialtyService} integration tests.
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
//@FlywayTest(value = @DataSource(url = "jdbc:h2:mem:default"))
class SpecialtyServiceTests {

    @Inject
    protected SpecialtyService specialtyService;


    @Test
    void shouldFindSpecialtyById() {
        Specialty specialty = this.specialtyService.findSpecialtyById(1);
        assertThat(specialty.getName()).isEqualTo("radiology");
    }

    @Test
    void shouldFindAllSpecialtys() {
        Collection<Specialty> specialties = this.specialtyService.findAllSpecialties();
        Specialty specialty1 = specialties.stream().filter(e -> e.getId() == 1).findFirst().get();
        assertThat(specialty1.getName()).isEqualTo("radiology");
        Specialty specialty3 = specialties.stream().filter(e -> e.getId() == 3).findFirst().get();
        assertThat(specialty3.getName()).isEqualTo("dentistry");
    }

    @Test
    @Transactional
    void shouldInsertSpecialty() {
        Collection<Specialty> specialties = this.specialtyService.findAllSpecialties();
        int found = specialties.size();

        Specialty specialty = new Specialty();
        specialty.setName("dermatologist");

        this.specialtyService.saveSpecialty(specialty);
        assertThat(specialty.getId().longValue()).isNotEqualTo(0);

        specialties = this.specialtyService.findAllSpecialties();
        assertThat(specialties.size()).isEqualTo(found + 1);
    }

    @Test
    @Transactional
    void shouldUpdateSpecialty() {
        Specialty specialty = this.specialtyService.findSpecialtyById(1);
        String oldLastName = specialty.getName();
        String newLastName = oldLastName + "X";
        specialty.setName(newLastName);
        this.specialtyService.saveSpecialty(specialty);
        specialty = this.specialtyService.findSpecialtyById(1);
        assertThat(specialty.getName()).isEqualTo(newLastName);
    }

    @Test
    @Transactional
    void shouldDeleteSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("test");
        this.specialtyService.saveSpecialty(specialty);
        Integer specialtyId = specialty.getId();
        assertThat(specialtyId).isNotNull();
        specialty = this.specialtyService.findSpecialtyById(specialtyId);
        assertThat(specialty).isNotNull();
        this.specialtyService.deleteSpecialty(specialty);
        try {
            specialty = this.specialtyService.findSpecialtyById(specialtyId);
        } catch (Exception e) {
            specialty = null;
        }
        assertThat(specialty).isNull();
    }
}
