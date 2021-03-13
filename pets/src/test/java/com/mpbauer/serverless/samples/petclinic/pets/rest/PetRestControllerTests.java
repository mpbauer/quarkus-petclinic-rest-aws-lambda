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

package com.mpbauer.serverless.samples.petclinic.pets.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpbauer.serverless.samples.petclinic.pets.AbstractIntegrationTest;
import com.mpbauer.serverless.samples.petclinic.pets.model.Owner;
import com.mpbauer.serverless.samples.petclinic.pets.model.Pet;
import com.mpbauer.serverless.samples.petclinic.pets.model.PetType;
import com.mpbauer.serverless.samples.petclinic.pets.service.ClinicService;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;


/**
 * Test class for {@link PetRestController}
 *
 * @author Vitaliy Fedoriv
 */

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
class PetRestControllerTests extends AbstractIntegrationTest {

    @InjectMock
    ClinicService clinicService;

    private List<Pet> pets;

    @BeforeEach
    public void initPets() {
        pets = new ArrayList<>();

        Owner owner = new Owner();
        owner.setId(1);
        owner.setFirstName("Eduardo");
        owner.setLastName("Rodriquez");
        owner.setAddress("2693 Commerce St.");
        owner.setCity("McFarland");
        owner.setTelephone("6085558763");

        PetType petType = new PetType();
        petType.setId(2);
        petType.setName("dog");

        Pet pet = new Pet();
        pet.setId(3);
        pet.setName("Rosy");
        pet.setBirthDate(new Date());
        pet.setOwner(owner);
        pet.setType(petType);
        pets.add(pet);

        pet = new Pet();
        pet.setId(4);
        pet.setName("Jewel");
        pet.setBirthDate(new Date());
        pet.setOwner(owner);
        pet.setType(petType);
        pets.add(pet);
    }

    @Test
    void testGetPetSuccess() {
        given(this.clinicService.findPetById(3)).willReturn(pets.get(0));
        given()
            .auth().oauth2(generateValidOwnerAdminToken())
            .when()
            .accept(ContentType.JSON)
            .get("/api/pets/3")
            .then()
            .contentType(ContentType.JSON)
            .statusCode(Response.Status.OK.getStatusCode())
            .body("id", equalTo(3))
            .body("name", equalTo("Rosy"));
    }

    @Test
    void testGetPetNotFound() {
        given(this.clinicService.findPetById(-1)).willReturn(null);
        given()
            .auth().oauth2(generateValidOwnerAdminToken())
            .accept(ContentType.JSON)
            .when()
            .get("/api/pets/-1")
            .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void testGetAllPetsSuccess() {
        given(this.clinicService.findAllPets()).willReturn(pets);
        given()
            .auth().oauth2(generateValidOwnerAdminToken())
            .accept(ContentType.JSON)
            .when()
            .get("/api/pets/")
            .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(ContentType.JSON)
            .body("[0].id", equalTo(3))
            .body("[0].name", equalTo("Rosy"))
            .body("[1].id", equalTo(4))
            .body("[1].name", equalTo("Jewel"));
    }

    @Test
    void testGetAllPetsNotFound() {
        pets.clear();
        given(this.clinicService.findAllPets()).willReturn(pets);
        given()
            .auth().oauth2(generateValidOwnerAdminToken())
            .accept(ContentType.JSON)
            .when()
            .get("/api/pets/")
            .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void testCreatePetSuccess() throws Exception {
        Pet newPet = pets.get(0);
        newPet.setId(999);
        ObjectMapper mapper = new ObjectMapper();
        String newPetAsJSON = mapper.writeValueAsString(newPet);

        given()
            .auth().oauth2(generateValidOwnerAdminToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(newPetAsJSON)
            .when()
            .post("/api/pets/")
            .then()
            .statusCode(Response.Status.CREATED.getStatusCode());
    }

    @Test
    void testCreatePetError() throws Exception {
        Pet newPet = pets.get(0);
        newPet.setId(null);
        newPet.setName(null);
        ObjectMapper mapper = new ObjectMapper();
        String newPetAsJSON = mapper.writeValueAsString(newPet);

        given()
            .auth().oauth2(generateValidOwnerAdminToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(newPetAsJSON)
            .when()
            .post("/api/pets/")
            .then()
            .log().ifValidationFails(LogDetail.BODY) // TODO is this a full replacement for .andDo(MockMvcResultHandlers.print())?
            .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void testUpdatePetSuccess() throws Exception {
        given(this.clinicService.findPetById(3)).willReturn(pets.get(0));
        Pet newPet = pets.get(0);
        newPet.setName("Rosy I");
        ObjectMapper mapper = new ObjectMapper();
        String newPetAsJSON = mapper.writeValueAsString(newPet);

        given()
            .auth().oauth2(generateValidOwnerAdminToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(newPetAsJSON)
            .when()
            .put("/api/pets/3")
            .then()
            .contentType(ContentType.JSON)
            .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        given()
            .auth().oauth2(generateValidOwnerAdminToken())
            .when()
            .get("/api/pets/3")
            .then()
            .contentType(ContentType.JSON)
            .statusCode(Response.Status.OK.getStatusCode())
            .body("id", equalTo(3))
            .body("name", equalTo("Rosy I"));
    }

    @Test
    void testUpdatePetError() throws Exception {
        Pet newPet = pets.get(0);
        newPet.setName("");
        ObjectMapper mapper = new ObjectMapper();
        String newPetAsJSON = mapper.writeValueAsString(newPet);

        given()
            .auth().oauth2(generateValidOwnerAdminToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(newPetAsJSON)
            .when()
            .put("/api/pets/3")
            .then()
            .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void testDeletePetSuccess() throws Exception {
        Pet newPet = pets.get(0);
        ObjectMapper mapper = new ObjectMapper();
        String newPetAsJSON = mapper.writeValueAsString(newPet);
        given(this.clinicService.findPetById(3)).willReturn(pets.get(0));
        given()
            .auth().oauth2(generateValidOwnerAdminToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(newPetAsJSON)
            .when()
            .delete("/api/pets/3")
            .then()
            .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    void testDeletePetError() throws Exception {
        Pet newPet = pets.get(0);
        ObjectMapper mapper = new ObjectMapper();
        String newPetAsJSON = mapper.writeValueAsString(newPet);
        given(this.clinicService.findPetById(-1)).willReturn(null);
        given()
            .auth().oauth2(generateValidOwnerAdminToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(newPetAsJSON)
            .when()
            .delete("/api/pets/-1")
            .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }
}
