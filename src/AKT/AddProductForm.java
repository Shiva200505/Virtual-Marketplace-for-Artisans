package AKT;// Replace with your package name
//The Add Product Form will allow artisans to add new products.
/*3.3 Explanation of the Code
Add Product Form:

Contains fields for product name, price, category, and description.

A button to add the product to the database.

addProduct() Method:

Inserts the product details into the Products table.*/
//package com.virtualmarketplace; // Replace with your package name (e.g., AKT)

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AddProductForm extends JFrame {
    private JTextField nameField, priceField, stockField;
    private JComboBox<String> categoryCombo;
    private JTextArea descriptionArea;
    private JButton addButton, cancelButton, chooseImageButton;
    private JLabel imagePreviewLabel;
    private String selectedImagePath = null;
    private int artisanId = 1; // Default artisan ID, should be passed from login

    public AddProductForm() {
        setTitle("Add New Product - Virtual Marketplace");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Main container with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        
        // Create form panel
        JPanel formPanel = createFormPanel();
        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setBorder(null);
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Add panels to main container
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Center window on screen
        setLocationRelativeTo(null);
        
        // Add action listeners
        addButton.addActionListener(e -> addProduct());
        
        cancelButton.addActionListener(e -> {
            new ArtisanDashboard(artisanId).setVisible(true);
            dispose();
        });
        
        chooseImageButton.addActionListener(e -> chooseProductImage());
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Add New Product", JLabel.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(204, 102, 0));
        
        panel.add(titleLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Initialize components
        nameField = new JTextField(25);
        nameField.setPreferredSize(new Dimension(300, 30));
        
        priceField = new JTextField(10);
        priceField.setPreferredSize(new Dimension(150, 30));
        
        stockField = new JTextField(10);
        stockField.setPreferredSize(new Dimension(150, 30));
        
        String[] categories = {"Pottery", "Textiles", "Woodworking", "Jewelry", "Paper Crafts", "Glass Art"};
        categoryCombo = new JComboBox<>(categories);
        categoryCombo.setPreferredSize(new Dimension(200, 30));
        
        descriptionArea = new JTextArea(8, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setPreferredSize(new Dimension(300, 150));
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setPreferredSize(new Dimension(300, 150));
        
        // Image section
        imagePreviewLabel = new JLabel("No image selected");
        imagePreviewLabel.setPreferredSize(new Dimension(250, 250));
        imagePreviewLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        imagePreviewLabel.setHorizontalAlignment(JLabel.CENTER);
        
        chooseImageButton = new JButton("Choose Image");
        chooseImageButton.setPreferredSize(new Dimension(150, 35));
        
        // Create font for labels
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        
        // Product name
        JLabel nameLabel = new JLabel("Product Name:*");
        nameLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        panel.add(nameLabel, gbc);
        
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(nameField, gbc);
        
        // Price
        JLabel priceLabel = new JLabel("Price:*");
        priceLabel.setFont(labelFont);
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(priceLabel, gbc);
        
        gbc.gridy = 3;
        panel.add(priceField, gbc);
        
        // Category
        JLabel categoryLabel = new JLabel("Category:*");
        categoryLabel.setFont(labelFont);
        gbc.gridy = 4;
        panel.add(categoryLabel, gbc);
        
        gbc.gridy = 5;
        panel.add(categoryCombo, gbc);
        
        // Stock quantity
        JLabel stockLabel = new JLabel("Stock Quantity:*");
        stockLabel.setFont(labelFont);
        gbc.gridy = 6;
        panel.add(stockLabel, gbc);
        
        gbc.gridy = 7;
        panel.add(stockField, gbc);
        
        // Description
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(labelFont);
        gbc.gridy = 8;
        panel.add(descLabel, gbc);
        
        gbc.gridy = 9;
        panel.add(descScrollPane, gbc);
        
        // Image section
        JLabel imageLabel = new JLabel("Product Image:");
        imageLabel.setFont(labelFont);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        panel.add(imageLabel, gbc);
        
        gbc.gridy = 1;
        gbc.gridheight = 6;
        gbc.insets = new Insets(10, 25, 10, 10);
        panel.add(imagePreviewLabel, gbc);
        
        gbc.gridy = 7;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(chooseImageButton, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(120, 40));
        
        addButton = new JButton("Add Product");
        addButton.setPreferredSize(new Dimension(150, 40));
        
        // Styling
        addButton.setBackground(new Color(70, 130, 180));
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setFocusPainted(false);
        
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));
        
        panel.add(cancelButton);
        panel.add(addButton);
        
        return panel;
    }
    
    private void chooseProductImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Product Image");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedImagePath = selectedFile.getAbsolutePath();
            
            // Update image preview
            ImageIcon icon = new ImageIcon(selectedImagePath);
            Image img = icon.getImage().getScaledInstance(230, 230, Image.SCALE_SMOOTH);
            imagePreviewLabel.setIcon(new ImageIcon(img));
            imagePreviewLabel.setText("");
        }
    }
    
    private void addProduct() {
        // Validate input
        if (nameField.getText().isEmpty() || priceField.getText().isEmpty() || stockField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill in all required fields (marked with *)", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String name = nameField.getText().trim();
        double price;
        int stock;
        
        try {
            price = Double.parseDouble(priceField.getText().trim());
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Price must be a positive number", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Price must be a valid number", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            stock = Integer.parseInt(stockField.getText().trim());
            if (stock < 0) {
                JOptionPane.showMessageDialog(this, 
                    "Stock quantity cannot be negative", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Stock quantity must be a valid integer", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String category = (String) categoryCombo.getSelectedItem();
        String description = descriptionArea.getText().trim();
        
        // Copy and save the selected image
        String imageName = null;
        if (selectedImagePath != null) {
            try {
                String fileExt = selectedImagePath.substring(selectedImagePath.lastIndexOf('.'));
                imageName = category.toLowerCase() + "_" + System.currentTimeMillis() + fileExt;
                
                // Ensure the directory exists
                File resourcesDir = new File("resources/images");
                if (!resourcesDir.exists()) {
                    resourcesDir.mkdirs();
                }
                
                // Copy the file
                Path source = Paths.get(selectedImagePath);
                Path target = Paths.get("resources/images/" + imageName);
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error saving image: " + e.getMessage(), 
                    "Image Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Save to database
        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO Products (name, price, category, description, stock_quantity, image, artisan_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setString(3, category);
            pstmt.setString(4, description);
            pstmt.setInt(5, stock);
            pstmt.setString(6, imageName);
            pstmt.setInt(7, artisanId);
            
            int result = pstmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Product added successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Return to dashboard or view products
                new ArtisanDashboard(artisanId).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to add product. Please try again.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error adding product: " + ex.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new AddProductForm().setVisible(true);
        });
    }
}