package com.carselling.models;

import java.time.LocalDateTime;

public class Watchlist {
    private int id;
    private int userId;
    private int carId;
    private LocalDateTime createdAt;

    public Watchlist() {
    }

    public Watchlist(int userId, int carId) {
        this.userId = userId;
        this.carId = carId;
    }

    public Watchlist(int id, int userId, int carId, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.carId = carId;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

