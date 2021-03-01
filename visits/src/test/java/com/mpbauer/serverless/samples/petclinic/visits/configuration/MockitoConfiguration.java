package com.mpbauer.serverless.samples.petclinic.visits.configuration;

import org.mockito.configuration.DefaultMockitoConfiguration;

public class MockitoConfiguration extends DefaultMockitoConfiguration {

  @Override
  public boolean enableClassCache() {
    return false;
  }
}
