package com.carselling.database;

import com.carselling.models.Car;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarDAO {
    
    public boolean createCar(Car car) {
        String sql = "INSERT INTO cars (user_id, brand, model, year, price, mileage, color, description, image_path) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, car.getUserId());
            pstmt.setString(2, car.getBrand());
            pstmt.setString(3, car.getModel());
            pstmt.setInt(4, car.getYear());
            pstmt.setBigDecimal(5, car.getPrice());
            pstmt.setInt(6, car.getMileage());
            pstmt.setString(7, car.getColor());
            pstmt.setString(8, car.getDescription());
            pstmt.setString(9, car.getImagePath());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        car.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating car: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean updateCar(Car car) {
        String sql = "UPDATE cars SET brand = ?, model = ?, year = ?, price = ?, mileage = ?, " +
                     "color = ?, description = ?, image_path = ? WHERE id = ? AND user_id = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, car.getBrand());
            pstmt.setString(2, car.getModel());
            pstmt.setInt(3, car.getYear());
            pstmt.setBigDecimal(4, car.getPrice());
            pstmt.setInt(5, car.getMileage());
            pstmt.setString(6, car.getColor());
            pstmt.setString(7, car.getDescription());
            pstmt.setString(8, car.getImagePath());
            pstmt.setInt(9, car.getId());
            pstmt.setInt(10, car.getUserId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating car: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean deleteCar(int carId, int userId) {
        String sql = "DELETE FROM cars WHERE id = ? AND user_id = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, carId);
            pstmt.setInt(2, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting car: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public Car getCarById(int id) {
        String sql = "SELECT * FROM cars WHERE id = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return null;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCar(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting car by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars ORDER BY created_at DESC";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return cars;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                cars.add(mapResultSetToCar(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all cars: " + e.getMessage());
            e.printStackTrace();
        }
        
        return cars;
    }
    
    public List<Car> getCarsByUserId(int userId) {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars WHERE user_id = ? ORDER BY created_at DESC";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return cars;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    cars.add(mapResultSetToCar(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting cars by user ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return cars;
    }
    
    public List<Car> searchCars(String brand, String model, Integer minYear, Integer maxYear, 
                                BigDecimal minPrice, BigDecimal maxPrice) {
        List<Car> cars = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM cars WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (brand != null && !brand.trim().isEmpty()) {
            sql.append(" AND brand LIKE ?");
            params.add("%" + brand + "%");
        }
        
        if (model != null && !model.trim().isEmpty()) {
            sql.append(" AND model LIKE ?");
            params.add("%" + model + "%");
        }
        
        if (minYear != null) {
            sql.append(" AND year >= ?");
            params.add(minYear);
        }
        
        if (maxYear != null) {
            sql.append(" AND year <= ?");
            params.add(maxYear);
        }
        
        if (minPrice != null) {
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }
        
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }
        
        sql.append(" ORDER BY created_at DESC");
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return cars;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    cars.add(mapResultSetToCar(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching cars: " + e.getMessage());
            e.printStackTrace();
        }
        
        return cars;
    }
    
    private Car mapResultSetToCar(ResultSet rs) throws SQLException {
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
        
        return car;
    }
}
