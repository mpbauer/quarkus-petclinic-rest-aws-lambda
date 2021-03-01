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
package com.mpbauer.serverless.samples.petclinic.specialties.service;

import com.mpbauer.serverless.samples.petclinic.specialties.model.Specialty;
import com.mpbauer.serverless.samples.petclinic.specialties.repository.SpecialtyRepository;
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
public class SpecialtyServiceImpl implements SpecialtyService {

    private static final Logger LOG = LoggerFactory.getLogger(SpecialtyServiceImpl.class);

    SpecialtyRepository specialtyRepository;

    @Inject
    public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    @Override
    @Transactional
    public Specialty findSpecialtyById(int specialtyId) {
        Specialty specialty = null;
        try {
            specialty = specialtyRepository.findById(specialtyId);
        } catch (Exception e) {
            LOG.warn("An unexpected exception occured", e);

            // TODO try to catch a more generic exception to not accidentally swallow important exceptions
            // just ignore not found exceptions for Jdbc/Jpa realization
            return null;
        }
        return specialty;
    }

    @Override
    @Transactional
    public Collection<Specialty> findAllSpecialties() {
        return specialtyRepository.findAll();
    }

    @Override
    @Transactional
    public void saveSpecialty(Specialty specialty) {
        specialtyRepository.save(specialty);
    }

    @Override
    @Transactional
    public void deleteSpecialty(Specialty specialty) {
        specialtyRepository.delete(specialty);
    }
}
