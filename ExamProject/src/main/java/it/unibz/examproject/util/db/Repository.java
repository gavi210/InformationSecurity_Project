package it.unibz.examproject.util.db;

import org.apache.commons.codec.DecoderException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.*;

public abstract class Repository {

    private Properties connectionProperties;
    protected Connection connection;
    private PasswordSecurity passwordSecurity;


    /**
     * set up the database connection. Should be the first method invoked before any other interaction with the repository
     *
     * @param connectionProperties should contain the driver to be used, the username and password and the connection URL
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void init(Properties connectionProperties) throws SQLException, ClassNotFoundException {
        this.connectionProperties = connectionProperties;
        this.connection = getConnection();
        this.passwordSecurity = new PasswordSecurity();
    }

    /**
     * instantiate the connection at Repository Instantiation
     */
    private Connection getConnection() throws ClassNotFoundException, SQLException {
        loadDriver(this.connectionProperties.getProperty("db.driverClass"));

        Properties connectionProps = new Properties();

        connectionProps.put("user", this.connectionProperties.getProperty("db.user"));
        connectionProps.put("password", this.connectionProperties.getProperty("db.password"));

        return DriverManager.getConnection(this.connectionProperties.getProperty("db.url"), connectionProps);
    }

    private void loadDriver(String driver) throws ClassNotFoundException {
        Class.forName(driver);
    }

    /**
     * it follows the list of all abstract methods being implemented to support the queries on the different databases
     * finish implementing all the queries needed for the other database, then try to see whether postgres works as well.
     */

    protected abstract String getExistsUserByEmailQueryString();

    protected abstract String getPasswordGivenEmailQueryString();

    protected abstract String getSendEmailQueryString();

    protected abstract String getIncomingEmailsQueryString();

    protected abstract String getRegisterNewUserQueryString();

    protected abstract String getSentEmailsQueryString();

    public boolean emailAlreadyInUse(String email) {
        String sql = getExistsUserByEmailQueryString();
        try (PreparedStatement p = connection.prepareStatement(sql)) {
            p.setString(1, email);
            ResultSet res = p.executeQuery();

            return res.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean areCredentialsValid(String email, String password) {
        String sql = getPasswordGivenEmailQueryString();

        try (PreparedStatement p = connection.prepareStatement(sql)) {
            String pwdHash = passwordSecurity.createHash(password);
            p.setString(1, email);
            p.setString(2, password);
            ResultSet res = p.executeQuery();

            if (!res.next())
                return false;

            String referencePassword = res.getString(1);
            return this.passwordSecurity.validatePassword(password, referencePassword);
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException | DecoderException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void sendNewMail(String sender, String receiver, String subject, String body, String timestamp) {
        String sql = getSendEmailQueryString();
        try (PreparedStatement p = connection.prepareStatement(sql)) {
            p.setString(1, sender);
            p.setString(2, receiver);
            p.setString(3, subject);
            p.setString(4, body);
            p.setString(5, timestamp);
            p.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Email> getReceivedEmails(String email) {
        List<Email> inbox = new ArrayList<>();

        String sql = getIncomingEmailsQueryString();
        return getEmails(email, inbox, sql);
    }

    public void registerNewUser(String name, String surname, String email, String password) {
        String sql = getRegisterNewUserQueryString();
        try (PreparedStatement p = connection.prepareStatement(sql)) {
            p.setString(1, name);
            p.setString(2, surname);
            p.setString(3, email);
            p.setString(4, this.passwordSecurity.createHash(password));
            p.execute();
        } catch (SQLException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public List<Email> getSentEmails(String email) throws SQLException {
        List<Email> sent = new ArrayList<>();

        String sql = getSentEmailsQueryString();
        return getEmails(email, sent, sql);
    }

    private List<Email> getEmails(String email, List<Email> sent, String sql) {
        try (PreparedStatement p = connection.prepareStatement(sql)) {
            p.setString(1, email);

            ResultSet res = p.executeQuery();
            while (res.next()) {
                String sender = res.getString(1);
                String receiver = res.getString(2);
                String subject = res.getString(3);
                String body = res.getString(4);
                String timestamp = res.getString(5);

                sent.add(new Email(sender, receiver, subject, body, timestamp));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sent;
    }
}
