package com.carselling;

import com.carselling.database.DatabaseConnection;

import com.carselling.database.SchemaInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Test database connection
            DatabaseConnection.testConnection();
            SchemaInitializer.initialize();

            // Load login scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/carselling/fxml/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 800, 600);
            try {
                scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            } catch (Exception e) {
                // CSS file not found, continue without it
                System.out.println("Warning: Could not load CSS file");
            }

            primaryStage.setTitle("Car Selling App - Login");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.setResizable(true);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to start application: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        // Close database connection when application closes
        DatabaseConnection.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
