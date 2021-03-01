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
package com.mpbauer.serverless.samples.petclinic.vets.service;

import com.mpbauer.serverless.samples.petclinic.vets.model.Vet;
import com.mpbauer.serverless.samples.petclinic.vets.repository.VetRepository;
import io.quarkus.cache.CacheResult;
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
public class VetServiceImpl implements VetService {

    private static final Logger LOG = LoggerFactory.getLogger(VetServiceImpl.class);


    VetRepository vetRepository;

    @Inject
    public VetServiceImpl(VetRepository vetRepository) {
        this.vetRepository = vetRepository;
    }

    @Override
    @Transactional
    public Vet findVetById(int id) {
        Vet vet = null;
        try {
            vet = vetRepository.findById(id);
        } catch (Exception e) {
            LOG.warn("An unexpected exception occured", e);
            // TODO try to catch a more generic exception to not accidentally swallow important exceptions
            // just ignore not found exceptions for Jdbc/Jpa realization
            return null;
        }
        return vet;
    }

    @Override
    @Transactional
    public Collection<Vet> findAllVets() {
        return vetRepository.findAll();
    }

    @Override
    @Transactional
    public void saveVet(Vet vet) {
        vetRepository.save(vet);
    }

    @Override
    @Transactional
    public void deleteVet(Vet vet) {
        vetRepository.delete(vet);
    }

    @Override
    @Transactional
    @CacheResult(cacheName = "vets")
    public Collection<Vet> findVets() {
        return vetRepository.findAll();
    }
}
