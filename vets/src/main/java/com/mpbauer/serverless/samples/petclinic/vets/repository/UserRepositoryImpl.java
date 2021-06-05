package com.mpbauer.serverless.samples.petclinic.vets.repository;


import com.mpbauer.serverless.samples.petclinic.vets.model.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ApplicationScoped
public class UserRepositoryImpl implements UserRepository {

    @Inject
    @PersistenceContext
    EntityManager em;

    @Override
    public void save(User user) {
        if (this.em.find(User.class, user.getUsername()) == null) {
            this.em.persist(user);
        } else {
            this.em.merge(user);
        }
    }
}
