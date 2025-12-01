package com.carselling.controllers;

import java.io.File;

import com.carselling.database.CarDAO;
import com.carselling.database.WatchlistDAO;
import com.carselling.models.Car;
import com.carselling.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class CarDetailController {

    @FXML
    private ImageView carImageView;
    @FXML
    private Label carTitleLabel;
    @FXML
    private Label carPriceLabel;
    @FXML
    private Label brandLabel;
    @FXML
    private Label modelLabel;
    @FXML
    private Label yearLabel;
    @FXML
    private Label mileageLabel;
    @FXML
    private Label colorLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button addToWatchlistButton;
    @FXML
    private Button removeFromWatchlistButton;
    @FXML
    private Button backButton;

    private Car car;
    private final CarDAO carDAO = new CarDAO();
    private final WatchlistDAO watchlistDAO = new WatchlistDAO();

    public void setCar(Car car) {
        this.car = car;
        loadCarDetails();
    }

    @FXML
    public void initialize() {
        // Will be called after setCar
    }

    private void loadCarDetails() {
        if (car == null) {
            return;
        }

        // Title and price
        carTitleLabel.setText(car.getBrand() + " " + car.getModel() + " (" + car.getYear() + ")");
        carPriceLabel.setText("$" + String.format("%.2f", car.getPrice()));

        // Details
        brandLabel.setText(car.getBrand());
        modelLabel.setText(car.getModel());
        yearLabel.setText(String.valueOf(car.getYear()));
        mileageLabel.setText(car.getMileage() + " miles");
        colorLabel.setText(car.getColor() != null ? car.getColor() : "N/A");
        descriptionLabel.setText(car.getDescription() != null ? car.getDescription() : "No description available.");

        // Image
        if (car.getImagePath() != null && !car.getImagePath().isEmpty()) {
            File imageFile = new File("src/main/resources/" + car.getImagePath());
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                carImageView.setImage(image);
            }
        }

        // Update button visibility based on login status and ownership
        boolean isLoggedIn = SessionManager.isLoggedIn();
        boolean isOwner = isLoggedIn && SessionManager.getCurrentUserId() == car.getUserId();

        editButton.setVisible(isOwner);
        deleteButton.setVisible(isOwner);

        if (isLoggedIn && !isOwner) {
            // Check if car is in watchlist
            boolean inWatchlist = watchlistDAO.isInWatchlist(SessionManager.getCurrentUserId(), car.getId());
            addToWatchlistButton.setVisible(!inWatchlist);
            removeFromWatchlistButton.setVisible(inWatchlist);
        } else {
            addToWatchlistButton.setVisible(false);
            removeFromWatchlistButton.setVisible(false);
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/carselling/fxml/home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            
            // Preserve window size and position
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEdit() {
        if (car == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/carselling/fxml/car_management.fxml"));
            Parent root = loader.load();
            CarManagementController controller = loader.getController();
            controller.setCarForEdit(car);

            Stage stage = (Stage) editButton.getScene().getWindow();
            
            // Preserve window size and position
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
            stage.setTitle("Edit Car - Car Selling App");
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        if (car == null) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Car");
        alert.setHeaderText("Are you sure you want to delete this car?");
        alert.setContentText("This action cannot be undone.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (carDAO.deleteCar(car.getId(), SessionManager.getCurrentUserId())) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Car deleted successfully.");
                successAlert.showAndWait();

                handleBack();
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Failed to delete car.");
                errorAlert.showAndWait();
            }
        }
    }

    @FXML
    private void handleAddToWatchlist() {
        if (car == null || !SessionManager.isLoggedIn()) {
            return;
        }

        if (watchlistDAO.addToWatchlist(SessionManager.getCurrentUserId(), car.getId())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Car added to watchlist.");
            alert.showAndWait();

            loadCarDetails(); // Refresh to update button visibility
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to add car to watchlist. It may already be in your watchlist.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleRemoveFromWatchlist() {
        if (car == null || !SessionManager.isLoggedIn()) {
            return;
        }

        if (watchlistDAO.removeFromWatchlist(SessionManager.getCurrentUserId(), car.getId())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Car removed from watchlist.");
            alert.showAndWait();

            loadCarDetails(); // Refresh to update button visibility
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to remove car from watchlist.");
            alert.showAndWait();
        }
    }
}
