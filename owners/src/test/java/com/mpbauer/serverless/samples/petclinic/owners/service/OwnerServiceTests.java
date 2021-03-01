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
package com.mpbauer.serverless.samples.petclinic.owners.service;

import com.mpbauer.serverless.samples.petclinic.owners.config.QuarkusDataSourceProvider;
import com.mpbauer.serverless.samples.petclinic.owners.model.Owner;
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
 * <p> Base class for {@link OwnerService} integration tests.
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
class OwnerServiceTests {

    @Inject
    protected OwnerService ownerService;

    @Test
    void shouldFindOwnersByLastName() {
        Collection<Owner> owners = this.ownerService.findOwnerByLastName("Davis");
        assertThat(owners.size()).isEqualTo(2);

        owners = this.ownerService.findOwnerByLastName("Daviss");
        assertThat(owners.isEmpty()).isTrue();
    }

    @Test
    void shouldFindSingleOwnerWithPet() {
        Owner owner = this.ownerService.findOwnerById(1);
        assertThat(owner.getLastName()).startsWith("Franklin");
        assertThat(owner.getPets().size()).isEqualTo(1);
        assertThat(owner.getPets().get(0).getType()).isNotNull();
        assertThat(owner.getPets().get(0).getType().getName()).isEqualTo("cat");
    }

    @Test
    @Transactional
    void shouldInsertOwner() {
        Collection<Owner> owners = this.ownerService.findOwnerByLastName("Schultz");
        int found = owners.size();

        Owner owner = new Owner();
        owner.setFirstName("Sam");
        owner.setLastName("Schultz");
        owner.setAddress("4, Evans Street");
        owner.setCity("Wollongong");
        owner.setTelephone("4444444444");
        this.ownerService.saveOwner(owner);
        assertThat(owner.getId().longValue()).isNotEqualTo(0);

        owners = this.ownerService.findOwnerByLastName("Schultz");
        assertThat(owners.size()).isEqualTo(found + 1);
    }

    @Test
    @Transactional
    void shouldUpdateOwner() {
        Owner owner = this.ownerService.findOwnerById(1);
        String oldLastName = owner.getLastName();
        String newLastName = oldLastName + "X";

        owner.setLastName(newLastName);
        this.ownerService.saveOwner(owner);

        // retrieving new name from database
        owner = this.ownerService.findOwnerById(1);
        assertThat(owner.getLastName()).isEqualTo(newLastName);
    }

    @Test
    void shouldFindAllOwners() {
        Collection<Owner> owners = this.ownerService.findAllOwners();
        Owner owner1 = owners.stream().filter(e -> e.getId() == 1).findFirst().get();
        assertThat(owner1.getFirstName()).isEqualTo("George");
        Owner owner3 = owners.stream().filter(e -> e.getId() == 3).findFirst().get();
        assertThat(owner3.getFirstName()).isEqualTo("Eduardo");
    }

    @Test
    @Transactional
    void shouldDeleteOwner() {
        Owner owner = this.ownerService.findOwnerById(1);
        this.ownerService.deleteOwner(owner);
        try {
            owner = this.ownerService.findOwnerById(1);
        } catch (Exception e) {
            owner = null;
        }
        assertThat(owner).isNull();
    }
}
