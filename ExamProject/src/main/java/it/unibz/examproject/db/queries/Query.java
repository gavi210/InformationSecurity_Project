package it.unibz.examproject.db.queries;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Query {

    protected Connection conn;

    public Query(Connection conn) {
        this.conn = conn;
    }

    public abstract ResultSet executeQuery() throws SQLException;
}
