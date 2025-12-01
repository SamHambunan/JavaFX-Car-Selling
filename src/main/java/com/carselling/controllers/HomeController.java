package com.carselling.controllers;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import com.carselling.database.CarDAO;
import com.carselling.models.Car;
import com.carselling.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HomeController {

    @FXML
    private Button loginButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button addCarButton;
    @FXML
    private Button myCarsButton;
    @FXML
    private Button watchlistButton;
    @FXML
    private VBox carsContainer;

    @FXML
    private TextField searchBrandField;
    @FXML
    private TextField searchModelField;
    @FXML
    private TextField minYearField;
    @FXML
    private TextField maxYearField;
    @FXML
    private TextField minPriceField;
    @FXML
    private TextField maxPriceField;
    @FXML
    private Button searchButton;

    private final CarDAO carDAO = new CarDAO();

    @FXML
    public void initialize() {
        updateUI();
        loadAllCars();
    }

    private void updateUI() {
        boolean isLoggedIn = SessionManager.isLoggedIn();
        loginButton.setVisible(!isLoggedIn);
        logoutButton.setVisible(isLoggedIn);
        addCarButton.setVisible(isLoggedIn);
        addCarButton.setDisable(false);
        myCarsButton.setVisible(isLoggedIn);
        watchlistButton.setVisible(isLoggedIn);
    }

    @FXML
    private void handleLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/carselling/fxml/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();

            // Preserve window size and position
            double currentWidth = Math.max(stage.getWidth(), 800);
            double currentHeight = Math.max(stage.getHeight(), 600);
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
            stage.setMinWidth(800);
            stage.setMinHeight(600);

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
    private void handleLogout() {
        SessionManager.logout();
        updateUI();
        loadAllCars();
    }

    @FXML
    private void handleAddCar() {
        // Check if user is logged in
        if (!SessionManager.isLoggedIn()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Not Logged In");
            alert.setHeaderText(null);
            alert.setContentText("Please login to add a car.");
            alert.showAndWait();
            return;
        }
        navigateToCarManagement(null);
    }

    @FXML
    private void handleMyCars() {
        loadMyCars();
    }

    @FXML
    private void handleWatchlist() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/carselling/fxml/watchlist.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) watchlistButton.getScene().getWindow();

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
            stage.setTitle("Watchlist - Car Selling App");
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load watchlist page");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleSearch() {
        String brand = searchBrandField.getText().trim();
        String model = searchModelField.getText().trim();

        Integer minYear = null;
        Integer maxYear = null;
        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;

        try {
            if (!minYearField.getText().trim().isEmpty()) {
                minYear = Integer.parseInt(minYearField.getText().trim());
            }
            if (!maxYearField.getText().trim().isEmpty()) {
                maxYear = Integer.parseInt(maxYearField.getText().trim());
            }
            if (!minPriceField.getText().trim().isEmpty()) {
                minPrice = new BigDecimal(minPriceField.getText().trim());
            }
            if (!maxPriceField.getText().trim().isEmpty()) {
                maxPrice = new BigDecimal(maxPriceField.getText().trim());
            }
        } catch (NumberFormatException e) {
            // Invalid number format, ignore
        }

        String brandParam = brand.isEmpty() ? null : brand;
        String modelParam = model.isEmpty() ? null : model;

        List<Car> cars = carDAO.searchCars(brandParam, modelParam, minYear, maxYear, minPrice, maxPrice);
        displayCars(cars);
    }

    private void loadAllCars() {
        List<Car> cars = carDAO.getAllCars();
        displayCars(cars);
    }

    private void loadMyCars() {
        if (!SessionManager.isLoggedIn()) {
            return;
        }

        List<Car> cars = carDAO.getCarsByUserId(SessionManager.getCurrentUserId());
        displayCars(cars);
    }

    private void displayCars(List<Car> cars) {
        carsContainer.getChildren().clear();

        if (cars.isEmpty()) {
            Label emptyLabel = new Label("No cars found.");
            emptyLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 16px;");
            carsContainer.getChildren().add(emptyLabel);
            return;
        }

        // Create a responsive FlowPane for grid layout
        FlowPane gridPane = new FlowPane();
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        gridPane.setPadding(new Insets(20));
        gridPane.setStyle("-fx-background-color: #f5f5f5;");

        // Calculate card width based on container (responsive)
        gridPane.prefWrapLengthProperty().bind(carsContainer.widthProperty().subtract(40));

        // Display cars in a responsive grid layout
        for (Car car : cars) {
            VBox carCard = createCarCard(car);
            carCard.setPrefWidth(350);
            carCard.setMaxWidth(400);
            carCard.setMinWidth(300);
            gridPane.getChildren().add(carCard);
        }

        carsContainer.getChildren().add(gridPane);
    }

    private VBox createCarCard(Car car) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setPadding(new Insets(15));
        card.setMaxWidth(400);
        card.setMinWidth(300);

        // Image
        ImageView imageView = new ImageView();
        imageView.setFitHeight(200);
        imageView.setFitWidth(350);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        if (car.getImagePath() != null && !car.getImagePath().isEmpty()) {
            File imageFile = new File("src/main/resources/" + car.getImagePath());
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                imageView.setImage(image);
            } else {
                // Placeholder image
                imageView.setStyle("-fx-background-color: #ecf0f1;");
            }
        } else {
            imageView.setStyle("-fx-background-color: #ecf0f1;");
        }

        // Car info
        VBox infoBox = new VBox(10);
        infoBox.setPadding(new Insets(10, 0, 0, 0));

        Label titleLabel = new Label(car.getBrand() + " " + car.getModel() + " (" + car.getYear() + ")");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        titleLabel.setWrapText(true);

        Label priceLabel = new Label("$" + String.format("%.2f", car.getPrice()));
        priceLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        Label detailsLabel = new Label("Mileage: " + car.getMileage() + " miles | Color: " + (car.getColor() != null ? car.getColor() : "N/A"));
        detailsLabel.setStyle("-fx-text-fill: #7f8c8d;");
        detailsLabel.setWrapText(true);

        if (car.getDescription() != null && !car.getDescription().isEmpty()) {
            String shortDesc = car.getDescription().length() > 100
                    ? car.getDescription().substring(0, 100) + "..."
                    : car.getDescription();
            Label descLabel = new Label(shortDesc);
            descLabel.setWrapText(true);
            descLabel.setStyle("-fx-text-fill: #34495e;");
            infoBox.getChildren().addAll(titleLabel, priceLabel, detailsLabel, descLabel);
        } else {
            infoBox.getChildren().addAll(titleLabel, priceLabel, detailsLabel);
        }

        // View button
        Button viewButton = new Button("View Details");
        viewButton.setMaxWidth(Double.MAX_VALUE);
        viewButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        viewButton.setOnAction(e -> viewCarDetails(car));

        infoBox.getChildren().add(viewButton);
        card.getChildren().addAll(imageView, infoBox);
        card.setOnMouseClicked(e -> viewCarDetails(car));
        card.setStyle(card.getStyle() + " -fx-cursor: hand;");

        return card;
    }

    private void viewCarDetails(Car car) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/carselling/fxml/car_detail.fxml"));
            Parent root = loader.load();
            CarDetailController controller = loader.getController();
            controller.setCar(car);

            Stage stage = (Stage) carsContainer.getScene().getWindow();

            // Preserve window size and position
            double currentWidth = Math.max(stage.getWidth(), 900);
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
            stage.setTitle("Car Details - Car Selling App");
            stage.setMinWidth(900);
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load car details page");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void navigateToCarManagement(Car car) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/carselling/fxml/car_management.fxml"));
            Parent root = loader.load();
            CarManagementController controller = loader.getController();
            if (car != null) {
                controller.setCarForEdit(car);
            }

            Stage stage = (Stage) addCarButton.getScene().getWindow();

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
            stage.setTitle(car == null ? "Add Car - Car Selling App" : "Edit Car - Car Selling App");
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load car management page");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
