package com.github.marschall.getentropy;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.security.Provider;
import java.security.Provider.Service;
import java.security.Security;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;


class AppleProviderTests {

  @Test
  void provider() {
    Provider appleProvider = Security.getProvider("Apple");
    assumeTrue(appleProvider != null);

    for (Entry<Object, Object> entry : appleProvider.entrySet()) {
      System.out.println(entry.getKey() + ": " + entry.getValue());
    }

    for (Service service : appleProvider.getServices()) {
      System.out.println(service.getAlgorithm());
    }
  }

}
