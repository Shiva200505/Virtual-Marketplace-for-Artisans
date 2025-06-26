package AKT;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class AdminDashboard extends JFrame {
    private int adminId;
    private JTabbedPane tabbedPane;
    private JTable artisansTable, customersTable, productsTable;
    private JButton approveBtn, rejectBtn, viewDetailsBtn, deleteUserBtn, logoutBtn, refreshBtn;
    
    public AdminDashboard(int adminId) {
        this.adminId = adminId;
        
        setTitle("Admin Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Admin Dashboard", JLabel.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshBtn = new JButton("Refresh Data");
        logoutBtn = new JButton("Logout");
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(logoutBtn);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Create tabbed pane for different sections
        tabbedPane = new JTabbedPane();
        
        // Create Artisans panel
        JPanel artisansPanel = createArtisansPanel();
        tabbedPane.addTab("Artisans", artisansPanel);
        
        // Create Customers panel
        JPanel customersPanel = createCustomersPanel();
        tabbedPane.addTab("Customers", customersPanel);
        
        // Create Products panel
        JPanel productsPanel = createProductsPanel();
        tabbedPane.addTab("Products", productsPanel);
        
        // Create Statistics panel
        JPanel statsPanel = createStatsPanel();
        tabbedPane.addTab("Statistics", statsPanel);
        
        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Center on screen
        setLocationRelativeTo(null);
        
        // Add action listeners
        refreshBtn.addActionListener(e -> refreshAllData());
        
        logoutBtn.addActionListener(e -> {
            new AdminLogin().setVisible(true);
            dispose();
        });
        
        // Load initial data
        refreshAllData();
    }
    
    private JPanel createArtisansPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table model with columns
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Email");
        model.addColumn("Phone");
        model.addColumn("Status");
        
        // Create table with model
        artisansTable = new JTable(model);
        artisansTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(artisansTable);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        approveBtn = new JButton("Approve Selected");
        rejectBtn = new JButton("Reject Selected");
        viewDetailsBtn = new JButton("View Details");
        deleteUserBtn = new JButton("Delete User");
        
        buttonPanel.add(approveBtn);
        buttonPanel.add(rejectBtn);
        buttonPanel.add(viewDetailsBtn);
        buttonPanel.add(deleteUserBtn);
        
        // Add components to panel
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        approveBtn.addActionListener(e -> approveArtisan());
        rejectBtn.addActionListener(e -> rejectArtisan());
        viewDetailsBtn.addActionListener(e -> viewArtisanDetails());
        deleteUserBtn.addActionListener(e -> deleteUser("Artisans"));
        
        return panel;
    }
    
    private JPanel createCustomersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table model with columns
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Email");
        model.addColumn("Phone");
        
        // Create table with model
        customersTable = new JTable(model);
        customersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(customersTable);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton viewCustomerBtn = new JButton("View Details");
        JButton deleteCustomerBtn = new JButton("Delete User");
        
        buttonPanel.add(viewCustomerBtn);
        buttonPanel.add(deleteCustomerBtn);
        
        // Add components to panel
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        viewCustomerBtn.addActionListener(e -> viewCustomerDetails());
        deleteCustomerBtn.addActionListener(e -> deleteUser("Customers"));
        
        return panel;
    }
    
    private JPanel createProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table model with columns
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Price");
        model.addColumn("Category");
        model.addColumn("Artisan");
        
        // Create table with model
        productsTable = new JTable(model);
        productsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(productsTable);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton viewProductBtn = new JButton("View Product Details");
        JButton deleteProductBtn = new JButton("Delete Product");
        
        buttonPanel.add(viewProductBtn);
        buttonPanel.add(deleteProductBtn);
        
        // Add components to panel
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        viewProductBtn.addActionListener(e -> viewProductDetails());
        deleteProductBtn.addActionListener(e -> deleteProduct());
        
        return panel;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create stat panels
        panel.add(createStatCard("Total Artisans", "0", new Color(41, 128, 185)));
        panel.add(createStatCard("Total Customers", "0", new Color(39, 174, 96)));
        panel.add(createStatCard("Total Products", "0", new Color(142, 68, 173)));
        panel.add(createStatCard("Total Sales", "$0", new Color(230, 126, 34)));
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout(5, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(color);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Data operations
    private void refreshAllData() {
        loadArtisansData();
        loadCustomersData();
        loadProductsData();
        updateStatistics();
    }
    
    private void loadArtisansData() {
        DefaultTableModel model = (DefaultTableModel) artisansTable.getModel();
        model.setRowCount(0); // Clear existing data
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT id, name, email, phone, approved FROM Artisans";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                boolean approved = rs.getBoolean("approved");
                
                model.addRow(new Object[]{
                    id, name, email, phone, approved ? "Approved" : "Pending"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading artisans: " + ex.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadCustomersData() {
        DefaultTableModel model = (DefaultTableModel) customersTable.getModel();
        model.setRowCount(0); // Clear existing data
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT id, name, email, phone FROM Customers";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                
                model.addRow(new Object[]{
                    id, name, email, phone
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading customers: " + ex.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadProductsData() {
        DefaultTableModel model = (DefaultTableModel) productsTable.getModel();
        model.setRowCount(0); // Clear existing data
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT p.id, p.name, p.price, p.category, a.name as artisan_name " +
                           "FROM Products p " +
                           "JOIN Artisans a ON p.artisan_id = a.id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String category = rs.getString("category");
                String artisanName = rs.getString("artisan_name");
                
                model.addRow(new Object[]{
                    id, name, String.format("$%.2f", price), category, artisanName
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading products: " + ex.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateStatistics() {
        try (Connection conn = DBConnection.getConnection()) {
            // Update artisans count
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Artisans");
            if (rs.next()) {
                int count = rs.getInt("count");
                updateStatValue("Total Artisans", String.valueOf(count));
            }
            
            // Update customers count
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Customers");
            if (rs.next()) {
                int count = rs.getInt("count");
                updateStatValue("Total Customers", String.valueOf(count));
            }
            
            // Update products count
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Products");
            if (rs.next()) {
                int count = rs.getInt("count");
                updateStatValue("Total Products", String.valueOf(count));
            }
            
            // Update sales amount
            rs = stmt.executeQuery("SELECT SUM(total_amount) as total FROM Orders");
            if (rs.next()) {
                double total = rs.getDouble("total");
                updateStatValue("Total Sales", String.format("$%.2f", total));
            }
        } catch (Exception ex) {
            System.err.println("Error updating statistics: " + ex.getMessage());
        }
    }
    
    private void updateStatValue(String title, String value) {
        JPanel statsPanel = (JPanel) tabbedPane.getComponentAt(3);
        for (Component comp : statsPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel card = (JPanel) comp;
                Component titleComp = ((BorderLayout) card.getLayout()).getLayoutComponent(BorderLayout.NORTH);
                if (titleComp instanceof JLabel && ((JLabel) titleComp).getText().equals(title)) {
                    Component valueComp = ((BorderLayout) card.getLayout()).getLayoutComponent(BorderLayout.CENTER);
                    if (valueComp instanceof JLabel) {
                        ((JLabel) valueComp).setText(value);
                        break;
                    }
                }
            }
        }
    }
    
    private void approveArtisan() {
        int selectedRow = artisansTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an artisan to approve!");
            return;
        }
        
        int artisanId = (int) artisansTable.getValueAt(selectedRow, 0);
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "UPDATE Artisans SET approved = true WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, artisanId);
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Artisan has been approved successfully!");
                loadArtisansData(); // Refresh data
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error approving artisan: " + ex.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void rejectArtisan() {
        int selectedRow = artisansTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an artisan to reject!");
            return;
        }
        
        int artisanId = (int) artisansTable.getValueAt(selectedRow, 0);
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "UPDATE Artisans SET approved = false WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, artisanId);
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Artisan has been rejected!");
                loadArtisansData(); // Refresh data
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error rejecting artisan: " + ex.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewArtisanDetails() {
        int selectedRow = artisansTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an artisan to view!");
            return;
        }
        
        int artisanId = (int) artisansTable.getValueAt(selectedRow, 0);
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM Artisans WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, artisanId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String details = "Artisan Details:\n\n" +
                                "ID: " + rs.getInt("id") + "\n" +
                                "Name: " + rs.getString("name") + "\n" +
                                "Email: " + rs.getString("email") + "\n" +
                                "Phone: " + rs.getString("phone") + "\n" +
                                "Address: " + rs.getString("address") + "\n" +
                                "Status: " + (rs.getBoolean("approved") ? "Approved" : "Pending");
                
                JOptionPane.showMessageDialog(this, details, "Artisan Details", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading artisan details: " + ex.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewCustomerDetails() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to view!");
            return;
        }
        
        int customerId = (int) customersTable.getValueAt(selectedRow, 0);
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM Customers WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String details = "Customer Details:\n\n" +
                                "ID: " + rs.getInt("id") + "\n" +
                                "Name: " + rs.getString("name") + "\n" +
                                "Email: " + rs.getString("email") + "\n" +
                                "Phone: " + rs.getString("phone") + "\n" +
                                "Address: " + rs.getString("address");
                
                JOptionPane.showMessageDialog(this, details, "Customer Details", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading customer details: " + ex.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewProductDetails() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to view!");
            return;
        }
        
        int productId = (int) productsTable.getValueAt(selectedRow, 0);
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT p.*, a.name as artisan_name FROM Products p " +
                           "JOIN Artisans a ON p.artisan_id = a.id " +
                           "WHERE p.id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String details = "Product Details:\n\n" +
                                "ID: " + rs.getInt("id") + "\n" +
                                "Name: " + rs.getString("name") + "\n" +
                                "Price: $" + rs.getDouble("price") + "\n" +
                                "Category: " + rs.getString("category") + "\n" +
                                "Description: " + rs.getString("description") + "\n" +
                                "Artisan: " + rs.getString("artisan_name");
                
                JOptionPane.showMessageDialog(this, details, "Product Details", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading product details: " + ex.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteUser(String table) {
        JTable targetTable = table.equals("Artisans") ? artisansTable : customersTable;
        int selectedRow = targetTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete!");
            return;
        }
        
        int userId = (int) targetTable.getValueAt(selectedRow, 0);
        String userName = (String) targetTable.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to delete " + userName + "?\nThis action cannot be undone.",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String query = "DELETE FROM " + table + " WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, userId);
                int result = pstmt.executeUpdate();
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "User deleted successfully!");
                    refreshAllData(); // Refresh all data
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting user: " + ex.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteProduct() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete!");
            return;
        }
        
        int productId = (int) productsTable.getValueAt(selectedRow, 0);
        String productName = (String) productsTable.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to delete product '" + productName + "'?\nThis action cannot be undone.",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String query = "DELETE FROM Products WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, productId);
                int result = pstmt.executeUpdate();
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Product deleted successfully!");
                    loadProductsData(); // Refresh products data
                    updateStatistics(); // Update statistics
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting product: " + ex.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new AdminDashboard(1).setVisible(true);
        });
    }
} 