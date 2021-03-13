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

package com.mpbauer.serverless.samples.petclinic.pettypes.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpbauer.serverless.samples.petclinic.pettypes.AbstractIntegrationTest;
import com.mpbauer.serverless.samples.petclinic.pettypes.model.PetType;
import com.mpbauer.serverless.samples.petclinic.pettypes.service.PetTypeService;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;


/**
 * Test class for {@link PetTypeRestController}
 *
 * @author Vitaliy Fedoriv
 */
@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
class PetTypeRestControllerTests extends AbstractIntegrationTest {

    @InjectMock
    PetTypeService petTypeService;

    private List<PetType> petTypes;

    @BeforeEach
    public void initPetTypes() {
        petTypes = new ArrayList<>();

        PetType petType = new PetType();
        petType.setId(1);
        petType.setName("cat");
        petTypes.add(petType);

        petType = new PetType();
        petType.setId(2);
        petType.setName("dog");
        petTypes.add(petType);

        petType = new PetType();
        petType.setId(3);
        petType.setName("lizard");
        petTypes.add(petType);

        petType = new PetType();
        petType.setId(4);
        petType.setName("snake");
        petTypes.add(petType);
    }

    @Test
    void testGetPetTypeSuccessAsOwnerAdmin() {
        given(this.petTypeService.findPetTypeById(1)).willReturn(petTypes.get(0));

        RestAssured.given()
            .auth().oauth2(generateValidOwnerAdminToken())
            .accept(ContentType.JSON)
            .when()
            .get("/api/pettypes/1")
            .then()
            .contentType(ContentType.JSON)
            .statusCode(Response.Status.OK.getStatusCode())
            .body("id", equalTo(1))
            .body("name", equalTo("cat"));
    }

    @Test
    void testGetPetTypeSuccessAsVetAdmin() {
        given(this.petTypeService.findPetTypeById(1)).willReturn(petTypes.get(0));
        given()
            .auth().oauth2(generateValidVetAdminToken())
            .accept(ContentType.JSON)
            .when()
            .get("/api/pettypes/1")
            .then()
            .contentType(ContentType.JSON)
            .statusCode(Response.Status.OK.getStatusCode())
            .body("id", equalTo(1))
            .body("name", equalTo("cat"));
    }

    @Test
    void testGetPetTypeNotFound() {
        given(this.petTypeService.findPetTypeById(-1)).willReturn(null);
        given()
            .auth().oauth2(generateValidOwnerAdminToken())
            .accept(ContentType.JSON)
            .when()
            .get("/api/pettypes/-1")
            .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void testGetAllPetTypesSuccessAsOwnerAdmin() {
        petTypes.remove(0);
        petTypes.remove(1);
        given(this.petTypeService.findAllPetTypes()).willReturn(petTypes);
        given()
            .auth().oauth2(generateValidOwnerAdminToken())
            .accept(ContentType.JSON)
            .when()
            .get("/api/pettypes/")
            .then()
            .contentType(ContentType.JSON)
            .statusCode(Response.Status.OK.getStatusCode())
            .body("[0].id", equalTo(2))
            .body("[0].name", equalTo("dog"))
            .body("[1].id", equalTo(4))
            .body("[1].name", equalTo("snake"));
    }

    @Test
    void testGetAllPetTypesSuccessAsVetAdmin() {
        petTypes.remove(0);
        petTypes.remove(1);
        given(this.petTypeService.findAllPetTypes()).willReturn(petTypes);
        given()
            .auth().oauth2(generateValidVetAdminToken())
            .accept(ContentType.JSON)
            .when()
            .get("/api/pettypes/")
            .then()
            .contentType(ContentType.JSON)
            .statusCode(Response.Status.OK.getStatusCode())
            .body("[0].id", equalTo(2))
            .body("[0].name", equalTo("dog"))
            .body("[1].id", equalTo(4))
            .body("[1].name", equalTo("snake"));
    }

    @Test
    void testGetAllPetTypesNotFound() {
        petTypes.clear();
        given(this.petTypeService.findAllPetTypes()).willReturn(petTypes);
        given()
            .auth().oauth2(generateValidVetAdminToken())
            .accept(ContentType.JSON)
            .when()
            .get("/api/pettypes/")
            .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void testCreatePetTypeSuccess() throws Exception {
        PetType newPetType = petTypes.get(0);
        newPetType.setId(999);
        ObjectMapper mapper = new ObjectMapper();
        String newPetTypeAsJSON = mapper.writeValueAsString(newPetType);
        given()
            .auth().oauth2(generateValidVetAdminToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(newPetTypeAsJSON)
            .when()
            .post("/api/pettypes/")
            .then()
            .statusCode(Response.Status.CREATED.getStatusCode());
    }

    @Test
    void testCreatePetTypeError() throws Exception {
        PetType newPetType = petTypes.get(0);
        newPetType.setId(null);
        newPetType.setName(null);
        ObjectMapper mapper = new ObjectMapper();
        String newPetTypeAsJSON = mapper.writeValueAsString(newPetType);
        given()
            .auth().oauth2(generateValidVetAdminToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(newPetTypeAsJSON)
            .when()
            .post("/api/pettypes/")
            .then()
            .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void testUpdatePetTypeSuccess() throws Exception {
        given(this.petTypeService.findPetTypeById(2)).willReturn(petTypes.get(1));
        PetType newPetType = petTypes.get(1);
        newPetType.setName("dog I");
        ObjectMapper mapper = new ObjectMapper();
        String newPetTypeAsJSON = mapper.writeValueAsString(newPetType);
        given()
            .auth().oauth2(generateValidVetAdminToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(newPetTypeAsJSON)
            .when()
            .put("/api/pettypes/2")
            .then()
            .contentType(ContentType.JSON)
            .statusCode(Response.Status.NO_CONTENT.getStatusCode());


        given()
            .auth().oauth2(generateValidVetAdminToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .when()
            .get("/api/pettypes/2")
            .then()
            .contentType(ContentType.JSON)
            .statusCode(Response.Status.OK.getStatusCode())
            .body("id", equalTo(2))
            .body("name", equalTo("dog I"));
    }

    @Test
    void testUpdatePetTypeError() throws Exception {
        PetType newPetType = petTypes.get(0);
        newPetType.setName("");
        ObjectMapper mapper = new ObjectMapper();
        String newPetTypeAsJSON = mapper.writeValueAsString(newPetType);
        given()
            .auth().oauth2(generateValidVetAdminToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(newPetTypeAsJSON)
            .when()
            .put("/api/pettypes/1")
            .then()
            .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void testDeletePetTypeSuccess() throws Exception {
        PetType newPetType = petTypes.get(0);
        ObjectMapper mapper = new ObjectMapper();
        String newPetTypeAsJSON = mapper.writeValueAsString(newPetType);
        given(this.petTypeService.findPetTypeById(1)).willReturn(petTypes.get(0));
        given()
            .auth().oauth2(generateValidVetAdminToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(newPetTypeAsJSON)
            .when()
            .delete("/api/pettypes/1")
            .then()
            .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    void testDeletePetTypeError() throws Exception {
        PetType newPetType = petTypes.get(0);
        ObjectMapper mapper = new ObjectMapper();
        String newPetTypeAsJSON = mapper.writeValueAsString(newPetType);
        given(this.petTypeService.findPetTypeById(-1)).willReturn(null);
        given()
            .auth().oauth2(generateValidVetAdminToken())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(newPetTypeAsJSON)
            .when()
            .delete("/api/pettypes/-1")
            .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }
}
