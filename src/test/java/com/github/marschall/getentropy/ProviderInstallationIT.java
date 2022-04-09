package com.github.marschall.getentropy;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.security.Security;

import org.junit.jupiter.api.Test;

class ProviderInstallationIT {

  @Test
  void getProvider() {
    assertNotNull(Security.getProvider(GetentropyProvider.NAME));
  }

}
