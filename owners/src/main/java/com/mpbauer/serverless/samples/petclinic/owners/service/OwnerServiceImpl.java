/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mpbauer.serverless.samples.petclinic.owners.service;

import com.mpbauer.serverless.samples.petclinic.owners.model.Owner;
import com.mpbauer.serverless.samples.petclinic.owners.repository.OwnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Collection;

/**
 * Mostly used as a facade for all Petclinic controllers
 * Also a placeholder for @Transactional and @Cacheable annotations
 *
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
@ApplicationScoped
public class OwnerServiceImpl implements OwnerService {

    private static final Logger LOG = LoggerFactory.getLogger(OwnerServiceImpl.class);

    OwnerRepository ownerRepository;

    @Inject
    public OwnerServiceImpl(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Override
    @Transactional
    public Collection<Owner> findAllOwners() {
        return ownerRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteOwner(Owner owner) {
        ownerRepository.delete(owner);
    }

    @Override
    @Transactional
    public Owner findOwnerById(int id) {
        Owner owner = null;
        try {
            owner = ownerRepository.findById(id);
        } catch (Exception e) {
            LOG.warn("An unexpected exception occured", e);

            // TODO try to catch a more generic exception to not accidentally swallow important exceptions
            // just ignore not found exceptions for Jdbc/Jpa realization
            return null;
        }
        return owner;
    }

    @Override
    @Transactional
    public void saveOwner(Owner owner) {
        ownerRepository.save(owner);

    }

    @Override
    @Transactional
    public Collection<Owner> findOwnerByLastName(String lastName) {
        return ownerRepository.findByLastName(lastName);
    }
}
