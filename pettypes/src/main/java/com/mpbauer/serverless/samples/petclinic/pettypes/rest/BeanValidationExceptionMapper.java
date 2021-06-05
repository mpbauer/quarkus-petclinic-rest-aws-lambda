package com.mpbauer.serverless.samples.petclinic.pettypes.rest;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.List;

@Provider
public class BeanValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException e) {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(createErrorMessage(e))
            .header("errors", createErrorMessage(e))
            .type(MediaType.APPLICATION_JSON)
            .build();
    }

    private JsonArray createErrorMessage(ConstraintViolationException ex) {
        JsonArrayBuilder errors = Json.createArrayBuilder();
        ex.getConstraintViolations().forEach(violation -> {
            getObjectName(violation);
            errors.add(
                Json.createObjectBuilder()
                    .add("objectName", getObjectName(violation))
                    .add("fieldName", getFieldName(violation))
                    .add("fieldValue", getRejectedFieldValue(violation))
                    .add("errorMessage", violation.getMessage())
            );
        });
        return errors.build();
    }

    private String getRejectedFieldValue(ConstraintViolation<?> violation) {
        if (violation.getInvalidValue() != null) {
            return violation.getInvalidValue().toString();
        }
        return "";
    }

    private String getObjectName(ConstraintViolation<?> violation) {
        List<String> pathList = getConstraintViolationPropertyPaths(violation);

        if (pathList.size() > 1) {
            return pathList.get(pathList.size() - 2);
        }
        return pathList.get(0);
    }

    private String getFieldName(ConstraintViolation<?> violation) {
        List<String> pathList = getConstraintViolationPropertyPaths(violation);

        if (pathList.size() > 1) {
            return pathList.get(pathList.size() - 1);
        }
        return pathList.get(0);
    }

    private List<String> getConstraintViolationPropertyPaths(ConstraintViolation<?> violation) {
        List<String> pathList = new ArrayList<>();
        violation.getPropertyPath().forEach(node -> pathList.add(node.getName()));
        return pathList;
    }
}
