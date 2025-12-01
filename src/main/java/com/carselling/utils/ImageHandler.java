package com.carselling.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class ImageHandler {
    private static final String IMAGE_DIR = "src/main/resources/images/";
    
    static {
        // Create images directory if it doesn't exist
        File dir = new File(IMAGE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    public static String saveImage(File sourceFile) {
        if (sourceFile == null || !sourceFile.exists()) {
            return null;
        }
        
        try {
            // Generate unique filename
            String originalFilename = sourceFile.getName();
            String extension = "";
            int lastDot = originalFilename.lastIndexOf('.');
            if (lastDot > 0) {
                extension = originalFilename.substring(lastDot);
            }
            
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            Path targetPath = Paths.get(IMAGE_DIR + uniqueFilename);
            
            // Copy file to images directory
            Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // Return relative path for database storage
            return "images/" + uniqueFilename;
            
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public static File getImageFile(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        
        // Handle both relative and absolute paths
        String fullPath;
        if (imagePath.startsWith("images/")) {
            fullPath = "src/main/resources/" + imagePath;
        } else {
            fullPath = imagePath;
        }
        
        File file = new File(fullPath);
        if (file.exists()) {
            return file;
        }
        
        return null;
    }
    
    public static boolean deleteImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return false;
        }
        
        try {
            String fullPath;
            if (imagePath.startsWith("images/")) {
                fullPath = "src/main/resources/" + imagePath;
            } else {
                fullPath = imagePath;
            }
            
            Path path = Paths.get(fullPath);
            return Files.deleteIfExists(path);
            
        } catch (IOException e) {
            System.err.println("Error deleting image: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

