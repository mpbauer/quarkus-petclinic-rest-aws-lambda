package com.mpbauer.serverless.samples.petclinic.users.repository;


import com.mpbauer.serverless.samples.petclinic.users.model.User;

public interface UserRepository {

    void save(User user);
}
