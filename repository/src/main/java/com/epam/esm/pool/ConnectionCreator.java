package com.epam.esm.pool;

import com.epam.esm.exception.ConnectionPoolException;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


class ConnectionCreator {
    /* Url of the database*/
    @Value("${jdbc.url}")
    private static String url = "jdbc:postgresql://localhost:5432/gift_certificates";
    /*Login of the user of database*/
    @Value("${jdbc.user}")
    private static String user = "postgres";
    /*Password of the user of database*/
    @Value("${jdbc.password}")
    private static String password = "root";

    /*
    * Creates new connection and returns ProxyConnection object
    * */
    static ProxyConnection getConnection() throws ConnectionPoolException {
        Connection connection;
        try {
            connection = DriverManager.getConnection(url, user, password);

        } catch (SQLException e) {
            throw new ConnectionPoolException(String.format("Connection was not created. Reason : %s", e.getMessage()), e);
        }
        return new ProxyConnection(connection);
    }
}
