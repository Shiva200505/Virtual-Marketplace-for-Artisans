package AKT;

//package com.virtualmarketplace; // Replace with your package name (e.g., AKT)

/*2.4 Test the View Products Functionality
Run the ArtisanDashboard.java file.

Click View Products to open the ViewProductsForm.

Verify the products added by the artisan are displayed.*/

/*2.3 Explanation of the Code
View Products Form:

Displays a list of products added by the logged-in artisan.

A Back button to return to the ArtisanDashboard.

loadProducts() Method:

Fetches products from the Products table for the logged-in artisan (hardcoded to artisan_id = 1 for now).*/
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class ViewProductsForm extends JFrame {
    private JTable productsTable;
    private JButton addButton, editButton, deleteButton, backButton;
    private JTextField searchField;
    private JComboBox<String> categoryFilter;
    private int artisanId = 1; // Default to artisan ID 1, should be replaced with logged-in artisan's ID
    private JButton viewCartButton;
    private int customerId;
    private JButton purchaseButton;
    private JTable productTable;
    private DefaultTableModel tableModel;

    public ViewProductsForm() {
        setTitle("Manage Your Products - Virtual Marketplace");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Main container with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        
        // Create content panel (center)
        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        
        // Create filter panel
        JPanel filterPanel = createFilterPanel();
        contentPanel.add(filterPanel, BorderLayout.NORTH);
        
        // Create table model
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Add columns
        model.addColumn("ID");
        model.addColumn("Product Image");
        model.addColumn("Name");
        model.addColumn("Price");
        model.addColumn("Category");
        model.addColumn("Stock");
        model.addColumn("Description");
        
        // Create table
        productsTable = new JTable(model);
        productsTable.setRowHeight(80);
        productsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set up renderers for the image column
        productsTable.getColumnModel().getColumn(1).setCellRenderer(new ProductImageRenderer());
        
        // Set column widths
        productsTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        productsTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Image
        productsTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Name
        productsTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Price
        productsTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Category
        productsTable.getColumnModel().getColumn(5).setPreferredWidth(60);  // Stock
        productsTable.getColumnModel().getColumn(6).setPreferredWidth(200); // Description
        
        JScrollPane scrollPane = new JScrollPane(productsTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add panels to main container
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Center window on screen
        setLocationRelativeTo(null);
        
        // Load products initially
        loadProducts(null);
        
        // Add action listeners
        backButton.addActionListener(e -> {
            new ArtisanDashboard(artisanId).setVisible(true);
            dispose();
        });
        
        addButton.addActionListener(e -> {
            new AddProductForm().setVisible(true);
            dispose();
        });
        
        editButton.addActionListener(e -> {
            editSelectedProduct();
        });
        
        deleteButton.addActionListener(e -> {
            deleteSelectedProduct();
        });
        
        categoryFilter.addActionListener(e -> {
            String category = categoryFilter.getSelectedItem().toString();
            if (category.equals("All Categories")) {
                loadProducts(null);
            } else {
                loadProducts(category);
            }
        });
        
        viewCartButton.addActionListener(e -> {
            new ShoppingCartForm(customerId).setVisible(true);
            dispose();
        });
    }
    
    public ViewProductsForm(int customerId) {
        this.customerId = customerId;
        
        setTitle("Browse Products - Virtual Marketplace");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create main panel with border layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create heading
        JLabel headingLabel = new JLabel("Available Products");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headingLabel.setHorizontalAlignment(JLabel.LEFT);
        
        // Create table model with columns
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Price");
        tableModel.addColumn("Category");
        tableModel.addColumn("Artisan");
        
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.setRowHeight(30);
        
        // Set column widths
        productTable.getColumnModel().getColumn(0).setPreferredWidth(50);    // ID
        productTable.getColumnModel().getColumn(1).setPreferredWidth(200);   // Name
        productTable.getColumnModel().getColumn(2).setPreferredWidth(80);    // Price
        productTable.getColumnModel().getColumn(3).setPreferredWidth(100);   // Category
        productTable.getColumnModel().getColumn(4).setPreferredWidth(150);   // Artisan
        
        JScrollPane scrollPane = new JScrollPane(productTable);
        
        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        viewCartButton = new JButton("View Cart");
        purchaseButton = new JButton("Add to Cart");
        backButton = new JButton("Back to Dashboard");
        
        buttonPanel.add(viewCartButton);
        buttonPanel.add(purchaseButton);
        buttonPanel.add(backButton);
        
        // Add components to main panel
        mainPanel.add(headingLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Center the window
        setLocationRelativeTo(null);
        
        // Load products
        loadCustomerProducts();
        
        // Add action listeners
        backButton.addActionListener(e -> {
            // Go back to dashboard
            dispose();
            new EnhancedCustomerDashboard(customerId).setVisible(true);
        });
        
        purchaseButton.addActionListener(e -> {
            addSelectedProductToCart();
        });
        
        viewCartButton.addActionListener(e -> {
            new ShoppingCartForm(customerId).setVisible(true);
            dispose();
        });
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Manage Your Products", JLabel.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(204, 102, 0));
        
        backButton = new JButton("Back to Dashboard");
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(backButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel categoryLabel = new JLabel("Category: ");
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Load categories from database
        String[] categories = {"All Categories", "Pottery", "Textiles", "Woodworking", "Jewelry", "Paper Crafts", "Glass Art"};
        categoryFilter = new JComboBox<>(categories);
        categoryFilter.setPreferredSize(new Dimension(150, 30));
        
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        JButton searchButton = new JButton("Search");
        
        panel.add(categoryLabel);
        panel.add(categoryFilter);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(searchField);
        panel.add(searchButton);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        addButton = new JButton("Add New Product");
        editButton = new JButton("Edit Selected");
        deleteButton = new JButton("Delete Selected");
        viewCartButton = new JButton("View Cart");
        
        // Styling
        addButton.setBackground(new Color(70, 130, 180));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(viewCartButton);
        
        return panel;
    }
    
    private void loadProducts(String category) {
        DefaultTableModel model = (DefaultTableModel) productsTable.getModel();
        model.setRowCount(0); // Clear existing data
        
        try (Connection conn = DBConnection.getConnection()) {
            String query;
            PreparedStatement pstmt;
            
            if (category == null) {
                query = "SELECT * FROM Products WHERE artisan_id = ? ORDER BY name";
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, artisanId);
            } else {
                query = "SELECT * FROM Products WHERE artisan_id = ? AND category = ? ORDER BY name";
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, artisanId);
                pstmt.setString(2, category);
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String productCategory = rs.getString("category");
                String description = rs.getString("description");
                int stock = rs.getInt("stock_quantity");
                String image = rs.getString("image");
                
                // For the image column, we'll store the image path to be rendered by the custom renderer
                model.addRow(new Object[]{
                    id, image, name, price, productCategory, stock, description
                });
            }
            
            if (model.getRowCount() == 0) {
                if (category != null) {
                    JOptionPane.showMessageDialog(this,
                        "No products found in category: " + category,
                        "No Products",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "You haven't added any products yet!",
                        "No Products",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + ex.getMessage());
        }
    }
    
    private void editSelectedProduct() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a product to edit!", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int productId = (int) productsTable.getValueAt(selectedRow, 0);
        
        // In a real application, we would open an edit form here
        JOptionPane.showMessageDialog(this, 
            "Edit product ID: " + productId, 
            "Edit Product", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void deleteSelectedProduct() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a product to delete!", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int productId = (int) productsTable.getValueAt(selectedRow, 0);
        String productName = (String) productsTable.getValueAt(selectedRow, 2);
        
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete the product: " + productName + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String query = "DELETE FROM Products WHERE id = ? AND artisan_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, productId);
                pstmt.setInt(2, artisanId);
                
                int rowsDeleted = pstmt.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Product deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh the table
                    String category = categoryFilter.getSelectedItem().toString();
                    loadProducts(category.equals("All Categories") ? null : category);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to delete product. It may no longer exist or you don't have permission.",
                        "Delete Failed",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting product: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Custom renderer for product images
    private class ProductImageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof String) {
                String imagePath = (String) value;
                return ImageUtils.loadProductImage(imagePath, 70, 70);
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    private void loadCustomerProducts() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT p.id, p.name, p.price, c.name as category, a.name as artisan " +
                           "FROM Products p " +
                           "JOIN Categories c ON p.category_id = c.id " +
                           "JOIN Artisans a ON p.artisan_id = a.id " +
                           "ORDER BY p.id";
                           
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String category = rs.getString("category");
                String artisan = rs.getString("artisan");
                
                tableModel.addRow(new Object[]{id, name, String.format("$%.2f", price), category, artisan});
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading products: " + ex.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addSelectedProductToCart() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a product to add to cart", 
                "No Product Selected", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        String productName = (String) tableModel.getValueAt(selectedRow, 1);
        
        try (Connection conn = DBConnection.getConnection()) {
            // Check if product is already in cart
            String checkQuery = "SELECT quantity FROM Cart WHERE customer_id = ? AND product_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, customerId);
            checkStmt.setInt(2, productId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Update quantity if already in cart
                int currentQuantity = rs.getInt("quantity");
                String updateQuery = "UPDATE Cart SET quantity = ? WHERE customer_id = ? AND product_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, currentQuantity + 1);
                updateStmt.setInt(2, customerId);
                updateStmt.setInt(3, productId);
                updateStmt.executeUpdate();
            } else {
                // Add new entry to cart
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
                
            // Ask if user wants to view cart
            int response = JOptionPane.showConfirmDialog(this,
                "Item added to cart. Would you like to view your cart?",
                "View Cart",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
                
            if (response == JOptionPane.YES_OPTION) {
                new ShoppingCartForm(customerId).setVisible(true);
                dispose();
            }
            
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
            new ViewProductsForm().setVisible(true);
        });
    }
}