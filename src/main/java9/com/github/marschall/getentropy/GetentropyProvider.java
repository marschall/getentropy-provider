package com.github.marschall.getentropy;

import java.security.Provider;

/**
 * A security provider that installs one random number generation
 * algorithm that use the <a href="https://man7.org/linux/man-pages/man3/getentropy.3.html">getentropy()</a>
 * system call.
 *
 * @see <a href="https://docs.oracle.com/en/java/javase/17/security/howtoimplaprovider.html">How to Implement a Provider in the Java Cryptography Architecture</a>
 */
public final class GetentropyProvider extends Provider {

  /**
   * The name of this security provider.
   */
  public static final String NAME = "getentropy";

  /**
   * The name of the algorithm that uses getentropy().
   */
  public static final String GETENTROPY = "getentropy";

  private static final long serialVersionUID = 1L;

  public GetentropyProvider() {
    super(NAME, Getentropy.getVersion(), "getentropy (SecureRandom)");
    this.put("SecureRandom." + GETENTROPY, GetentropySecureRandom.class.getName());
    this.put("SecureRandom." + GETENTROPY + " ThreadSafe", "true");
  }

}
