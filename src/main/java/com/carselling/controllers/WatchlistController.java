package com.carselling.controllers;

import java.io.File;
import java.util.List;

import com.carselling.database.WatchlistDAO;
import com.carselling.models.Car;
import com.carselling.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WatchlistController {

    @FXML
    private VBox watchlistContainer;
    @FXML
    private Label emptyLabel;
    @FXML
    private Button backButton;

    private final WatchlistDAO watchlistDAO = new WatchlistDAO();

    @FXML
    public void initialize() {
        if (!SessionManager.isLoggedIn()) {
            handleBack();
            return;
        }

        loadWatchlist();
    }

    private void loadWatchlist() {
        List<Car> cars = watchlistDAO.getWatchlistCars(SessionManager.getCurrentUserId());

        watchlistContainer.getChildren().clear();

        if (cars.isEmpty()) {
            emptyLabel.setVisible(true);
            return;
        }

        emptyLabel.setVisible(false);

        // Create a responsive FlowPane for grid layout
        FlowPane gridPane = new FlowPane();
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        gridPane.setPadding(new Insets(20));
        gridPane.setStyle("-fx-background-color: #f5f5f5;");

        // Calculate card width based on container (responsive)
        gridPane.prefWrapLengthProperty().bind(watchlistContainer.widthProperty().subtract(40));

        for (Car car : cars) {
            VBox carCard = createCarCard(car);
            carCard.setPrefWidth(350);
            carCard.setMaxWidth(400);
            carCard.setMinWidth(300);
            gridPane.getChildren().add(carCard);
        }

        watchlistContainer.getChildren().add(gridPane);
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

        HBox buttonBox = new HBox(10);
        Button viewButton = new Button("View Details");
        viewButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        viewButton.setOnAction(e -> viewCarDetails(car));
        viewButton.setPrefWidth(150);

        Button removeButton = new Button("Remove");
        removeButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
        removeButton.setOnAction(e -> removeFromWatchlist(car));
        removeButton.setPrefWidth(150);

        buttonBox.getChildren().addAll(viewButton, removeButton);

        infoBox.getChildren().addAll(titleLabel, priceLabel, detailsLabel, buttonBox);
        card.getChildren().addAll(imageView, infoBox);

        return card;
    }

    private void viewCarDetails(Car car) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/carselling/fxml/car_detail.fxml"));
            Parent root = loader.load();
            CarDetailController controller = loader.getController();
            controller.setCar(car);

            Stage stage = (Stage) backButton.getScene().getWindow();
            
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
            
            // Force layout pass
            if (root instanceof Region) {
                ((Region) root).requestLayout();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeFromWatchlist(Car car) {
        if (watchlistDAO.removeFromWatchlist(SessionManager.getCurrentUserId(), car.getId())) {
            loadWatchlist(); // Refresh the list
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/carselling/fxml/home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home - Car Selling App");
            stage.setMinWidth(1000);
            stage.setMinHeight(700);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
