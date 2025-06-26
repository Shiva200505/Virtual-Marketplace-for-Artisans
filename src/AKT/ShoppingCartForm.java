package AKT;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShoppingCartForm extends JFrame {
    private int customerId;
    private JTable cartTable;
    private JLabel totalLabel;
    private JButton checkoutButton, continueShoppingButton, removeButton;
    private double cartTotal = 0.0;
    
    public ShoppingCartForm(int customerId) {
        this.customerId = customerId;
        
        setTitle("Shopping Cart - Virtual Marketplace");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Main container with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        
        // Create content panel (center)
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        
        // Create table model for cart items
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make only the quantity column editable
                return column == 3;
            }
        };
        
        // Add columns
        model.addColumn("Product ID");
        model.addColumn("Product Name");
        model.addColumn("Price");
        model.addColumn("Quantity");
        model.addColumn("Subtotal");
        
        // Create cart table
        cartTable = new JTable(model);
        cartTable.setRowHeight(30);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set column widths
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(cartTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create total and checkout panel
        JPanel totalPanel = createTotalPanel();
        contentPanel.add(totalPanel, BorderLayout.SOUTH);
        
        // Add panels to main container
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Center window on screen
        setLocationRelativeTo(null);
        
        // Load cart items
        loadCartItems();
        
        // Add action listeners
        continueShoppingButton.addActionListener(e -> {
            new EnhancedCustomerDashboard(customerId).setVisible(true);
            dispose();
        });
        
        removeButton.addActionListener(e -> {
            removeSelectedItem();
        });
        
        checkoutButton.addActionListener(e -> {
            if (cartTotal <= 0) {
                JOptionPane.showMessageDialog(this,
                    "Your cart is empty. Please add items before checkout.",
                    "Empty Cart",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            showCheckoutDialog();
        });
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Your Shopping Cart", JLabel.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        
        continueShoppingButton = new JButton("Continue Shopping");
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(continueShoppingButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createTotalPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Create total section
        JPanel totalSection = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalSection.add(totalLabel);
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        removeButton = new JButton("Remove Selected");
        checkoutButton = new JButton("Proceed to Checkout");
        
        // Style checkout button
        checkoutButton.setBackground(new Color(0, 102, 204));
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        checkoutButton.setFocusPainted(false);
        
        buttonsPanel.add(removeButton);
        buttonsPanel.add(checkoutButton);
        
        panel.add(totalSection, BorderLayout.NORTH);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadCartItems() {
        DefaultTableModel model = (DefaultTableModel) cartTable.getModel();
        model.setRowCount(0); // Clear existing data
        cartTotal = 0.0;
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = 
                "SELECT c.product_id, p.name, p.price, c.quantity " +
                "FROM Cart c " +
                "JOIN Products p ON c.product_id = p.id " +
                "WHERE c.customer_id = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int productId = rs.getInt("product_id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                double subtotal = price * quantity;
                
                model.addRow(new Object[]{
                    productId, name, String.format("$%.2f", price), quantity, String.format("$%.2f", subtotal)
                });
                
                cartTotal += subtotal;
            }
            
            totalLabel.setText(String.format("Total: $%.2f", cartTotal));
            
            if (model.getRowCount() == 0) {
                // Cart is empty
                JOptionPane.showMessageDialog(this,
                    "Your cart is empty. Browse products to add items to your cart.",
                    "Empty Cart",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading cart items: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void removeSelectedItem() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an item to remove",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int productId = (int) cartTable.getValueAt(selectedRow, 0);
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "DELETE FROM Cart WHERE customer_id = ? AND product_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, productId);
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this,
                "Item removed from cart",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Reload cart items
            loadCartItems();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error removing item: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showCheckoutDialog() {
        JDialog checkoutDialog = new JDialog(this, "Checkout", true);
        checkoutDialog.setSize(600, 500);
        checkoutDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create checkout form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Shipping Information
        JLabel shippingLabel = new JLabel("Shipping Information");
        shippingLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(shippingLabel, gbc);
        
        JLabel addressLabel = new JLabel("Address:");
        JTextArea addressArea = new JTextArea(4, 20);
        addressArea.setLineWrap(true);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(addressLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(addressScroll, gbc);
        
        // Payment Information
        JLabel paymentLabel = new JLabel("Payment Information");
        paymentLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(paymentLabel, gbc);
        
        // Payment method selection
        JLabel methodLabel = new JLabel("Payment Method:");
        String[] methods = {"Credit Card", "PayPal", "Bank Transfer"};
        JComboBox<String> methodCombo = new JComboBox<>(methods);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(methodLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(methodCombo, gbc);
        
        // Card details (only shown when Credit Card is selected)
        JPanel cardPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JLabel cardNumberLabel = new JLabel("Card Number:");
        JTextField cardNumberField = new JTextField(16);
        JLabel expiryLabel = new JLabel("Expiry Date (MM/YY):");
        JTextField expiryField = new JTextField(5);
        JLabel cvvLabel = new JLabel("CVV:");
        JTextField cvvField = new JTextField(3);
        
        cardPanel.add(cardNumberLabel);
        cardPanel.add(cardNumberField);
        cardPanel.add(expiryLabel);
        cardPanel.add(expiryField);
        cardPanel.add(cvvLabel);
        cardPanel.add(cvvField);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(cardPanel, gbc);
        
        // Order Summary
        JLabel summaryLabel = new JLabel("Order Summary");
        summaryLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(summaryLabel, gbc);
        
        JLabel itemsLabel = new JLabel("Number of Items:");
        JLabel itemsValueLabel = new JLabel(cartTable.getRowCount() + "");
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        formPanel.add(itemsLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(itemsValueLabel, gbc);
        
        JLabel totalAmountLabel = new JLabel("Total Amount:");
        JLabel totalAmountValueLabel = new JLabel(String.format("$%.2f", cartTotal));
        totalAmountValueLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(totalAmountLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(totalAmountValueLabel, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton placeOrderButton = new JButton("Place Order");
        
        placeOrderButton.setBackground(new Color(0, 102, 204));
        placeOrderButton.setForeground(Color.WHITE);
        placeOrderButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(placeOrderButton);
        
        // Add components to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        methodCombo.addActionListener(e -> {
            String selected = (String) methodCombo.getSelectedItem();
            cardPanel.setVisible(selected.equals("Credit Card"));
            checkoutDialog.pack();
            checkoutDialog.setSize(600, selected.equals("Credit Card") ? 500 : 400);
        });
        
        cancelButton.addActionListener(e -> checkoutDialog.dispose());
        
        placeOrderButton.addActionListener(e -> {
            // Validate input
            if (addressArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(checkoutDialog,
                    "Please enter your shipping address",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String paymentMethod = (String) methodCombo.getSelectedItem();
            
            if (paymentMethod.equals("Credit Card")) {
                if (cardNumberField.getText().trim().isEmpty() ||
                    expiryField.getText().trim().isEmpty() ||
                    cvvField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(checkoutDialog,
                        "Please fill in all card details",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            
            // Process the order
            if (processOrder(addressArea.getText(), paymentMethod)) {
                checkoutDialog.dispose();
                JOptionPane.showMessageDialog(this,
                    "Your order has been placed successfully!",
                    "Order Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);
                    
                // Clear the cart and go back to dashboard
                new EnhancedCustomerDashboard(customerId).setVisible(true);
                dispose();
            }
        });
        
        // Show dialog
        checkoutDialog.add(mainPanel);
        checkoutDialog.setVisible(true);
    }
    
    private boolean processOrder(String shippingAddress, String paymentMethod) {
        try (Connection conn = DBConnection.getConnection()) {
            // Insert order
            String orderQuery = 
                "INSERT INTO Orders (customer_id, status, total_amount, shipping_address, payment_method, payment_status) " +
                "VALUES (?, 'Processing', ?, ?, ?, 'Completed')";
            
            PreparedStatement orderStmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, customerId);
            orderStmt.setDouble(2, cartTotal);
            orderStmt.setString(3, shippingAddress);
            orderStmt.setString(4, paymentMethod);
            
            int result = orderStmt.executeUpdate();
            
            if (result <= 0) {
                throw new Exception("Failed to create order");
            }
            
            // Get the generated order ID
            ResultSet generatedKeys = orderStmt.getGeneratedKeys();
            int orderId;
            
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
            } else {
                throw new Exception("Failed to get order ID");
            }
            
            // Insert order items
            String cartQuery = 
                "SELECT c.product_id, p.price, c.quantity " +
                "FROM Cart c " +
                "JOIN Products p ON c.product_id = p.id " +
                "WHERE c.customer_id = ?";
            
            PreparedStatement cartStmt = conn.prepareStatement(cartQuery);
            cartStmt.setInt(1, customerId);
            ResultSet cartRs = cartStmt.executeQuery();
            
            String itemQuery = 
                "INSERT INTO OrderItems (order_id, product_id, quantity, price) " +
                "VALUES (?, ?, ?, ?)";
            
            PreparedStatement itemStmt = conn.prepareStatement(itemQuery);
            
            while (cartRs.next()) {
                int productId = cartRs.getInt("product_id");
                double price = cartRs.getDouble("price");
                int quantity = cartRs.getInt("quantity");
                
                itemStmt.setInt(1, orderId);
                itemStmt.setInt(2, productId);
                itemStmt.setInt(3, quantity);
                itemStmt.setDouble(4, price);
                itemStmt.executeUpdate();
            }
            
            // Clear the cart
            String clearCartQuery = "DELETE FROM Cart WHERE customer_id = ?";
            PreparedStatement clearCartStmt = conn.prepareStatement(clearCartQuery);
            clearCartStmt.setInt(1, customerId);
            clearCartStmt.executeUpdate();
            
            return true;
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error processing order: " + ex.getMessage(),
                "Order Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new ShoppingCartForm(1).setVisible(true);
        });
    }
} 