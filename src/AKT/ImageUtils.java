package AKT;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Utility class for handling product images in the Virtual Marketplace application.
 */
public class ImageUtils {
    
    // Default image directory
    private static final String IMAGE_DIR = "resources/images/";
    
    /**
     * Loads an image from the resources directory.
     * 
     * @param imageName The name of the image file to load
     * @param width The desired width of the image
     * @param height The desired height of the image
     * @return A JLabel containing the scaled image, or a placeholder if the image can't be found
     */
    public static JLabel loadProductImage(String imageName, int width, int height) {
        try {
            // Try to load from resources directory
            BufferedImage img = null;
            File imgFile = new File(IMAGE_DIR + imageName);
            
            if (imgFile.exists()) {
                // Load from file
                img = ImageIO.read(imgFile);
            } else {
                // If file doesn't exist, try to load from classpath
                URL imageUrl = ImageUtils.class.getClassLoader().getResource(IMAGE_DIR + imageName);
                if (imageUrl != null) {
                    img = ImageIO.read(imageUrl);
                }
            }
            
            if (img != null) {
                // Scale the image
                Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new JLabel(new ImageIcon(scaledImg));
            } else {
                // If image not found, return a placeholder
                return createPlaceholderImage(width, height, imageName);
            }
            
        } catch (IOException e) {
            System.err.println("Error loading image: " + e.getMessage());
            return createPlaceholderImage(width, height, imageName);
        }
    }
    
    /**
     * Creates a placeholder image with text.
     * 
     * @param width The width of the placeholder
     * @param height The height of the placeholder
     * @param text The text to display on the placeholder
     * @return A JLabel with a colored background and text
     */
    private static JLabel createPlaceholderImage(int width, int height, String text) {
        JLabel placeholder = new JLabel(getProductTypeFromFilename(text));
        placeholder.setPreferredSize(new Dimension(width, height));
        placeholder.setMinimumSize(new Dimension(width, height));
        placeholder.setMaximumSize(new Dimension(width, height));
        placeholder.setHorizontalAlignment(JLabel.CENTER);
        placeholder.setVerticalAlignment(JLabel.CENTER);
        placeholder.setBackground(new Color(240, 240, 240));
        placeholder.setForeground(new Color(120, 120, 120));
        placeholder.setOpaque(true);
        placeholder.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        return placeholder;
    }
    
    /**
     * Extracts a product type from a filename for use in placeholders.
     * e.g., "pottery_vase.jpg" becomes "Pottery"
     */
    private static String getProductTypeFromFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "Product";
        }
        
        if (filename.contains("_")) {
            String category = filename.substring(0, filename.indexOf('_'));
            return capitalizeFirstLetter(category);
        }
        
        return "Product";
    }
    
    /**
     * Capitalizes the first letter of a string.
     */
    private static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
    
    /**
     * Sets up the resources directory for storing product images.
     * Creates the directory if it doesn't exist.
     */
    public static void setupImageDirectory() {
        File directory = new File(IMAGE_DIR);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("Created image directory: " + directory.getAbsolutePath());
            } else {
                System.err.println("Failed to create image directory: " + directory.getAbsolutePath());
            }
        }
    }
} 