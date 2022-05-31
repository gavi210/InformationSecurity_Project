package it.unibz.examproject.passwordsecurity;

import it.unibz.examproject.util.db.PasswordSecurity;
import it.unibz.examproject.util.db.PostgresRepository;
import it.unibz.examproject.util.db.Repository;
import it.unibz.examproject.util.db.SQLServerRepository;
import org.junit.Test;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PasswordSecurityDBInteractionUnitTest {
    Repository repository;
    @Test
    public void registerNewUser() throws IOException, SQLException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {Properties configProperties = new Properties();
        configProperties.load(new FileReader("src/test/resources/dbConfig.properties"));

        String dbms = configProperties.getProperty("db.dbms");
        if ("postgres".equals(dbms))
            this.repository = new PostgresRepository();
        else
            this.repository = new SQLServerRepository();

        this.repository.init(configProperties);

        //this.repository.registerNewUser("Riccardo","Rigoni","rccrd.rigoni@gmail.com","123456");
        assertFalse(this.repository.areCredentialsValid("rccrd.rigoni@gmail.com", "wrong"));
        assertTrue(this.repository.areCredentialsValid("rccrd.rigoni@gmail.com", "123456"));
    }
}
