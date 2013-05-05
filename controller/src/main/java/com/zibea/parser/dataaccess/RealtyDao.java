package com.zibea.parser.dataaccess;

import com.zibea.parser.model.domain.*;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * @author: Mikhail Bragin
 */
public class RealtyDao {

    private static DataSource dataSource;

    public static void createDataSource() throws Exception {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            String connectionUrl = "jdbc:sqlserver://192.168.1.105;" +
                    "user=root;" +
                    "password=1234mudar@";
            dataSource = setupDataSource(connectionUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DataSource setupDataSource(String connectURI) throws Exception {

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

    public static Connection getConnection() {
        try {
            Connection connection = dataSource.getConnection();
            System.out.println("Opened connection");
            return connection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        return null;
    }

    public void saveBatch(List<RealtyObject> objects) {
        Connection con = getConnection();
        try {
            String tableName = null;
            String schemaName = "realty";

            String SQL = null;
            if (objects != null && !objects.isEmpty()) {
                if (objects.get(0) instanceof Apartment) {
                    tableName = schemaName + ".Apartments";
                    SQL = "INSERT INTO " + tableName + " VALUES (?,?,?)";
                }
                if (objects.get(0) instanceof State) {
                    tableName = schemaName + ".States";
                    SQL = "INSERT INTO " + tableName + " VALUES (?,?,?)";
                }
                if (objects.get(0) instanceof City) {
                    tableName = schemaName + ".Cities";
                    SQL = "INSERT INTO " + tableName + "(id, state_id, title, url_param) VALUES (?,?,?,?)";
                }

                if (objects.get(0) instanceof District) {
                    tableName = schemaName + ".Districts";
                    SQL = "INSERT INTO " + tableName + "(id, city_id, title, url_param) VALUES (?,?,?,?)";
                }
            }
            if (tableName != null) {

                PreparedStatement stmt = con.prepareStatement(SQL);

                for (RealtyObject object : objects) {
                    if (object instanceof City) {
                        stmt.setLong(1, object.getId());
                        stmt.setLong(2, ((City) object).getStateId());
                        stmt.setString(3, object.getTitle());
                        stmt.setString(4, object.getUrlParam());
                    } else if (object instanceof District) {
                        stmt.setLong(1, object.getId());
                        stmt.setLong(2, ((District) object).getCityId());
                        stmt.setString(3, object.getTitle());
                        stmt.setString(4, object.getUrlParam());
                    } else {
                        stmt.setLong(1, object.getId());
                        stmt.setString(2, object.getTitle());
                        stmt.setString(3, object.getUrlParam());
                    }
                    stmt.addBatch();
                }

                int[] rs = stmt.executeBatch();
                con.commit();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        } finally {
            closeConnection(con);
        }
    }

    public List<State> getAllStates() {
        List<State> result = new ArrayList<>();
        Connection con = getConnection();
        try {

            String SQL = "SELECT * FROM realty.States";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                result.add(new State(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("url_param")));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        } finally {
            closeConnection(con);
        }

        return result;
    }

    public List<City> getAllCities() {
        List<City> result = new ArrayList<>();
        Connection con = getConnection();
        try {

            String SQL = "SELECT * FROM realty.Cities";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                result.add(new City(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("url_param"),
                        rs.getLong("state_id")));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        } finally {
            closeConnection(con);
        }

        return result;
    }

    public List<City> getCityByState(State state) {
        List<City> result = new ArrayList<>();
        Connection con = getConnection();
        try {

            String SQL = "SELECT * FROM realty.Cities WHERE state_id = " + state.getId();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                result.add(new City(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("url_param"),
                        rs.getLong("state_id")));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        } finally {
            closeConnection(con);
        }

        return result;
    }

    public static void closeConnection(Connection con) {
        if (con != null)
            try {
                con.close();
                System.out.println("Connection closed");
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public List<Apartment> getAllApartments() {
        List<Apartment> result = new ArrayList<>();
        Connection con = getConnection();
        try {

            String SQL = "SELECT * FROM realty.Apartments";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                result.add(new Apartment(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("url_param")));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        } finally {
            closeConnection(con);
        }

        return result;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> result = new ArrayList<>();
        Connection con = getConnection();
        try {

            String SQL = "SELECT * FROM realty.Transactions";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                result.add(new Transaction(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("url_param")));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        } finally {
            closeConnection(con);
        }

        return result;
    }

    public List<District> getDistrictsByCity(City city) {
        List<District> result = new ArrayList<>();
        Connection con = getConnection();
        try {

            String SQL = "SELECT * FROM realty.Districts";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                result.add(new District(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("url_param"),
                        city));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        } finally {
            closeConnection(con);
        }

        return result;
    }

    public Set<Long> getAllSavedOfferIds() {
        System.out.println("Getting saved offers ids");
        Set<Long> result = new HashSet<>();

        Connection con = getConnection();
        try {

            String SQL = "SELECT id FROM realty.Offers";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                result.add(rs.getLong("id"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        } finally {
            closeConnection(con);
        }
        System.out.println("Got saved offers ids size=" + result.size());
        return result;


    }
}
