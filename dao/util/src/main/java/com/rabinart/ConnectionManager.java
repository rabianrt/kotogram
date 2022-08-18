package com.rabinart;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public final class ConnectionManager {

    private static final String POOL_SIZE_KEY = "db.pool.size";
    private static final String URL_KEY = "db.url";
    private static final String PASSWORD_KEY = "db.password";
    private static final String USER_KEY = "db.user";
    private static final String DRIVER_KEY = "db.driver";
    private static BlockingQueue<Connection> pool;
    private static List<Connection> sourceConnections;
    private static final Integer DEFAULT_SIZE = 10;

    static {
        loadDriver();
        loadConnections();
    }

    private ConnectionManager(){
    }

    private static void loadConnections() {
        var capacity = PropertiesUtil.get(POOL_SIZE_KEY);
        var size = capacity == null ? DEFAULT_SIZE : Integer.parseInt(capacity);
        pool = new ArrayBlockingQueue<>(size);
        sourceConnections = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            var connection = make();
            Connection proxyConnection = (Connection)
                    Proxy.newProxyInstance(ConnectionManager.class.getClassLoader(), new Class[]{Connection.class},
                            (proxy, method, args) ->
                                    method.getName().equals("close")
                                            ? pool.add((Connection) proxy)
                                            : method.invoke(connection,args));

            sourceConnections.add(connection);
            pool.add(proxyConnection);
        }

    }

    private static Connection make() {
        try {
            return DriverManager.getConnection(
                    PropertiesUtil.get(URL_KEY),
                    PropertiesUtil.get(USER_KEY),
                    PropertiesUtil.get(PASSWORD_KEY)
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadDriver() {
        try {
            Class.forName(PropertiesUtil.get(DRIVER_KEY));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public static Connection get() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closePool(){
        for (Connection connection : sourceConnections) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
