package com.carselling.controllers;

import java.io.File;
import java.math.BigDecimal;

import com.carselling.database.CarDAO;
import com.carselling.models.Car;
import com.carselling.utils.ImageHandler;
import com.carselling.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class CarManagementController {

    @FXML
    private TextField brandField;
    @FXML
    private TextField modelField;
    @FXML
    private TextField yearField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField mileageField;
    @FXML
    private TextField colorField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private ImageView imagePreview;
    @FXML
    private Label imagePathLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button browseImageButton;
    @FXML
    private Button backButton;

    private final CarDAO carDAO = new CarDAO();
    private Car carForEdit = null;
    private File selectedImageFile = null;

    @FXML
    public void initialize() {
        // Check if user is logged in
        if (!SessionManager.isLoggedIn()) {
            handleBack();
            return;
        }
    }

    public void setCarForEdit(Car car) {
        this.carForEdit = car;
        if (car != null) {
            titleLabel.setText("Edit Car");
            loadCarData();
        }
    }

    private void loadCarData() {
        if (carForEdit == null) {
            return;
        }

        brandField.setText(carForEdit.getBrand());
        modelField.setText(carForEdit.getModel());
        yearField.setText(String.valueOf(carForEdit.getYear()));
        priceField.setText(carForEdit.getPrice().toString());
        mileageField.setText(String.valueOf(carForEdit.getMileage()));
        colorField.setText(carForEdit.getColor() != null ? carForEdit.getColor() : "");
        descriptionField.setText(carForEdit.getDescription() != null ? carForEdit.getDescription() : "");

        if (carForEdit.getImagePath() != null && !carForEdit.getImagePath().isEmpty()) {
            File imageFile = new File("src/main/resources/" + carForEdit.getImagePath());
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                imagePreview.setImage(image);
                imagePathLabel.setText(carForEdit.getImagePath());
            }
        }
    }

    @FXML
    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Car Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );

        selectedImageFile = fileChooser.showOpenDialog(browseImageButton.getScene().getWindow());

        if (selectedImageFile != null) {
            Image image = new Image(selectedImageFile.toURI().toString());
            imagePreview.setImage(image);
            imagePathLabel.setText(selectedImageFile.getName());
        }
    }

    @FXML
    private void handleSave() {
        // Validation
        if (brandField.getText().trim().isEmpty()) {
            showError("Brand is required.");
            return;
        }

        if (modelField.getText().trim().isEmpty()) {
            showError("Model is required.");
            return;
        }

        int year;
        try {
            year = Integer.parseInt(yearField.getText().trim());
            if (year < 1900 || year > 2100) {
                showError("Please enter a valid year.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid year.");
            return;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceField.getText().trim());
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Price must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid price.");
            return;
        }

        int mileage = 0;
        if (!mileageField.getText().trim().isEmpty()) {
            try {
                mileage = Integer.parseInt(mileageField.getText().trim());
                if (mileage < 0) {
                    showError("Mileage cannot be negative.");
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Please enter a valid mileage.");
                return;
            }
        }

        // Handle image
        String imagePath = null;
        if (selectedImageFile != null) {
            imagePath = ImageHandler.saveImage(selectedImageFile);
            if (imagePath == null) {
                showError("Failed to save image. Please try again.");
                return;
            }
        } else if (carForEdit != null && carForEdit.getImagePath() != null) {
            // Keep existing image if editing and no new image selected
            imagePath = carForEdit.getImagePath();
        }

        // Create or update car
        boolean success = false;

        if (carForEdit == null) {
            // Create new car
            Car newCar = new Car(
                    SessionManager.getCurrentUserId(),
                    brandField.getText().trim(),
                    modelField.getText().trim(),
                    year,
                    price,
                    mileage,
                    colorField.getText().trim(),
                    descriptionField.getText().trim(),
                    imagePath
            );

            success = carDAO.createCar(newCar);
        } else {
            // Update existing car
            carForEdit.setBrand(brandField.getText().trim());
            carForEdit.setModel(modelField.getText().trim());
            carForEdit.setYear(year);
            carForEdit.setPrice(price);
            carForEdit.setMileage(mileage);
            carForEdit.setColor(colorField.getText().trim());
            carForEdit.setDescription(descriptionField.getText().trim());
            if (imagePath != null) {
                carForEdit.setImagePath(imagePath);
            }

            success = carDAO.updateCar(carForEdit);
        }

        if (success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText(carForEdit == null ? "Car added successfully!" : "Car updated successfully!");
            alert.showAndWait();

            handleBack();
        } else {
            showError("Failed to save car. Please try again.");
        }
    }

    @FXML
    private void handleCancel() {
        handleBack();
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

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
