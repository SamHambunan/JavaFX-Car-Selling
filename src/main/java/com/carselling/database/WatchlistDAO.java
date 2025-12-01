package com.carselling.database;

import com.carselling.models.Car;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WatchlistDAO {
    
    public boolean addToWatchlist(int userId, int carId) {
        String sql = "INSERT INTO watchlist (user_id, car_id) VALUES (?, ?)";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, carId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            // Check if it's a duplicate entry error
            if (e.getErrorCode() == 1062) {
                System.out.println("Car already in watchlist");
                return false;
            }
            System.err.println("Error adding to watchlist: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean removeFromWatchlist(int userId, int carId) {
        String sql = "DELETE FROM watchlist WHERE user_id = ? AND car_id = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, carId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error removing from watchlist: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean isInWatchlist(int userId, int carId) {
        String sql = "SELECT COUNT(*) FROM watchlist WHERE user_id = ? AND car_id = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, carId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking watchlist: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public List<Car> getWatchlistCars(int userId) {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT c.* FROM cars c " +
                     "INNER JOIN watchlist w ON c.id = w.car_id " +
                     "WHERE w.user_id = ? " +
                     "ORDER BY w.created_at DESC";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return cars;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Car car = new Car();
                    car.setId(rs.getInt("id"));
                    car.setUserId(rs.getInt("user_id"));
                    car.setBrand(rs.getString("brand"));
                    car.setModel(rs.getString("model"));
                    car.setYear(rs.getInt("year"));
                    car.setPrice(rs.getBigDecimal("price"));
                    car.setMileage(rs.getInt("mileage"));
                    car.setColor(rs.getString("color"));
                    car.setDescription(rs.getString("description"));
                    car.setImagePath(rs.getString("image_path"));
                    
                    Timestamp timestamp = rs.getTimestamp("created_at");
                    if (timestamp != null) {
                        car.setCreatedAt(timestamp.toLocalDateTime());
                    }
                    
                    cars.add(car);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting watchlist cars: " + e.getMessage());
            e.printStackTrace();
        }
        
        return cars;
    }
}
