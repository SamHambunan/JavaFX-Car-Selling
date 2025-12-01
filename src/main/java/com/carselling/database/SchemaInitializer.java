package com.carselling.database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class SchemaInitializer {

    public static void initialize() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // MySQL Syntax Definitions
            String autoIncrement = "INT PRIMARY KEY AUTO_INCREMENT";
            String currentTimestamp = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP";

            // --- 1. Create Users Table ---
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id " + autoIncrement + ", " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "email VARCHAR(100) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "created_at " + currentTimestamp +
                    ");";
            stmt.execute(createUsersTable);

            // --- 2. Create Cars Table ---
            String createCarsTable = "CREATE TABLE IF NOT EXISTS cars (" +
                    "id " + autoIncrement + ", " +
                    "user_id INT NOT NULL, " +
                    "brand VARCHAR(50) NOT NULL, " +
                    "model VARCHAR(50) NOT NULL, " +
                    "year INT NOT NULL, " +
                    "price DECIMAL(10, 2) NOT NULL, " +
                    "mileage INT, " +
                    "color VARCHAR(30), " +
                    "description TEXT, " +
                    "image_path VARCHAR(255), " +
                    "created_at " + currentTimestamp + ", " +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ");";
            stmt.execute(createCarsTable);

            // --- 3. Create Watchlist Table ---
            String createWatchlistTable = "CREATE TABLE IF NOT EXISTS watchlist (" +
                    "id " + autoIncrement + ", " +
                    "user_id INT NOT NULL, " +
                    "car_id INT NOT NULL, " +
                    "created_at " + currentTimestamp + ", " +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE CASCADE, " +
                    "UNIQUE(user_id, car_id)" +
                    ");";
            stmt.execute(createWatchlistTable);

            // --- 4. Create Indexes ---
            // 'IF NOT EXISTS' protects against errors if indexes are already there
            // Note: MySQL requires index names to be unique per table, but global uniqueness is safer
            try {
                stmt.execute("CREATE INDEX idx_cars_brand ON cars(brand);");
            } catch (SQLException ignored) {} // Ignore if exists

            try {
                stmt.execute("CREATE INDEX idx_cars_model ON cars(model);");
            } catch (SQLException ignored) {}

            try {
                stmt.execute("CREATE INDEX idx_cars_year ON cars(year);");
            } catch (SQLException ignored) {}

            try {
                stmt.execute("CREATE INDEX idx_cars_price ON cars(price);");
            } catch (SQLException ignored) {}

            try {
                stmt.execute("CREATE INDEX idx_cars_user ON cars(user_id);");
            } catch (SQLException ignored) {}

            System.out.println("Database tables and indexes checked/created successfully (MySQL Mode).");

        } catch (SQLException e) {
            System.err.println("Error initializing database tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
}