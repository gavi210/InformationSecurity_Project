package it.unibz.examproject.util.db;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.security.SecureRandom;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class PasswordSecurity {
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int SALT_BYTES = 16;
    private static final int PBKDF2_ITERATIONS = 10000;
    private static final int KEY_LENGTH = 128;


    /**
     * @param password the password to hash.
     * @param salt     the salt
     * @return the PBDKF2 hash of the password
     */
    private static byte[] hashPassword(char[] password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, PBKDF2_ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
        return factory.generateSecret(spec).getEncoded();
    }

    /**
     * Creates a PBKDF2 hash with salt.
     *
     * @param password the password to hash
     * @return a salted PBKDF2 hash of the password
     */
    public static String createHash(String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        char[] passwordToChar = password.toCharArray();

        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);
        byte[] hash = hashPassword(passwordToChar, salt);

        // format salt&hash
        return encodeHexToString(salt) + "&" + encodeHexToString(hash);
    }

    /**
     * Validates a password using a hash.
     *
     * @param password     the password to check
     * @param hashPassword the hash of the valid password
     * @return true if the password is correct, false if not
     */
    public static boolean validatePassword(String password, String hashPassword)
            throws NoSuchAlgorithmException, InvalidKeySpecException, DecoderException {
        char[] passwordToChar = password.toCharArray();
        String[] hashes = hashPassword.split("&");
        byte[] salt = decodeHexString(hashes[0]);
        byte[] hash = decodeHexString(hashes[1]);
        byte[] testHash = hashPassword(passwordToChar, salt);
        return byteEquals(hash, testHash);
    }

    /**
     * Compares two byte arrays in length-constant time. This comparison method
     * is used so that password hashes cannot be extracted from an on-line
     * system using a timing attack and then attacked off-line.
     *
     * @param a the first byte array
     * @param b the second byte array
     * @return true if both byte arrays are the same, false if not
     */
    private static boolean byteEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        int i = 0;
        while (i < a.length && i < b.length) {
            diff |= a[i] ^ b[i];
            i++;
        }
        return diff == 0;
    }


    /**
     * Converts an array of bytes into a string of hexadecimal values using the Apache Commons Hex Class.
     *
     * @param bytes the byte array to convert
     * @return the hex string decoded into a byte array
     */
    private static String encodeHexToString(byte[] bytes) {
        return Hex.encodeHexString(bytes);
    }

    /**
     * Converts a string of hexadecimal values into an array of bytes using the Apache Commons Hex Class.
     *
     * @param hexString the hex string
     * @return an array of bytes encoded in a hex string
     */
    private static byte[] decodeHexString(String hexString)
            throws DecoderException {
        return Hex.decodeHex(hexString.toCharArray());
    }
}


