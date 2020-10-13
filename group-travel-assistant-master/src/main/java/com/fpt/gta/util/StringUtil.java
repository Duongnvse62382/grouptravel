package com.fpt.gta.util;

import java.security.SecureRandom;
import java.util.Base64;

public final class StringUtil {
    public static String generateRandomBase64Token(int byteLength) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[byteLength];
        secureRandom.nextBytes(token);
        return Base64.getUrlEncoder().encodeToString(token); //base64 encoding
    }

    public static String encodeBase64(String origin) {
        return Base64.getUrlEncoder().encodeToString(origin.getBytes());
    }

    public static String decodeBase64(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }
}
