package com.carselling.controllers;

import java.io.IOException;

import com.carselling.database.UserDAO;
import com.carselling.models.User;
import com.carselling.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;
    @FXML
    private Label errorLabel;

    // Registration fields
    @FXML
    private TextField regUsernameField;
    @FXML
    private TextField regEmailField;
    @FXML
    private PasswordField regPasswordField;
    @FXML
    private PasswordField regConfirmPasswordField;
    @FXML
    private Button registerSubmitButton;
    @FXML
    private Button backToLoginButton;
    @FXML
    private Label regErrorLabel;

    private final UserDAO userDAO = new UserDAO();
    private boolean isRegisterMode = false;

    @FXML
    public void initialize() {
        // Check if we're in register mode by checking if register fields are visible
        if (regUsernameField != null && regUsernameField.isVisible()) {
            isRegisterMode = true;
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        User user = userDAO.authenticateUser(username, password);

        if (user != null) {
            SessionManager.setCurrentUser(user);
            navigateToHome();
        } else {
            showError("Invalid username/email or password.");
        }
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/carselling/fxml/register.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) registerButton.getScene().getWindow();
            
            // Preserve window size and position
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            double currentX = stage.getX();
            double currentY = stage.getY();
            
            Scene newScene = new Scene(root, currentWidth, currentHeight);
            try {
                newScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            } catch (Exception e) {
                // CSS file not found, continue without it
            }
            
            // Ensure root node respects scene size (if it's a Region)
            if (root instanceof Region) {
                Region region = (Region) root;
                region.setPrefSize(currentWidth, currentHeight);
                region.setMinSize(0, 0);
                region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            }
            
            stage.setScene(newScene);
            stage.setTitle("Register - Car Selling App");
            
            // Restore window position and size
            stage.setX(currentX);
            stage.setY(currentY);
            stage.setWidth(currentWidth);
            stage.setHeight(currentHeight);
            
            // Force layout pass
            if (root instanceof Region) {
                ((Region) root).requestLayout();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load registration page");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Unexpected error");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleRegisterSubmit() {
        String username = regUsernameField.getText().trim();
        String email = regEmailField.getText().trim();
        String password = regPasswordField.getText();
        String confirmPassword = regConfirmPasswordField.getText();

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showRegError("Please fill in all fields.");
            return;
        }

        if (password.length() < 6) {
            showRegError("Password must be at least 6 characters long.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showRegError("Passwords do not match.");
            return;
        }

        if (userDAO.usernameExists(username)) {
            showRegError("Username already exists.");
            return;
        }

        if (userDAO.emailExists(email)) {
            showRegError("Email already exists.");
            return;
        }

        // Create user
        User newUser = new User(username, email, password);
        if (userDAO.createUser(newUser)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Successful");
            alert.setHeaderText(null);
            alert.setContentText("Account created successfully! Please login.");
            alert.showAndWait();

            handleBackToLogin();
        } else {
            showRegError("Registration failed. Please try again.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/carselling/fxml/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backToLoginButton.getScene().getWindow();
            
            // Preserve window size and position
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            double currentX = stage.getX();
            double currentY = stage.getY();
            
            Scene newScene = new Scene(root, currentWidth, currentHeight);
            try {
                newScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            } catch (Exception e) {
                // CSS file not found, continue without it
            }
            
            // Ensure root node respects scene size (if it's a Region)
            if (root instanceof Region) {
                Region region = (Region) root;
                region.setPrefSize(currentWidth, currentHeight);
                region.setMinSize(0, 0);
                region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            }
            
            stage.setScene(newScene);
            stage.setTitle("Login - Car Selling App");
            
            // Restore window position and size
            stage.setX(currentX);
            stage.setY(currentY);
            stage.setWidth(currentWidth);
            stage.setHeight(currentHeight);
            
            // Force layout pass
            if (root instanceof Region) {
                ((Region) root).requestLayout();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/carselling/fxml/home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            
            // Preserve window size and position, but ensure minimum size
            double currentWidth = Math.max(stage.getWidth(), 1000);
            double currentHeight = Math.max(stage.getHeight(), 700);
            double currentX = stage.getX();
            double currentY = stage.getY();
            
            Scene newScene = new Scene(root, currentWidth, currentHeight);
            try {
                newScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            } catch (Exception e) {
                // CSS file not found, continue without it
            }
            
            // Ensure root node respects scene size (if it's a Region)
            if (root instanceof Region) {
                Region region = (Region) root;
                region.setPrefSize(currentWidth, currentHeight);
                region.setMinSize(0, 0);
                region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            }
            
            stage.setScene(newScene);
            stage.setTitle("Home - Car Selling App");
            stage.setMinWidth(1000);
            stage.setMinHeight(700);
            
            // Restore window position and size
            stage.setX(currentX);
            stage.setY(currentY);
            stage.setWidth(currentWidth);
            stage.setHeight(currentHeight);
            
            // Force layout pass
            if (root instanceof Region) {
                ((Region) root).requestLayout();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void showRegError(String message) {
        regErrorLabel.setText(message);
        regErrorLabel.setVisible(true);
    }
}
