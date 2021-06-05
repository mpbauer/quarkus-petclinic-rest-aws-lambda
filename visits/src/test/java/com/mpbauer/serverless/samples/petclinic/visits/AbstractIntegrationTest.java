package com.mpbauer.serverless.samples.petclinic.visits;

import com.mpbauer.serverless.samples.petclinic.visits.security.Roles;
import io.smallrye.jwt.build.Jwt;

public abstract class AbstractIntegrationTest {

    protected String generateValidOwnerAdminToken() {
        return Jwt
                .subject("X00001")
                .groups(Roles.OWNER_ADMIN).sign();
    }

    protected String generateValidVetAdminToken() {
        return Jwt
                .subject("X00002")
                .groups(Roles.VET_ADMIN)
                .sign();
    }

    protected String generateValidAdminToken() {
        return Jwt
                .subject("X00003")
                .groups(Roles.ADMIN)
                .sign();
    }
}
