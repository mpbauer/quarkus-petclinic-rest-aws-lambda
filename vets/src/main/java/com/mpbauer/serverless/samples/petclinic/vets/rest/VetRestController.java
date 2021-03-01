/*
 * Copyright 2016-2018 the original author or authors.
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
package com.mpbauer.serverless.samples.petclinic.vets.rest;

import com.mpbauer.serverless.samples.petclinic.vets.model.Specialty;
import com.mpbauer.serverless.samples.petclinic.vets.model.Vet;
import com.mpbauer.serverless.samples.petclinic.vets.security.Roles;
import com.mpbauer.serverless.samples.petclinic.vets.service.VetService;

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
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Vitaliy Fedoriv
 */

@Path("api/vets")
public class VetRestController {

    @Inject
    VetService vetService;

    @RolesAllowed(Roles.VET_ADMIN)
    @GET
    @Path("/")
    public Response getAllVets() {
        Collection<Vet> vets = new ArrayList<>(this.vetService.findAllVets());
        if (vets.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(vets).build();
    }

    @RolesAllowed(Roles.VET_ADMIN)
    @GET
    @Path(value = "/{vetId}")
    public Response getVet(@PathParam("vetId") int vetId) {
        Vet vet = this.vetService.findVetById(vetId);
        if (vet == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(vet).build();
    }

    @RolesAllowed(Roles.VET_ADMIN)
    @POST
    @Path("/")
    public Response addVet(@Valid @NotNull Vet vet,  @Context UriInfo uriInfo) {
        this.vetService.saveVet(vet);
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        uriBuilder.path(Integer.toString(vet.getId()));
        return Response.created(uriBuilder.build()).entity(vet).build();
    }

    @RolesAllowed(Roles.VET_ADMIN)
    @PUT
    @Path("/{vetId}")
    public Response updateVet(@PathParam("vetId") int vetId, @Valid Vet vet) {
        Vet currentVet = this.vetService.findVetById(vetId);
        if (currentVet == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        currentVet.setFirstName(vet.getFirstName());
        currentVet.setLastName(vet.getLastName());
        currentVet.clearSpecialties();
        for (Specialty spec : vet.getSpecialties()) {
            currentVet.addSpecialty(spec);
        }
        this.vetService.saveVet(currentVet);
        return Response.noContent().entity(currentVet).build();
    }

    @RolesAllowed(Roles.VET_ADMIN)
    @DELETE
    @Path("/{vetId}")
    @Transactional
    public Response deleteVet(@PathParam("vetId") int vetId) {
        Vet vet = this.vetService.findVetById(vetId);
        if (vet == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        this.vetService.deleteVet(vet);
        return Response.noContent().build();
    }
}
