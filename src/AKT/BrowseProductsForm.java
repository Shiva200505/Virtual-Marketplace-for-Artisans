package AKT;	// Replace with your package name (e.g., AKT)
//The Browse Products Form will allow customers to browse and purchase products.

//package com.virtualmarketplace; // Replace with your package name (e.g., AKT)

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BrowseProductsForm extends JFrame {
    private JPanel productsPanel;
    private JComboBox<String> categoryFilter;
    private JTextField searchField;
    private JButton searchButton, backButton;
    private JLabel resultsLabel;
    private int customerId = 1; // Default value, should be replaced with logged-in user's ID

    public BrowseProductsForm() {
        setTitle("Browse Products - Virtual Marketplace");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Main container with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        
        // Create filter panel
        JPanel filterPanel = createFilterPanel();
        
        // Create products panel (scrollable)
        productsPanel = new JPanel();
        productsPanel.setLayout(new GridLayout(0, 3, 15, 15)); // Grid with 3 columns
        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setBorder(null);
        
        // Results label
        resultsLabel = new JLabel("Showing all products");
        resultsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Create control panel (at bottom)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButton = new JButton("Back to Dashboard");
        backButton.setPreferredSize(new Dimension(150, 40));
        controlPanel.add(backButton);
        
        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(filterPanel, BorderLayout.WEST);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(resultsLabel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Center window on screen
        setLocationRelativeTo(null);
        
        // Load products initially
        loadProducts(null, null);
        
        // Add action listeners
        backButton.addActionListener(e -> {
            new CustomerDashboard().setVisible(true);
            dispose();
        });
        
        searchButton.addActionListener(e -> {
            String category = categoryFilter.getSelectedItem().toString();
            String searchTerm = searchField.getText().trim();
            
            if (category.equals("All Categories")) {
                category = null;
            }
            
            if (searchTerm.isEmpty()) {
                searchTerm = null;
            }
            
            loadProducts(category, searchTerm);
        });
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Browse Products", JLabel.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        panel.add(titleLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(0, 0, 0, 20)
        ));
        panel.setPreferredSize(new Dimension(200, getHeight()));
        
        JLabel filterLabel = new JLabel("Filter Products");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 16));
        filterLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Load categories from database
        List<String> categories = getCategories();
        categories.add(0, "All Categories");
        
        categoryFilter = new JComboBox<>(categories.toArray(new String[0]));
        categoryFilter.setMaximumSize(new Dimension(180, 30));
        categoryFilter.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        searchLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        searchField = new JTextField();
        searchField.setMaximumSize(new Dimension(180, 30));
        searchField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        searchButton = new JButton("Search");
        searchButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchButton.setMaximumSize(new Dimension(180, 35));
        
        panel.add(filterLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(categoryLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(categoryFilter);
        panel.add(Box.createVerticalStrut(15));
        panel.add(searchLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(searchField);
        panel.add(Box.createVerticalStrut(15));
        panel.add(searchButton);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT DISTINCT category FROM Products ORDER BY category";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (Exception ex) {
            System.err.println("Error loading categories: " + ex.getMessage());
        }
        
        return categories;
    }
    
    private void loadProducts(String category, String searchTerm) {
        productsPanel.removeAll();
        
        try (Connection conn = DBConnection.getConnection()) {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT p.*, a.name as artisan_name FROM Products p ");
            queryBuilder.append("JOIN Artisans a ON p.artisan_id = a.id ");
            
            List<Object> params = new ArrayList<>();
            
            if (category != null || searchTerm != null) {
                queryBuilder.append("WHERE ");
                
                if (category != null) {
                    queryBuilder.append("category = ? ");
                    params.add(category);
                }
                
                if (searchTerm != null) {
                    if (category != null) {
                        queryBuilder.append("AND ");
                    }
                    queryBuilder.append("(p.name LIKE ? OR p.description LIKE ?) ");
                    params.add("%" + searchTerm + "%");
                    params.add("%" + searchTerm + "%");
                }
            }
            
            queryBuilder.append("ORDER BY p.name");
            
            PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString());
            
            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String productCategory = rs.getString("category");
                String description = rs.getString("description");
                String image = rs.getString("image");
                String artisanName = rs.getString("artisan_name");
                
                // Create product card
                JPanel card = createProductCard(id, name, price, productCategory, description, image, artisanName);
                productsPanel.add(card);
                count++;
            }
            
            if (count == 0) {
                JLabel noResultsLabel = new JLabel("No products found");
                noResultsLabel.setFont(new Font("Arial", Font.BOLD, 14));
                productsPanel.add(noResultsLabel);
                resultsLabel.setText("No products found");
            } else {
                String resultText = "Showing " + count + " product" + (count > 1 ? "s" : "");
                if (category != null) {
                    resultText += " in category '" + category + "'";
                }
                if (searchTerm != null) {
                    resultText += " matching '" + searchTerm + "'";
                }
                resultsLabel.setText(resultText);
            }
            
            productsPanel.revalidate();
            productsPanel.repaint();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + ex.getMessage());
        }
    }
    
    private JPanel createProductCard(int productId, String name, double price, 
                                     String category, String description, 
                                     String imageName, String artisanName) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Create image panel
        JLabel imageLabel = ImageUtils.loadProductImage(imageName, 180, 180);
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        // Create info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JLabel priceLabel = new JLabel(String.format("$%.2f", price));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setForeground(new Color(0, 102, 0));
        
        JLabel categoryLabel = new JLabel("Category: " + category);
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JLabel artisanLabel = new JLabel("By: " + artisanName);
        artisanLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(categoryLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(artisanLabel);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        JButton detailsButton = new JButton("Details");
        JButton addToCartButton = new JButton("Add to Cart");
        
        buttonPanel.add(detailsButton);
        buttonPanel.add(addToCartButton);
        
        // Add all panels to card
        card.add(imagePanel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
                    BorderFactory.createEmptyBorder(9, 9, 9, 9)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }
        });
        
        // Add action listeners
        detailsButton.addActionListener(e -> {
            showProductDetails(productId, name, price, category, description, imageName, artisanName);
        });
        
        addToCartButton.addActionListener(e -> {
            addToCart(productId, name);
        });
        
        return card;
    }
    
    private void showProductDetails(int productId, String name, double price, 
                                   String category, String description, 
                                   String imageName, String artisanName) {
        JDialog detailDialog = new JDialog(this, "Product Details: " + name, true);
        detailDialog.setSize(600, 650);
        detailDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Product image at larger size
        JLabel imageLabel = ImageUtils.loadProductImage(imageName, 350, 350);
        
        // Product details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        JLabel priceLabel = new JLabel(String.format("Price: $%.2f", price));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        priceLabel.setForeground(new Color(0, 102, 0));
        
        JLabel categoryLabel = new JLabel("Category: " + category);
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JLabel artisanLabel = new JLabel("Artisan: " + artisanName);
        artisanLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JTextArea descriptionArea = new JTextArea(description);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setEditable(false);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setPreferredSize(new Dimension(550, 100));
        
        // Add to cart button
        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.setFont(new Font("Arial", Font.BOLD, 16));
        addToCartButton.setPreferredSize(new Dimension(200, 40));
        
        addToCartButton.addActionListener(e -> {
            addToCart(productId, name);
            detailDialog.dispose();
        });
        
        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        closeButton.addActionListener(e -> detailDialog.dispose());
        
        // Add components
        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(priceLabel);
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(categoryLabel);
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(artisanLabel);
        detailsPanel.add(Box.createVerticalStrut(15));
        detailsPanel.add(descriptionLabel);
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(descScrollPane);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.add(addToCartButton);
        buttonPanel.add(closeButton);
        
        mainPanel.add(imageLabel, BorderLayout.NORTH);
        mainPanel.add(detailsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        detailDialog.add(mainPanel);
        detailDialog.setVisible(true);
    }
    
    private void addToCart(int productId, String productName) {
        try (Connection conn = DBConnection.getConnection()) {
            // Check if product already in cart
            String checkQuery = "SELECT * FROM Cart WHERE customer_id = ? AND product_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, customerId);
            checkStmt.setInt(2, productId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Product already in cart, update quantity
                int currentQuantity = rs.getInt("quantity");
                String updateQuery = "UPDATE Cart SET quantity = ? WHERE customer_id = ? AND product_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, currentQuantity + 1);
                updateStmt.setInt(2, customerId);
                updateStmt.setInt(3, productId);
                updateStmt.executeUpdate();
            } else {
                // Add new product to cart
                String insertQuery = "INSERT INTO Cart (customer_id, product_id, quantity) VALUES (?, ?, 1)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setInt(1, customerId);
                insertStmt.setInt(2, productId);
                insertStmt.executeUpdate();
            }
            
            JOptionPane.showMessageDialog(this, 
                productName + " added to your cart!", 
                "Added to Cart", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error adding to cart: " + ex.getMessage(), 
                "Cart Error", 
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
            new BrowseProductsForm().setVisible(true);
        });
    }
}