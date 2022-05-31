package it.unibz.examproject.passwordsecurity;

import it.unibz.examproject.util.db.PasswordSecurity;
import org.apache.commons.codec.DecoderException;
import org.junit.Before;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.Assert.*;

public class PasswordSecurityUnitTests {
    String password = "QTRYt3e!KsFft95";
    String wrongPassword = "ELKf3jCiAD6aBn!";


    @Test
    public void testPasswordValidator() throws NoSuchAlgorithmException, InvalidKeySpecException, DecoderException {
        String hashedPassword = PasswordSecurity.createHash(password);
        assertTrue("PASSED: RIGHT PASSWORD", PasswordSecurity.validatePassword(password, hashedPassword));
        assertFalse("FAILURE: WRONG PASSWORD ACCEPTED!", PasswordSecurity.validatePassword(wrongPassword, hashedPassword));
    }

    @Test
    public void testHashesUniqueness() throws NoSuchAlgorithmException, InvalidKeySpecException {
        assertNotEquals("Two hashed password of the same value should not be the same",
                PasswordSecurity.createHash(password), PasswordSecurity.createHash(password));
    }

    /**
     * Test passwordValidator even with unique hashes but of the same password value
     */
    @Test
    public void testPasswordValidatorWithUniqueHashes() throws NoSuchAlgorithmException, InvalidKeySpecException, DecoderException {
        String firstHash = PasswordSecurity.createHash(password);
        String secondHash = PasswordSecurity.createHash(password);
        String thirdHash = PasswordSecurity.createHash(password);
        assertTrue("PASSED: FIRST PASSWORD", PasswordSecurity.validatePassword(password, firstHash));
        assertTrue("PASSED: SECOND PASSWORD", PasswordSecurity.validatePassword(password, secondHash));
        assertTrue("PASSED: THIRD PASSWORD", PasswordSecurity.validatePassword(password, thirdHash));

        //With wrong password
        assertFalse("FAILURE: FIRST PASSWORD ", PasswordSecurity.validatePassword(wrongPassword, firstHash));
        assertFalse("FAILURE: SECOND PASSWORD ", PasswordSecurity.validatePassword(wrongPassword, secondHash));
        assertFalse("FAILURE: THIRD PASSWORD ", PasswordSecurity.validatePassword(wrongPassword, thirdHash));
    }
}
