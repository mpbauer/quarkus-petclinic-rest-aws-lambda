package com.mpbauer.serverless.samples.petclinic.vets.repository;


import com.mpbauer.serverless.samples.petclinic.vets.model.User;

public interface UserRepository {

    void save(User user);
}
