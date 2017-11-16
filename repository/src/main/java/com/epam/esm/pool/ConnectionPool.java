package com.epam.esm.pool;

import com.epam.esm.exception.ConnectionPoolException;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Enumeration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class ConnectionPool implements DataSource{
    @Value("${jdbc.driver}")
    private String driver = "org.postgre.Driver";
    @Value("${jdbc.encoding}")
    private String encoding = "UTF-8";

    private static final int POOL_SIZE = 10;
    /*Lock object for thread safe instantiating of connection pool*/
    private static Lock lock = new ReentrantLock();
    /*Blocking queue to store connections to database*/
    private BlockingQueue<ProxyConnection> connectionQueue;
    /*Instance of connection pool*/
    private static ConnectionPool instance;
    /*AtomicBoolean object for indicating if connection pool created*/
    private static AtomicBoolean isConnectionPoolCreated = new AtomicBoolean(false);

    /*Constructor of connection pool. Private to make it singleton*/
    private ConnectionPool() {
        connectionQueue = new ArrayBlockingQueue<>(POOL_SIZE);
        try {
            initConnectionPool();
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Connection pool was not created. Reason : %s", e.getMessage()), e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(String.format("Can not register driver. Reason : %s", e.getMessage()), e);
        }
    }

    /*Gets instance of connection pool*/
    public static ConnectionPool getInstance() {
        if (!isConnectionPoolCreated.get()) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new ConnectionPool();
                    isConnectionPoolCreated.set(true);
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    /* Initializes connection pool. If connection pool size less then half of required number of connections
    * RuntimeException is thrown*/
    private void initConnectionPool() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        for (int i = 0; i < POOL_SIZE; i++) {
            createConnectionAndAddToPool();
        }
        if (!isAllConnectionsCreated()) {
            tryToRecreateConnections();
            if (connectionQueue.size() < POOL_SIZE / 2) {
                throw new RuntimeException("Connection pool has not enough connections to proceed working");
            }
        }
    }

    /*Checks if all connections were created*/
    private boolean isAllConnectionsCreated() {
        return connectionQueue.size() == POOL_SIZE;
    }

    /* Gets connection from connection pool*/
    public ProxyConnection getConnection() {
        ProxyConnection connection = null;
        try {
            connection = connectionQueue.take();
        } catch (InterruptedException e) {
//            LOGGER.log(Level.ERROR, String.format("Can not get connection from pool. Reason : %s", e.getMessage()));
        }
        return connection;
    }

    /*Gets size of connection pool*/
    public int size() {
        return connectionQueue.size();
    }

    /*Returns connection to connection pool*/
    void releaseConnection(ProxyConnection connection) {
        connectionQueue.offer(connection);
    }

    /*Destroys all connections and deregisters drivers*/
    public void destroyConnections() {
        int size = connectionQueue.size();
        for (int i = 0; i < size; i++) {
            try {
                ProxyConnection connection = connectionQueue.take();
                connection.closeConnection();
            } catch (InterruptedException e) {
//                LOGGER.log(Level.ERROR, String.format("Can not take connection from pool. Reason : %s", e.getMessage()));
            } catch (SQLException e) {
//                LOGGER.log(Level.ERROR, String.format("Can not close connection. Reason : %s", e.getMessage()));
            }
        }
        try {
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                DriverManager.deregisterDriver(driver);
            }
        } catch (SQLException e) {
//            LOGGER.log(Level.ERROR, String.format("Can not deregister driver. Reason : %s", e.getMessage()));
        }
    }

    /*Creates connection and adds to connection pool*/
    private void createConnectionAndAddToPool() {
        try {
            ProxyConnection connection = ConnectionCreator.getConnection();
            connectionQueue.put(connection);
        } catch (ConnectionPoolException e) {
//            LOGGER.log(Level.ERROR, e.getMessage());
        } catch (InterruptedException e) {
//            LOGGER.log(Level.ERROR, String.format("Connection was not added to pool. Reason : %s", e.getMessage()));
        }
    }

    /*Tries to recreate connections, if not all were created */
    private void tryToRecreateConnections() {
        int difference = POOL_SIZE - connectionQueue.size();
        for (int i = 0; i < difference; i++) {
            createConnectionAndAddToPool();
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
