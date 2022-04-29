package it.unibz.examproject.db.queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterQuery extends Query {
    private String name;
    private String surname;
    private String email;
    private String password;

    public RegisterQuery(Connection connection, String name, String surname, String email, String password) {
        super(connection);
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
    }

    public String getQueryString() {
        return "INSERT INTO [user] ( name, surname, email, password ) "
                + "VALUES ( ?, ?, ?, ? )";
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        PreparedStatement p = conn.prepareStatement(this.getQueryString());
        p.setString(1, this.name);
        p.setString(2, this.surname);
        p.setString(3, this.email);
        p.setString(4, this.password);
        p.execute();

        return null;
    }
}
