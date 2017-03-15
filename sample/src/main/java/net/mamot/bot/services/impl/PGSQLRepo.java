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
        String sql = "INSERT INTO " + table + "(username, " + dataColumn + ") VALUES(?,?)";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, data);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Map<String, String> selectAll() {
        Map result = new HashMap();
        final String sql = "SELECT username, " + dataColumn + " FROM " + table;

        try (Connection c = connect();
             ResultSet rs = c.createStatement().executeQuery(sql)) {

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
        String sql = "SELECT " + dataColumn + " FROM " + table + "WHERE username = ?";

        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user);
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
        final String sql = "CREATE TABLE IF NOT EXISTS " + table + " (username text PRIMARY KEY, " + dataColumn + " text NOT NULL);";

        try (Connection c = connect()) {
            c.createStatement().execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void dropTable() {
        String sql = "DROP TABLE " + table + ";";

        try (Connection conn = connect()) {
            conn.createStatement().execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void update(String user, String data) {
        String sql = "UPDATE " + table + " SET " + dataColumn + "  = ? WHERE username = ?";

        try (Connection c = connect();
            PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, data);
            ps.setString(2, user);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void delete(String user) {
        String sql = "DELETE FROM " + table + " WHERE username = ?";
        try (Connection c = this.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
