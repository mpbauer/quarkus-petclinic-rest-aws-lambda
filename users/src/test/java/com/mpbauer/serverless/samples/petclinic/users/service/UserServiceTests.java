package com.mpbauer.serverless.samples.petclinic.users.service;

import com.mpbauer.serverless.samples.petclinic.users.config.QuarkusDataSourceProvider;
import com.mpbauer.serverless.samples.petclinic.users.model.User;
import com.radcortez.flyway.test.annotation.DataSource;
import com.radcortez.flyway.test.annotation.FlywayTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@QuarkusTest
@FlywayTest(@DataSource(QuarkusDataSourceProvider.class))
class UserServiceTests {

    @Inject
    UserService userService;

    @Test
    void shouldAddUser() throws Exception {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEnabled(true);
        user.addRole("OWNER_ADMIN");

        userService.saveUser(user);
        assertThat(user.getRoles().parallelStream().allMatch(role -> role.getName().startsWith("ROLE_")), is(true));
        assertThat(user.getRoles().parallelStream().allMatch(role -> role.getUser() != null), is(true));
    }
}
