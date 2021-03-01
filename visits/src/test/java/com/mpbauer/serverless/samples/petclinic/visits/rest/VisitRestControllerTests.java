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

package com.mpbauer.serverless.samples.petclinic.visits.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpbauer.serverless.samples.petclinic.visits.model.Owner;
import com.mpbauer.serverless.samples.petclinic.visits.model.Pet;
import com.mpbauer.serverless.samples.petclinic.visits.model.PetType;
import com.mpbauer.serverless.samples.petclinic.visits.model.Visit;
import com.mpbauer.serverless.samples.petclinic.visits.service.VisitService;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for {@link VisitRestController}
 *
 * @author Vitaliy Fedoriv
 */
@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
    // TODO check if necessary for native image build
class VisitRestControllerTests {

    @InjectMock
    VisitService visitService;

    private List<Visit> visits;

    @BeforeEach
    public void initVisits() {
        visits = new ArrayList<>();

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
        pet.setId(8);
        pet.setName("Rosy");
        pet.setBirthDate(new Date());
        pet.setOwner(owner);
        pet.setType(petType);


        Visit visit = new Visit();
        visit.setId(2);
        visit.setPet(pet);
        visit.setDate(new Date());
        visit.setDescription("rabies shot");
        visits.add(visit);

        visit = new Visit();
        visit.setId(3);
        visit.setPet(pet);
        visit.setDate(new Date());
        visit.setDescription("neutered");
        visits.add(visit);
    }

    @Test
        //@WithMockUser(roles="OWNER_ADMIN")
    void testGetVisitSuccess() {
        given(this.visitService.findVisitById(2)).willReturn(visits.get(0));
        given()
            .auth().none()
            .accept(ContentType.JSON)
            .when()
            .get("/api/visits/2")
            .then()
            .contentType(ContentType.JSON)
            .statusCode(Response.Status.OK.getStatusCode())
            .body("id", equalTo(2))
            .body("description", equalTo("rabies shot"));
    }

    @Test
        //@WithMockUser(roles="OWNER_ADMIN")
    void testGetVisitNotFound() {
        given(this.visitService.findVisitById(-1)).willReturn(null);
        given()
            .auth().none()
            .accept(ContentType.JSON)
            .when()
            .get("/api/visits/-1")
            .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
        //@WithMockUser(roles="OWNER_ADMIN")
    void testGetAllVisitsSuccess() {
        given(this.visitService.findAllVisits()).willReturn(visits);
        given()
            .auth().none()
            .accept(ContentType.JSON)
            .when()
            .get("/api/visits/")
            .then()
            .contentType(ContentType.JSON)
            .statusCode(Response.Status.OK.getStatusCode())
            .body("[0].id", equalTo(2))
            .body("[0].description", equalTo("rabies shot"))
            .body("[1].id", equalTo(3))
            .body("[1].description", equalTo("neutered"));
    }

    @Test
        //@WithMockUser(roles="OWNER_ADMIN")
    void testGetAllVisitsNotFound() {
        visits.clear();
        given(this.visitService.findAllVisits()).willReturn(visits);
        given()
            .auth().none()
            .accept(ContentType.JSON)
            .when()
            .get("/api/visits/")
            .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
        //@WithMockUser(roles="OWNER_ADMIN")
    void testCreateVisitSuccess() throws Exception {
        Visit newVisit = visits.get(0);
        newVisit.setId(999);
        ObjectMapper mapper = new ObjectMapper();
        String newVisitAsJSON = mapper.writeValueAsString(newVisit);
        System.out.println("newVisitAsJSON " + newVisitAsJSON);
        given()
            .auth().none()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(newVisitAsJSON)
            .when()
            .post("/api/visits/")
            .then()
            .statusCode(Response.Status.CREATED.getStatusCode());
    }

    //@Test(expected = IOException.class)
    //@WithMockUser(roles="OWNER_ADMIN")
    @Test
    // TODO assert exception
    void testCreateVisitError() {
        assertThrows(IOException.class, () -> {
            Visit newVisit = visits.get(0);
            newVisit.setId(null);
            newVisit.setPet(null);
            ObjectMapper mapper = new ObjectMapper();
            String newVisitAsJSON = mapper.writeValueAsString(newVisit);
            given()
                .auth().none()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(newVisitAsJSON)
                .when()
                .post("/api/visits/")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
        });
    }

    @Test
        //@WithMockUser(roles="OWNER_ADMIN")
    void testUpdateVisitSuccess() throws Exception {
        given(this.visitService.findVisitById(2)).willReturn(visits.get(0));
        Visit newVisit = visits.get(0);
        newVisit.setDescription("rabies shot test");
        ObjectMapper mapper = new ObjectMapper();
        String newVisitAsJSON = mapper.writeValueAsString(newVisit);

        given()
            .auth().none()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(newVisitAsJSON)
            .when()
            .put("/api/visits/2")
            .then()
            .contentType(ContentType.JSON)
            .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        given()
            .auth().none()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .when()
            .get("/api/visits/2")
            .then()
            .contentType(ContentType.JSON)
            .statusCode(Response.Status.OK.getStatusCode())
            .body("id", equalTo(2))
            .body("description", equalTo("rabies shot test"));
    }

    //@Test(expected = IOException.class)
    //@WithMockUser(roles="OWNER_ADMIN")
    @Test
    // TODO assert exception
    void testUpdateVisitError() {
        assertThrows(IOException.class, () -> {
            Visit newVisit = visits.get(0);
            newVisit.setPet(null);
            ObjectMapper mapper = new ObjectMapper();
            String newVisitAsJSON = mapper.writeValueAsString(newVisit);
            given()
                .auth().none()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(newVisitAsJSON)
                .when()
                .put("/api/visits/2")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
        });
    }

    @Test
        //@WithMockUser(roles="OWNER_ADMIN")
    void testDeleteVisitSuccess() throws Exception {
        Visit newVisit = visits.get(0);
        ObjectMapper mapper = new ObjectMapper();
        String newVisitAsJSON = mapper.writeValueAsString(newVisit);
        given(this.visitService.findVisitById(2)).willReturn(visits.get(0));
        given()
            .auth().none()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(newVisitAsJSON)
            .when()
            .delete("/api/visits/2")
            .then()
            .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
        //@WithMockUser(roles="OWNER_ADMIN")
    void testDeleteVisitError() throws Exception {
        Visit newVisit = visits.get(0);
        ObjectMapper mapper = new ObjectMapper();
        String newVisitAsJSON = mapper.writeValueAsString(newVisit);
        given(this.visitService.findVisitById(-1)).willReturn(null);
        given()
            .auth().none()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(newVisitAsJSON)
            .when()
            .delete("/api/visits/-1")
            .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

}
