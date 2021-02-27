package com.mpbauer.serverless.samples.petclinic.visits;

import com.mpbauer.serverless.samples.petclinic.commons.util.MySharedUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello-resteasy")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return MySharedUtils.greetings("Mpbauer");
    }
}