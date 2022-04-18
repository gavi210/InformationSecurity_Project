package it.unibz.examproject.db.queries;

import java.sql.*;

public class LoginQuery extends Query{

    private String email;
    private String password;

    public LoginQuery(Connection connection, String email, String password) {
        super(connection);
        this.email = email;
        this.password = password;
    }

    public String getQueryString() {
        return "SELECT * FROM [user] WHERE email= ? AND password= ?";
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        PreparedStatement p = conn.prepareStatement(this.getQueryString());
        p.setString(1, this.email);
        p.setString(2, this.password);
        return p.executeQuery();
    }
}
