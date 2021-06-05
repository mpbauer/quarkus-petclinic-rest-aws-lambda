package com.mpbauer.serverless.samples.petclinic.users.service;


import com.mpbauer.serverless.samples.petclinic.users.model.User;

public interface UserService {

    void saveUser(User user) throws Exception;
}
