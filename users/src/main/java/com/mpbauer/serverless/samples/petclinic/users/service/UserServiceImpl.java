package com.mpbauer.serverless.samples.petclinic.users.service;

import com.mpbauer.serverless.samples.petclinic.users.model.Role;
import com.mpbauer.serverless.samples.petclinic.users.model.User;
import com.mpbauer.serverless.samples.petclinic.users.repository.UserRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    @Inject
    UserRepository userRepository;

    @Override
    @Transactional
    public void saveUser(User user) throws Exception {

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            throw new Exception("User must have at least a role set!");
        }

        for (Role role : user.getRoles()) {
            if (!role.getName().startsWith("ROLE_")) {
                role.setName("ROLE_" + role.getName());
            }

            if (role.getUser() == null) {
                role.setUser(user);
            }
        }

        userRepository.save(user);
    }
}
