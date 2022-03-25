module com.github.marschall.getentropy {
  provides java.security.Provider with com.github.marschall.getentropy.GetentropyProvider;
  exports com.github.marschall.getentropy;
}
