package com.github.marschall.getentropy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

final class Getentropy {

  static {
    String version = getVersion();
    String libraryName = "getentropy-provider-" + version;
    try {
      Runtime.getRuntime().loadLibrary(libraryName);
    } catch (UnsatisfiedLinkError e) {
      // the library is not in the library path
      // extract it from the JAR and load it from there
      String fileName = System.mapLibraryName(libraryName);
      Path extracted = extractLibrary(fileName);
      try {
        System.load(extracted.toAbsolutePath().toString());
      } finally {
        // under Linux can be deleted after loading
        try {
          Files.delete(extracted);
        } catch (IOException e1) {
          throw new AssertionError("could not delete temp file: " + fileName, e);
        }
      }
    }
  }

  private static String getVersion() {
    String fileName = "getentropy-provider.version";
    try (InputStream stream = Getentropy.class.getClassLoader().getResourceAsStream(fileName)) {
      if (stream == null) {
        throw new AssertionError("could not load resource: " + fileName);
      }
      ByteArrayOutputStream bos = new ByteArrayOutputStream(16);
      byte[] buffer = new byte[16];
      int read = 0;
      do {
        if (read > 0) {
          bos.write(buffer, 0, read);
        }
        read = stream.read(buffer);
      } while (read != -1);
      return new String(bos.toByteArray(), StandardCharsets.US_ASCII);
    } catch (IOException e) {
      throw new AssertionError("could not load file: " + fileName, e);
    }
  }

  private static Path extractLibrary(String fileName) {
    Path tempFile;
    try {
      tempFile = Files.createTempFile("getentropy-provider-", ".so");
    } catch (IOException e) {
      throw new AssertionError("could not create temp file", e);
    }
    try (OutputStream output = Files.newOutputStream(tempFile);
         InputStream input = Getentropy.class.getClassLoader().getResourceAsStream(fileName)) {
      if (input == null) {
        throw new AssertionError("could not load resource: " + fileName);
      }
      byte[] buffer = new byte[8192];
      int read = 0;
      do {
        if (read > 0) {
          output.write(buffer, 0, read);
        }
        read = input.read(buffer);
      } while (read != -1);
    } catch (IOException e) {
      throw new AssertionError("could copy to temp file: " + fileName, e);
    }
    return tempFile;
  }

  private static final int EFAULT = 14;
  private static final int EIO = 5;
  private static final int EINVAL = 22;

  private Getentropy() {
    throw new AssertionError("not instantiable");
  }

  static void getentropy(byte[] bytes) {
    Objects.requireNonNull(bytes);
    int exitCode = getentropy0(bytes, bytes.length);
    if (exitCode != 0) {
      switch (exitCode) {
        case EFAULT:
          throw new IllegalStateException("Part or all of the buffer specified by buffer and length is not in valid addressable memory.");
        case EIO:
        case EINVAL:
          throw new IllegalStateException("Too many bytes requested, or some other fatal error occurred.");
        default:
          throw new IllegalStateException("Encountered unknown exit code: " + exitCode + ".");
      }
    }
  }


  // https://man7.org/linux/man-pages/man3/getentropy.3.html
  private static native int getentropy0(byte[] bytes, int length);

}
