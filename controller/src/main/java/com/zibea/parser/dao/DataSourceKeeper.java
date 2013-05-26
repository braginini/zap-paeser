package com.zibea.parser.dao;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author: Mikhail Bragin
 */
public class DataSourceKeeper {

    private DataSource dataSource;

    public DataSourceKeeper() throws Exception {
        createDataSource();
    }

    private void createDataSource() throws Exception {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            String connectionUrl = "jdbc:sqlserver://192.168.1.52;" +
                    "user=root;" +
                    "password=1234mudar@";
            dataSource = setupDataSource(connectionUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DataSource setupDataSource(String connectURI) throws Exception {

        Properties props = new Properties();
        props.setProperty("Username", "root");
        props.setProperty("Password", "1234mudar@");

        ObjectPool connectionPool = new GenericObjectPool(null);

        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI, props);

        PoolableConnectionFactory poolableConnectionFactory =
                new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);

        PoolingDataSource dataSource = new PoolingDataSource(connectionPool);

        return dataSource;
    }

    public Connection getConnection() {
        try {
            Connection connection = dataSource.getConnection();
            //System.out.println("Opened connection");
            return connection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        return null;
    }

    public static void closeConnection(Connection con) {
        if (con != null)
            try {
                con.close();
                //System.out.println("Connection closed");
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
}
