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

    private static final int COUNT_COLUMN_INDEX = 1;

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

    @Override
    public void insert(String name, String data) {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO " + table + " (username, " + dataColumn + ") VALUES(?,?)")) {
            ps.setString(1, name);
            ps.setString(2, data);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
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
             PreparedStatement ps = c.prepareStatement("SELECT " + dataColumn + " FROM " + table + " WHERE username = ?")) {
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

    @Override
    public void update(String user, String data) {
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement("UPDATE " + table + " SET " + dataColumn + "  = ? WHERE username = ?")) {
            ps.setString(1, data);
            ps.setString(2, user);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void delete(String user) {
        try (Connection c = this.connect();
             PreparedStatement ps = c.prepareStatement("DELETE FROM " + table + " WHERE username = ?")) {
            ps.setString(1, user);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public boolean exists(String user, String data) {
        try (Connection c = this.connect();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(1) FROM " + table + " WHERE username = ? AND " + dataColumn + " = ? ")) {
            ps.setString(1, user);
            ps.setString(2, data);
            ResultSet rs = ps.executeQuery();
            return rs.next() && isPositiveCount(rs);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
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

    private boolean isPositiveCount(ResultSet rs) throws SQLException {
        return rs.getLong(COUNT_COLUMN_INDEX) > 0;
    }
}
