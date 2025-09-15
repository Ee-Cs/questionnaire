package kp.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * The tripcode utils were proposed by Copilot.
 */
public class TripcodeUtils {
    private static final String SEPARATOR = "=";

    /**
     * Creates the user tripcode signature string in the format:
     * username + SEPARATOR + hash(username+secret+salt).
     *
     * @param username the username
     * @param secret   the secret
     * @return the signature string
     */
    public static String createSignature(String username, String secret) {

        if (username.isBlank() || secret.isBlank()) {
            return "";
        }
        final String saltStr = Base64.getEncoder().encodeToString(generateSalt());
        // Combine username, secret, and salt for hashing
        final String hash = hashSHA256(username + secret + saltStr);
        return username + SEPARATOR + hash;
    }

    /**
     * Generate a random salt (16 bytes).
     *
     * @return the salt bytes
     */
    private static byte[] generateSalt() {

        final byte[] saltBytes = new byte[16];
        new SecureRandom().nextBytes(saltBytes);
        return saltBytes;
    }

    /**
     * Produce the SHA-256 hash of the input string, returns as hex.
     *
     * @param input the input
     * @return the hash
     */
    private static String hashSHA256(String input) {

        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hashedBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    /**
     * Convert bytes to hex string.
     *
     * @param bytes the byte array
     * @return hex string
     */
    private static String bytesToHex(byte[] bytes) {

        final StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString();
    }
}