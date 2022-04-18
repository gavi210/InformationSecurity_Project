package it.unibz.examproject.db.queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SentEmailsQuery extends Query{
    private String email;

    public SentEmailsQuery(Connection conn, String email) {
        super(conn);
        this.email = email;
    }

    private String getQueryString() {
        return "SELECT * FROM mail WHERE sender= ? ORDER BY [time] DESC";
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        PreparedStatement p = conn.prepareStatement(this.getQueryString());
        p.setString(1, this.email);
        return p.executeQuery();
    }
}
