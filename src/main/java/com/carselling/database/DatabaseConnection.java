package com.carselling.database;

import com.carselling.utils.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static String DB_URL = ConfigLoader.getProperty("db.url");
    private static String DB_USER = ConfigLoader.getProperty("db.user");
    private static String DB_PASSWORD = ConfigLoader.getProperty("db.password");

    private static Connection connection = null;

    private DatabaseConnection() {
        // Private constructor to prevent instantiation
    }

    public static Connection getConnection() {
        try {
            // Check if connection is null or closed, then create a new one
            if (connection == null || connection.isClosed()) {
                try {

                    Class.forName("com.mysql.cj.jdbc.Driver");
                    connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                    System.out.println("Database connection established successfully.");
                } catch (ClassNotFoundException e) {
                    System.err.println("MySQL JDBC Driver not found.");
                    e.printStackTrace();
                    return null;
                } catch (SQLException e) {
                    System.err.println("Failed to connect to database.");
                    e.printStackTrace();
                    return null;
                }
            }
        } catch (SQLException e) {
            // If checking isClosed() throws an exception, connection is invalid
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Database connection re-established successfully.");
            } catch (ClassNotFoundException | SQLException ex) {
                System.err.println("Failed to reconnect to database.");
                ex.printStackTrace();
                return null;
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection.");
                e.printStackTrace();
            }
        }
    }

    public static void testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("Database connection test successful.");
            }
        } catch (SQLException e) {
            System.err.println("Database connection test failed.");
            e.printStackTrace();
        }
    }
}
