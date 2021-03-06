package it.unibz.examproject.util.db;

public class PostgresRepository extends Repository {

    @Override
    protected String getExistsUserByEmailQueryString() {
        return "SELECT * FROM \"user\" WHERE email= ?";
    }

    @Override
    protected String getPasswordGivenEmailQueryString() {
        return "SELECT password FROM \"user\" WHERE email= ?";
    }

    @Override
    protected String getSendEmailQueryString() {
        return "INSERT INTO mail ( sender, receiver, subject, body, \"time\", signature ) "
                + "VALUES ( ?, ?, ?, ?, ?, ? )";
    }

    @Override
    protected String getIncomingEmailsQueryString() {
        return "SELECT sender, receiver, subject, body, \"time\", signature FROM mail WHERE receiver = ? ORDER BY \"time\" DESC";
    }

    @Override
    protected String getRegisterNewUserQueryString() {
        return "INSERT INTO \"user\" ( name, surname, email, password, val, n ) "
                + "VALUES ( ?, ?, ?, ?, ?, ? )";
    }

    @Override
    protected String getSentEmailsQueryString() {
        return "SELECT sender, receiver, subject, body, \"time\", signature FROM mail WHERE sender = ? ORDER BY \"time\" DESC";
    }

    @Override
    protected String getResetDatabaseQueryString() {
        return "DELETE FROM mail; DELETE from \"user\"";
    }

    @Override
    protected String getUserPublicKetQueryString() {
        return "SELECT val, n FROM \"user\" WHERE email = ?";
    }
}
