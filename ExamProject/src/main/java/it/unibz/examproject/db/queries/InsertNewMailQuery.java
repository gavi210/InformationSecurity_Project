package it.unibz.examproject.db.queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InsertNewMailQuery extends Query {
    private String sender;
    private String receiver;
    private String subject;
    private String body;
    private String timestamp;

    public InsertNewMailQuery(Connection conn, String sender, String receiver, String subject, String body, String timestamp) {
        super(conn);
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.body = body;
        this.timestamp = timestamp;
    }

    private String getQueryString() {
        return "INSERT INTO mail ( sender, receiver, subject, body, [time] ) "
                + "VALUES ( ?, ?, ?, ?, ? )";
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        PreparedStatement p = conn.prepareStatement(this.getQueryString());
        p.setString(1, this.sender);
        p.setString(2, this.receiver);
        p.setString(3, this.subject);
        p.setString(4, this.body);
        p.setString(5, this.timestamp);
        p.execute();

        return null;
    }
}
