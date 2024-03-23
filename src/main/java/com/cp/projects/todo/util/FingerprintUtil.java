package com.cp.projects.todo.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Component;

@Component
public class FingerprintUtil {
  private SecureRandom random;

  public FingerprintUtil() {
    this.random = new SecureRandom();
  }

  public String getFingerprint() {
    byte[] randomFingerprint = new byte[50];
    random.nextBytes(randomFingerprint);
    return Base64.getEncoder().encodeToString(randomFingerprint);
  }

  public String getEncryptedFingerprint(String fingerprint)
      throws NoSuchAlgorithmException, UnsupportedEncodingException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] fingerprintDigest = digest.digest(fingerprint.getBytes("utf-8"));
    return Base64.getEncoder().encodeToString(fingerprintDigest);
  }
}
