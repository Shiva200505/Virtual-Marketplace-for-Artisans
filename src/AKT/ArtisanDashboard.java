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
import java.util.*;
import java.util.List;

public class ArtisanDashboard extends JFrame {
    private int artisanId;
    private String artisanName;
    private JPanel recentProductsPanel, statisticsPanel;
    private JButton addProductBtn, viewProductsBtn, profileBtn, ordersBtn, logoutBtn;
    private JTable ordersTable;
    
    public ArtisanDashboard(int artisanId) {
        this.artisanId = artisanId;
        
        // Get artisan name from database
        artisanName = getArtisanName(artisanId);
        
        setTitle("Virtual Marketplace - Artisan Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create the main container
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Create header panel with welcome message and buttons
        JPanel headerPanel = createHeaderPanel();
        
        // Create sidebar for navigation
        JPanel sidebarPanel = createSidebarPanel();
        
        // Create content panel that will hold different views
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        // Dashboard content
        JPanel dashboardPanel = new JPanel(new BorderLayout(0, 20));
        
        // Welcome section
        JPanel welcomeSection = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + artisanName + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        welcomeSection.add(welcomeLabel, BorderLayout.NORTH);
        
        // Create recent products panel
        JLabel recentProductsTitle = new JLabel("Your Products");
        recentProductsTitle.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeSection.add(recentProductsTitle, BorderLayout.SOUTH);
        
        dashboardPanel.add(welcomeSection, BorderLayout.NORTH);
        
        // Panel to hold recent products in a grid
        recentProductsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        loadRecentProducts();
        
        // Scroll pane for recent products
        JScrollPane productsScrollPane = new JScrollPane(recentProductsPanel);
        productsScrollPane.setBorder(null);
        productsScrollPane.setPreferredSize(new Dimension(800, 220));
        
        dashboardPanel.add(productsScrollPane, BorderLayout.CENTER);
        
        // Orders section
        JPanel ordersSection = new JPanel(new BorderLayout());
        JLabel ordersTitle = new JLabel("Recent Orders for Your Products");
        ordersTitle.setFont(new Font("Arial", Font.BOLD, 18));
        ordersTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        ordersSection.add(ordersTitle, BorderLayout.NORTH);
        
        // Create orders table
        ordersTable = createOrdersTable();
        JScrollPane ordersScrollPane = new JScrollPane(ordersTable);
        ordersScrollPane.setPreferredSize(new Dimension(800, 200));
        ordersSection.add(ordersScrollPane, BorderLayout.CENTER);
        
        dashboardPanel.add(ordersSection, BorderLayout.SOUTH);
        
        // Add dashboard to content panel
        contentPanel.add(dashboardPanel, BorderLayout.CENTER);
        
        // Add panels to main container
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Center on screen
        setLocationRelativeTo(null);
    }
    
    // Constructor for compatibility with existing code that doesn't pass artisanId
    public ArtisanDashboard() {
        this(1); // Default to artisan ID 1
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JLabel logoLabel = new JLabel("Virtual Marketplace - Artisan Portal", JLabel.LEFT);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        logoLabel.setForeground(new Color(204, 102, 0));
        
        // Panel for the right side icons
        JPanel iconsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Notifications and profile buttons
        JButton notificationsButton = new JButton("Notifications");
        JButton profileButton = new JButton("Profile");
        
        iconsPanel.add(notificationsButton);
        iconsPanel.add(profileButton);
        
        panel.add(logoLabel, BorderLayout.WEST);
        panel.add(iconsPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createSidebarPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.setPreferredSize(new Dimension(180, getHeight()));
        panel.setBackground(new Color(245, 245, 245));
        
        addProductBtn = createSidebarButton("Add New Product");
        viewProductsBtn = createSidebarButton("Manage Products");
        profileBtn = createSidebarButton("My Profile");
        ordersBtn = createSidebarButton("Orders");
        logoutBtn = createSidebarButton("Logout");
        
        panel.add(Box.createVerticalStrut(10));
        panel.add(addProductBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(viewProductsBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(profileBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(ordersBtn);
        panel.add(Box.createVerticalGlue());
        panel.add(logoutBtn);
        panel.add(Box.createVerticalStrut(20));
        
        // Add action listeners
        addProductBtn.addActionListener(e -> {
            new AddProductForm().setVisible(true);
            dispose();
        });
        
        viewProductsBtn.addActionListener(e -> {
            new ViewProductsForm().setVisible(true);
            dispose();
        });
        
        logoutBtn.addActionListener(e -> {
            new LoginForm().setVisible(true);
            dispose();
        });
        
        return panel;
    }
    
    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(170, 40));
        button.setFocusPainted(false);
        button.setBackground(new Color(245, 245, 245));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        // Button hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(230, 230, 230));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(204, 102, 0)),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(245, 245, 245));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
                ));
            }
        });
        
        return button;
    }
    
    private void loadRecentProducts() {
        recentProductsPanel.removeAll();
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT id, name, price, category, image, description " +
                           "FROM Products " +
                           "WHERE artisan_id = ? " +
                           "ORDER BY id DESC LIMIT 3";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, artisanId);
            ResultSet rs = pstmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                int productId = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String category = rs.getString("category");
                String imageName = rs.getString("image");
                String description = rs.getString("description");
                
                // Create product card
                JPanel productCard = createProductCard(productId, name, price, category, imageName, description);
                recentProductsPanel.add(productCard);
                count++;
            }
            
            if (count == 0) {
                JPanel noProductsPanel = createNoProductsPanel();
                recentProductsPanel.add(noProductsPanel);
            }
            
            // Fill remaining slots if needed
            for (int i = count; i < 3; i++) {
                recentProductsPanel.add(new JPanel());
            }
            
            recentProductsPanel.revalidate();
            recentProductsPanel.repaint();
        } catch (Exception ex) {
            System.err.println("Error loading recent products: " + ex.getMessage());
        }
    }
    
    private JPanel createNoProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel messageLabel = new JLabel("You haven't added any products yet.");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JButton addNewBtn = new JButton("Add Your First Product");
        addNewBtn.addActionListener(e -> {
            new AddProductForm().setVisible(true);
            dispose();
        });
        
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(addNewBtn, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createProductCard(int productId, String name, double price, String category, String imageName, String description) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Product image
        JLabel imageLabel = ImageUtils.loadProductImage(imageName, 150, 150);
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        // Product info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel priceLabel = new JLabel(String.format("$%.2f", price));
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JLabel categoryLabel = new JLabel("Category: " + category);
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(categoryLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton editButton = new JButton("Edit");
        
        buttonPanel.add(editButton);
        
        // Add components to card
        card.add(imagePanel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add mouse hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(204, 102, 0), 2),
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
        editButton.addActionListener(e -> {
            // Open edit product form - this would be implemented in a real app
            JOptionPane.showMessageDialog(this, 
                "Edit product: " + name, 
                "Edit Product", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        return card;
    }
    
    private JTable createOrdersTable() {
        // Create table model
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Add columns
        model.addColumn("Order ID");
        model.addColumn("Product");
        model.addColumn("Customer");
        model.addColumn("Quantity");
        model.addColumn("Price");
        model.addColumn("Date");
        model.addColumn("Status");
        
        // Create table
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        
        // Load orders data
        loadOrdersData(model);
        
        return table;
    }
    
    private void loadOrdersData(DefaultTableModel model) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = 
                "SELECT o.id as order_id, p.name as product_name, c.name as customer_name, " +
                "oi.quantity, oi.price, o.order_date, o.status " +
                "FROM Orders o " +
                "JOIN OrderItems oi ON o.id = oi.order_id " +
                "JOIN Products p ON oi.product_id = p.id " +
                "JOIN Customers c ON o.customer_id = c.id " +
                "WHERE p.artisan_id = ? " +
                "ORDER BY o.order_date DESC LIMIT 10";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, artisanId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getInt("order_id"),
                    rs.getString("product_name"),
                    rs.getString("customer_name"),
                    rs.getInt("quantity"),
                    String.format("$%.2f", rs.getDouble("price")),
                    rs.getTimestamp("order_date"),
                    rs.getString("status")
                });
            }
            
            // If no orders found, add a message row
            if (model.getRowCount() == 0) {
                model.addRow(new Object[] {
                    "", "No orders found", "", "", "", "", ""
                });
            }
        } catch (Exception ex) {
            System.err.println("Error loading orders data: " + ex.getMessage());
        }
    }
    
    private String getArtisanName(int artisanId) {
        String name = "Artisan";
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT name FROM Artisans WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, artisanId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                name = rs.getString("name");
            }
        } catch (Exception ex) {
            System.err.println("Error retrieving artisan name: " + ex.getMessage());
        }
        
        return name;
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new ArtisanDashboard(1).setVisible(true);
        });
    }
}