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

package com.mpbauer.serverless.samples.petclinic.specialties.rest;

import com.mpbauer.serverless.samples.petclinic.specialties.security.Roles;
import com.mpbauer.serverless.samples.petclinic.specialties.service.SpecialtyService;
import com.mpbauer.serverless.samples.petclinic.specialties.model.Specialty;

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

@Path("api/specialties")
public class SpecialtyRestController {

    @Inject
    SpecialtyService specialtyService;

    @RolesAllowed(Roles.VET_ADMIN)
    @GET
    @Path("/")
    public Response getAllSpecialtys() {
        Collection<Specialty> specialties = new ArrayList<>(this.specialtyService.findAllSpecialties());
        if (specialties.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(specialties).build();
    }

    @RolesAllowed(Roles.VET_ADMIN)
    @GET
    @Path("/{specialtyId}")
    public Response getSpecialty(@PathParam("specialtyId") int specialtyId) {
        Specialty specialty = this.specialtyService.findSpecialtyById(specialtyId);
        if (specialty == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(specialty).build();
    }

    @RolesAllowed(Roles.VET_ADMIN)
    @POST
    @Path("/")
    public Response addSpecialty(@Valid @NotNull Specialty specialty, @Context UriInfo uriInfo) {
        this.specialtyService.saveSpecialty(specialty);
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        uriBuilder.path(Integer.toString(specialty.getId()));
        return Response.status(Response.Status.CREATED).entity(specialty).build();
    }

    @RolesAllowed(Roles.VET_ADMIN)
    @PUT
    @Path("/{specialtyId}")
    public Response updateSpecialty(@PathParam("specialtyId") int specialtyId, @Valid @NotNull Specialty specialty) {
        Specialty currentSpecialty = this.specialtyService.findSpecialtyById(specialtyId);
        if (currentSpecialty == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        currentSpecialty.setName(specialty.getName());
        this.specialtyService.saveSpecialty(currentSpecialty);
        return Response.noContent().entity(currentSpecialty).build();
    }

    @RolesAllowed(Roles.VET_ADMIN)
    @DELETE
    @Path("/{specialtyId}")
    @Transactional
    public Response deleteSpecialty(@PathParam("specialtyId") int specialtyId) {
        Specialty specialty = this.specialtyService.findSpecialtyById(specialtyId);
        if (specialty == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        this.specialtyService.deleteSpecialty(specialty);
        return Response.noContent().build();
    }

}
