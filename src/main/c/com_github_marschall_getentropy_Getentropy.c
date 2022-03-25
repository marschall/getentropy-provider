#include <jni.h>

//#include <unistd.h>           /* for getentropy() */
#include <sys/random.h>       /* for getentropy() */
#include <errno.h>            /* errno */
#include <string.h>           /* memset */

#include "com_github_marschall_getentropy_Getentropy.h"

/*
 * The maximum size of the stack-allocated buffer is the
 * same as maximum size supported by getentropy.
 */
#define BUFFER_SIZE 256

 _Static_assert (com_github_marschall_getentropy_Getentropy_EFAULT == EFAULT, "com_github_marschall_fetentropy_Getentropy_EFAULT == EFAULT");
 _Static_assert (com_github_marschall_getentropy_Getentropy_EIO == EIO, "com_github_marschall_getentropy_Getentropy_EIO == EIO");
// _Static_assert (com_github_marschall_getentropy_Getentropy_ENOSYSL == ENOSYS, "com_github_marschall_fetentropy_Getentropy_ENOSYS == ENOSYS");

JNIEXPORT jint JNICALL Java_com_github_marschall_getentropy_Getentropy_getentropy0
  (JNIEnv *env, jclass clazz, jbyteArray bytes, jint arrayLength)
{
  _Static_assert (sizeof(jbyte) == sizeof(char), "sizeof(jbyte) == sizeof(char)");

  char buffer[BUFFER_SIZE];
  ssize_t lastWritten = 0;
  ssize_t totalWritten = 0;
  int getEntropyErrorCode = 0;

  /* call getrandom until we have all the bytes or a call fails */
  size_t remaining = (size_t) arrayLength;
  do
  {
    size_t buflen;
    if (remaining > BUFFER_SIZE)
    {
      buflen = BUFFER_SIZE;
    }
    else
    {
      buflen = remaining;
    }
    lastWritten = getentropy(&buffer[totalWritten], buflen);
    if (lastWritten != -1)
    {
      /* copy from native to Java memory */
      (*env)->SetByteArrayRegion(env, bytes, (jsize) totalWritten, (jsize) buflen, (const jbyte *) buffer);
      
      /* error check */
      if ((*env)->ExceptionCheck(env) == JNI_TRUE)
      {
        /* doens't really matter, ArrayIndexOutOfBoundsException will be thrown upon returning */
        lastWritten = -1;
        getEntropyErrorCode = EFAULT;
      }
      remaining -= buflen;
    }
    else
    {
      getEntropyErrorCode = errno;
    }
  }
  while (lastWritten != -1 && remaining > 0);
  
  /* clean out the native memory */
  size_t toClear = BUFFER_SIZE;
  if (toClear > arrayLength)
  {
    toClear = (size_t) arrayLength;
  }
  memset(buffer, 0, toClear);

  if (lastWritten == -1)
  {
    /* exception will be raised by calling Java code */
    return getEntropyErrorCode;
  }
  else
  {
    return 0;
  }
}
