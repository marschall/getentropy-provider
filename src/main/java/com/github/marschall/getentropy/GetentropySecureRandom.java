package com.github.marschall.getentropy;

import java.security.SecureRandomSpi;
import java.util.Objects;

/**
 * A {@link SecureRandomSpi}s that uses the {@code getentropy} system call.
 */
public final class GetentropySecureRandom extends SecureRandomSpi {

  private static final long serialVersionUID = 1L;

  /**
   * Default constructor for JCA, should not be called directly.
   */
  public GetentropySecureRandom() {
    super();
  }

  @Override
  protected void engineNextBytes(byte[] bytes) {
    Objects.requireNonNull(bytes);
    Getentropy.getentropy(bytes);
  }

  @Override
  protected void engineSetSeed(byte[] seed) {
    Objects.requireNonNull(seed);
    // ignore
  }

  @Override
  protected byte[] engineGenerateSeed(int numBytes) {
    if (numBytes < 0) {
      throw new IllegalArgumentException("numBytes must not be negative");
    }
    byte[] bytes = new byte[numBytes];
    if (numBytes > 0) {
      this.engineNextBytes(bytes);
    }
    return bytes;
  }

}
