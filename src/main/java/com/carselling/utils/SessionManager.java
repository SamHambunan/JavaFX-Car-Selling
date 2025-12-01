package com.carselling.utils;

import com.carselling.models.User;

public class SessionManager {
    private static User currentUser = null;
    
    private SessionManager() {
        // Private constructor to prevent instantiation
    }
    
    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public static void logout() {
        currentUser = null;
    }
    
    public static int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }
}

