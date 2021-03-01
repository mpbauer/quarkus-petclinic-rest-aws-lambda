package com.mpbauer.serverless.samples.petclinic.users.configuration;

import org.mockito.configuration.DefaultMockitoConfiguration;

public class MockitoConfiguration extends DefaultMockitoConfiguration {

  @Override
  public boolean enableClassCache() {
    return false;
  }
}
