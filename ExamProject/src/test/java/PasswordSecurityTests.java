import it.unibz.examproject.util.db.PasswordSecurity;
import org.apache.commons.codec.DecoderException;
import org.junit.Before;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PasswordSecurityTests {
    String password = "QTRYt3e!KsFft95";
    String wrongPassword = "ELKf3jCiAD6aBn!";
    private PasswordSecurity passwordSecurity;

    @Before
    public void setUp() {
        passwordSecurity = new PasswordSecurity();
    }

    @Test
    public void testPasswordValidator() throws NoSuchAlgorithmException, InvalidKeySpecException, DecoderException {
        String hashedPassword = passwordSecurity.createHash(password);
        assertTrue("PASSED: RIGHT PASSWORD", passwordSecurity.validatePassword(password, hashedPassword));
        assertFalse("FAILURE: WRONG PASSWORD ACCEPTED!", passwordSecurity.validatePassword(wrongPassword, hashedPassword));
    }

    @Test
    public void testHashesUniqueness() throws NoSuchAlgorithmException, InvalidKeySpecException {
        assertNotEquals("Two hashed password of the same value should not be the same",
                passwordSecurity.createHash(password), passwordSecurity.createHash(password));
    }

    /**
     * Test passwordValidator even with unique hashes but of the same password value
     */
    @Test
    public void testPasswordValidatorWithUniqueHashes() throws NoSuchAlgorithmException, InvalidKeySpecException, DecoderException {
        String firstHash = passwordSecurity.createHash(password);
        String secondHash = passwordSecurity.createHash(password);
        String thirdHash = passwordSecurity.createHash(password);
        assertTrue("PASSED: FIRST PASSWORD", passwordSecurity.validatePassword(password, firstHash));
        assertTrue("PASSED: SECOND PASSWORD", passwordSecurity.validatePassword(password, secondHash));
        assertTrue("PASSED: THIRD PASSWORD", passwordSecurity.validatePassword(password, thirdHash));

        //With wrong password
        assertFalse("FAILURE: FIRST PASSWORD ", passwordSecurity.validatePassword(wrongPassword, firstHash));
        assertFalse("FAILURE: SECOND PASSWORD ", passwordSecurity.validatePassword(wrongPassword, secondHash));
        assertFalse("FAILURE: THIRD PASSWORD ", passwordSecurity.validatePassword(wrongPassword, thirdHash));
    }
}
