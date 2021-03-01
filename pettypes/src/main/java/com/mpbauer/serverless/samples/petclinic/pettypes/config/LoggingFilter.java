package com.mpbauer.serverless.samples.petclinic.pettypes.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) {
        LOG.info("Request: [Method={}, Uri={}, Headers={}]", requestContext.getMethod(), requestContext.getUriInfo().getRequestUri(), requestContext.getHeaders());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        LOG.info("Response: [Method={}, StatusCode={} {}, Uri={}, Headers={}]", requestContext.getMethod(), responseContext.getStatusInfo().getStatusCode(), responseContext.getStatusInfo().getReasonPhrase(), requestContext.getUriInfo().getRequestUri(), responseContext.getHeaders());
    }
}
