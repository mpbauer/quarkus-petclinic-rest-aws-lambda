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

import com.mpbauer.serverless.samples.petclinic.pets.model.Pet;
import com.mpbauer.serverless.samples.petclinic.pets.security.Roles;
import com.mpbauer.serverless.samples.petclinic.pets.service.ClinicService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.Collection;

/**
 * @author Vitaliy Fedoriv
 */

@Path("api/pets")
public class PetRestController {

    @Inject
    ClinicService clinicService;

    @RolesAllowed(Roles.OWNER_ADMIN)
    @GET
    @Path("/{petId}")
    public Response getPet(@PathParam("petId") int petId) {
        Pet pet = this.clinicService.findPetById(petId);
        if (pet == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(pet).build();
    }

    @RolesAllowed(Roles.OWNER_ADMIN)
    @GET
    @Path("/")
    public Response getPets() {
        Collection<Pet> pets = this.clinicService.findAllPets();
        if (pets.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(pets).build();
    }

    @RolesAllowed(Roles.OWNER_ADMIN)
    @GET
    @Path("/pettypes")
    public Response getPetTypes() {
        return Response.ok(this.clinicService.findPetTypes()).build();
    }

    @RolesAllowed(Roles.OWNER_ADMIN)
    @POST
    @Path("/")
    public Response addPet(@Valid @NotNull Pet pet, @Context UriInfo uriInfo) {
        this.clinicService.savePet(pet);
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        uriBuilder.path(Integer.toString(pet.getId()));
        return Response.status(Response.Status.CREATED).entity(pet).build();
    }

    @RolesAllowed(Roles.OWNER_ADMIN)
    @PUT
    @Path(value = "/{petId}")
    public Response updatePet(@PathParam("petId") int petId, @Valid @NotNull Pet pet) {
        Pet currentPet = this.clinicService.findPetById(petId);
        if (currentPet == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        currentPet.setBirthDate(pet.getBirthDate());
        currentPet.setName(pet.getName());
        currentPet.setType(pet.getType());
        currentPet.setOwner(pet.getOwner());
        this.clinicService.savePet(currentPet);
        return Response.noContent().entity(currentPet).build();
    }

    @RolesAllowed(Roles.OWNER_ADMIN)
    @DELETE
    @Path("/{petId}")
    @Transactional
    public Response deletePet(@PathParam("petId") int petId) {
        Pet pet = this.clinicService.findPetById(petId);
        if (pet == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        this.clinicService.deletePet(pet);
        return Response.noContent().build();
    }
}
