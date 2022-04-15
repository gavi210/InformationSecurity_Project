package it.unibz.examproject.db;

import jakarta.servlet.ServletContext;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * the class is used to initialize the database connection
 */
public class DatabaseConnection {
    public static Connection initializeDatabase(InputStream inputStream) throws ClassNotFoundException, SQLException, IOException {
        Properties prop = getProperties(inputStream);

        Class.forName(prop.getProperty("db.driverClass"));

        Properties connectionProps = new Properties();
        connectionProps.put("user", prop.getProperty("db.user"));
        connectionProps.put("password", prop.getProperty("db.password"));

        Connection conn = DriverManager.getConnection(prop.getProperty("db.url"), connectionProps);

        return conn;
    }

    private static Properties getProperties(InputStream inputStream) throws IOException {
        try {

            Properties prop = new Properties();

            // load a properties file
            prop.load(inputStream);

            return prop;

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}


