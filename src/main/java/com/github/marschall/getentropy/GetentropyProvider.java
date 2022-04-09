package com.github.marschall.getentropy;

import java.security.Provider;

/**
 * A security provider that installs one random number generation
 * algorithm that use the <a href="https://man.openbsd.org/OpenBSD-current/man2/getentropy.2">getentropy()</a>
 * system call.
 *
 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/HowToImplAProvider.html">How to Implement a Provider in the Java Cryptography Architecture</a>
 */
public final class GetentropyProvider extends Provider {
  //https://gist.github.com/nicerobot/1536232

  /**
   * The name of this security provider.
   */
  public static final String NAME = "getentropy";

  /**
   * The name of the algorithm that uses getentropy().
   */
  public static final String GETENTROPY = "getentropy";

  private static final long serialVersionUID = 1L;

  /**
   * Default constructor.
   */
  public GetentropyProvider() {
    super(NAME, 0.1d, "getentropy (SecureRandom)");
    this.put("SecureRandom." + GETENTROPY, GetentropySecureRandom.class.getName());
    this.put("SecureRandom." + GETENTROPY + " ThreadSafe", "true");
  }

}
