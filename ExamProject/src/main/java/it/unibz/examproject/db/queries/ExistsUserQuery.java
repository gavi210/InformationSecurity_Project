package it.unibz.examproject.db.queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExistsUserQuery extends Query {

    private String email;

    public ExistsUserQuery(Connection connection, String email) {
        super(connection);
        this.email = email;
    }

    public String getQueryString() {
        return String.format("SELECT * FROM [user] WHERE email= ?", this.email);
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        PreparedStatement p = conn.prepareStatement(this.getQueryString());
        p.setString(1, this.email);
        return p.executeQuery();
    }
}
