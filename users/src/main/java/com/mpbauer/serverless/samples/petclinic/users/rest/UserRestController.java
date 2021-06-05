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

package com.mpbauer.serverless.samples.petclinic.users.rest;

import com.mpbauer.serverless.samples.petclinic.users.model.User;
import com.mpbauer.serverless.samples.petclinic.users.security.Roles;
import com.mpbauer.serverless.samples.petclinic.users.service.UserService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("api/users")
public class UserRestController {

    @Inject
    UserService userService;

    @RolesAllowed(Roles.ADMIN)
    @POST
    @Path("/")
    public Response addOwner(@Valid @NotNull User user) throws Exception {
        this.userService.saveUser(user);
        return Response.status(Response.Status.CREATED).entity(user).build();
    }
}
