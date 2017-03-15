package net.mamot.bot.services.impl;

import net.mamot.bot.services.Repo;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public class PGSQLRepo implements Repo {
    private static final String DB_URL_FORMAT = "jdbc:postgresql://%s:%d%s?sslmode=require";
    private static final String DATABASE_URL = System.getenv("DATABASE_URL");
    protected final String table;
    protected final String dataColumn;

    public PGSQLRepo(String table, String dataColumn) {
        this.table = table;
        this.dataColumn = dataColumn;
        createTable();
    }

    private Connection connect() {
        Connection conn = null;
        try {
            URI uri = new URI(DATABASE_URL);
            String username = uri.getUserInfo().split(":")[0];
            String password = uri.getUserInfo().split(":")[1];
            String url = format(DB_URL_FORMAT, uri.getHost(), uri.getPort(), uri.getPath());
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException | URISyntaxException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void insert(String name, String data) {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO ? (username, ?) VALUES(?,?)")) {
            ps.setString(1, table);
            ps.setString(2, dataColumn);
            ps.setString(3, name);
            ps.setString(4, data);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Map<String, String> selectAll() {
        Map result = new HashMap();
        try (Connection c = connect();
             ResultSet rs = c.createStatement().executeQuery("SELECT username, " + dataColumn + " FROM " + table)) {

            while (rs.next()) {
                result.put(rs.getString("username"), rs.getString(dataColumn));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    @Override
    public String select(String user) {
        String result = "";

        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement("SELECT ? FROM ? WHERE username = ?")) {
            ps.setString(1, table);
            ps.setString(2, dataColumn);
            ps.setString(3, user);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                result = resultSet.getString(dataColumn);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public void createTable() {
        try (Connection c = connect()) {
            c.createStatement().execute("CREATE TABLE IF NOT EXISTS " + table + " (username text PRIMARY KEY, " + dataColumn + " text NOT NULL);");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void dropTable() {
        try (Connection conn = connect()) {
            conn.createStatement().execute("DROP TABLE " + table + ";");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void update(String user, String data) {
        try (Connection c = connect();
            PreparedStatement ps = c.prepareStatement("UPDATE ? SET ?  = ? WHERE username = ?")) {
            ps.setString(1, table);
            ps.setString(2, dataColumn);
            ps.setString(3, data);
            ps.setString(4, user);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void delete(String user) {
        try (Connection c = this.connect();
             PreparedStatement ps = c.prepareStatement("DELETE FROM ? WHERE username = ?")) {
            ps.setString(1, table);
            ps.setString(2, user);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
