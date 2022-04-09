# getrandom() SecureRandomSPI [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.marschall/getentropy-provider/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.marschall/getentropy-provider)  [![Javadocs](https://www.javadoc.io/badge/com.github.marschall/getentropy-provider.svg)](https://www.javadoc.io/doc/com.github.marschall/getentropy-provider)

A `SecureRandomSPI` that makes [getentropy()](https://man.openbsd.org/OpenBSD-current/man2/getentropy.2) system call available to `SecureRandom`.

* uses stack allocation rather than allocation on the C heap
* is marked as thread safe so concurrent access through `SecureRandom` will not synchronize in Java 9 and later, however there there may still be a kernel lock
* unlike the `NativePRNG` variants
  * does not use a file handle
  * does not have a global lock, but see comments on the kernel lock above
  * does not additionally mix with `SHA1PRNG`
  * zeros out native memory
* supports the ServiceLoader mechanism
* is a Java 9 module but works on Java 8
* no dependencies outside the `java.base` module

## Usage

A instance of the provider can be acquired using

```java
SecureRandom.getInstance("getentropy"); // GetentropyProvider.NAME
```

## Configuration

The provider can be configured in two different ways

1. programmatic configuration
1. static configuration

For best startup performance it is recommended to extract the .so from the JAR and add it to a folder present in the `LD_LIBRARY_PATH` environment variable or the `java.library.path` system property. Otherwise this library will extract the .so to a temporary folder the first time it is called.

We only ship `.so` files for Linux AMD64, other platforms like macOS and AArch64 are supported but you need to compile from source.

### Programmatic Configuration

The provider can be registered programmatically using

```java
Security.addProvider(new GetentropyProvider());
```

### Static Configuration Java 8

The provider can be configured statically in the `java.security` file by adding the provider at the end

```
security.provider.N=com.github.marschall.getentropy.GetentropyProvider
```

`N` should be the value of the last provider incremented by 1. For Oracle/OpenJDK 8 on Linux `N` should likely be 10.

This can be done [per JVM installation](https://docs.oracle.com/en/java/javase/11/security/howtoimplaprovider.html#GUID-831AA25F-F702-442D-A2E4-8DA6DEA16F33) or [per JVM Instance](https://docs.oracle.com/en/java/javase/11/security/java-authentication-and-authorization-service-jaas-reference-guide.html#GUID-106F4B32-B9A3-4B75-BDBF-29B252BB3F53).

Note that for this to work the provider JAR needs to be in the class path or extension folder.

### Static Configuration Java 9+

The provider can be configured statically in the `java.security` file by adding the provider at the end

```
security.provider.N=getentropy
```

`N` should be the value of the last provider incremented by 1. For Oracle/OpenJDK 9 on Linux `N` should likely be 13, on macOS `N` should likely be 14.

This can be done [per JVM installation](https://docs.oracle.com/javase/9/security/howtoimplaprovider.htm#GUID-831AA25F-F702-442D-A2E4-8DA6DEA16F33) or [per JVM Instance](https://dzone.com/articles/how-override-java-security).

The provider uses the ServiceLoader mechanism therefore using the `getrandom` string is enough, there is no need to use the fully qualified class name.

Note that for this to work the provider JAR needs to be in the class path or module path.

