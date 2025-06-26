package AKT;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.List;

public class EnhancedCustomerDashboard extends JFrame {
    private int customerId;
    private String customerName;
    private JPanel mainPanel, recommendedPanel, categoriesPanel, contentPanel;
    private JPanel headerPanel, sidebarPanel;
    private JPanel productPanel, cartPanel, orderPanel, profilePanel;
    private JButton browseBtn, profileBtn, historyBtn, cartBtn, logoutBtn;
    private JLabel welcomeLabel, balanceLabel;
    private Map<Integer, Integer> userRatings = new HashMap<>(); // productId -> rating
    private JTable productTable, cartTable, orderTable;
    private DefaultTableModel productTableModel, cartTableModel, orderTableModel;
    private JTextField searchField;
    private JComboBox<String> categoryComboBox;
    private JButton searchButton;
    private JButton addToCartButton, removeFromCartButton, checkoutButton, viewOrderButton, editProfileButton;
    private List<Product> products;
    private List<CartItem> cartItems;
    private List<Order> orders;
    
    public EnhancedCustomerDashboard(int customerId) {
        this.customerId = customerId;
        
        // Get customer name from database
        customerName = getCustomerName(customerId);
        
        setTitle("Customer Dashboard - Virtual Marketplace");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize components
        initializeComponents();
        loadCustomerData();
        loadProducts();
        loadCart();
        loadOrders();
        
        // Set up the main layout
        setupLayout();
    }
    
    private void initializeComponents() {
        // Main panel with gradient background
        mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(240, 240, 240), 0, getHeight(), new Color(220, 220, 220));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Add featured products carousel
        JPanel featuredPanel = new JPanel(new BorderLayout());
        featuredPanel.setOpaque(false);
        featuredPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel featuredLabel = new JLabel("Featured Products");
        featuredLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        featuredLabel.setForeground(new Color(0, 102, 204));
        featuredPanel.add(featuredLabel, BorderLayout.NORTH);
        
        JPanel carouselPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        carouselPanel.setOpaque(false);
        // Add sample featured products with animations
        for (int i = 0; i < 5; i++) {
            JPanel featuredCard = createFeaturedProductCard("Featured Product " + (i + 1), 149.99, 4.8);
            carouselPanel.add(featuredCard);
        }
        featuredPanel.add(new JScrollPane(carouselPanel), BorderLayout.CENTER);

        // Add trending products section
        JPanel trendingPanel = new JPanel(new BorderLayout());
        trendingPanel.setOpaque(false);
        trendingPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel trendingLabel = new JLabel("Trending Now");
        trendingLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        trendingLabel.setForeground(new Color(0, 102, 204));
        trendingPanel.add(trendingLabel, BorderLayout.NORTH);
        
        JPanel trendingGrid = new JPanel(new GridLayout(2, 3, 20, 20));
        trendingGrid.setOpaque(false);
        // Add sample trending products
        for (int i = 0; i < 6; i++) {
            JPanel trendingCard = createTrendingProductCard("Trending " + (i + 1), 79.99, 4.5);
            trendingGrid.add(trendingCard);
        }
        trendingPanel.add(trendingGrid, BorderLayout.CENTER);

        // Add these panels to the main content
        JPanel topContent = new JPanel(new BorderLayout());
        topContent.setOpaque(false);
        topContent.add(featuredPanel, BorderLayout.NORTH);
        topContent.add(trendingPanel, BorderLayout.CENTER);
        
        contentPanel.add(topContent, "TopContent");
        
        // Header panel with glass effect
        headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        // Welcome and balance labels with modern styling
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        infoPanel.setOpaque(false);
        
        welcomeLabel = new JLabel();
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(0, 102, 204));
        
        balanceLabel = new JLabel();
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        balanceLabel.setForeground(new Color(0, 150, 0));
        
        infoPanel.add(welcomeLabel);
        infoPanel.add(balanceLabel);
        
        // Search panel with modern styling
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);
        
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        categoryComboBox = new JComboBox<>(new String[]{"All Categories", "Handmade", "Art", "Craft"});
        categoryComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        categoryComboBox.setBackground(Color.WHITE);
        
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchButton.setBackground(new Color(0, 102, 204));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setBorderPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Category:"));
        searchPanel.add(categoryComboBox);
        searchPanel.add(searchButton);
        
        headerPanel.add(infoPanel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        // Sidebar with modern styling
        sidebarPanel = new JPanel(new GridLayout(6, 1, 0, 10));
        sidebarPanel.setOpaque(false);
        sidebarPanel.setBorder(new EmptyBorder(20, 10, 20, 10));
        
        JButton[] menuButtons = new JButton[6];
        String[] buttonLabels = {"Products", "Cart", "Orders", "Profile", "Settings", "Logout"};
        String[] iconNames = {"products.png", "cart.png", "orders.png", "profile.png", "settings.png", "logout.png"};
        Color[] buttonColors = {
            new Color(0, 102, 204),    // Products - Blue
            new Color(255, 153, 0),    // Cart - Orange
            new Color(0, 150, 0),      // Orders - Green
            new Color(153, 0, 204),    // Profile - Purple
            new Color(102, 102, 102),  // Settings - Gray
            new Color(204, 0, 0)       // Logout - Red
        };
        
        for (int i = 0; i < menuButtons.length; i++) {
            menuButtons[i] = new JButton(buttonLabels[i]);
            menuButtons[i].setFont(new Font("Segoe UI", Font.BOLD, 14));
            menuButtons[i].setBackground(buttonColors[i]);
            menuButtons[i].setForeground(Color.WHITE);
            menuButtons[i].setFocusPainted(false);
            menuButtons[i].setBorderPainted(false);
            menuButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            menuButtons[i].setPreferredSize(new Dimension(180, 50));
            menuButtons[i].setIcon(new ImageIcon("resources/icons/" + iconNames[i]));
            menuButtons[i].setHorizontalAlignment(SwingConstants.LEFT);
            menuButtons[i].setIconTextGap(15);
            
            // Add hover effect
            menuButtons[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    menuButtons[i].setBackground(buttonColors[i].darker());
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    menuButtons[i].setBackground(buttonColors[i]);
                }
            });
            
            sidebarPanel.add(menuButtons[i]);
        }
        
        // Content panel with card layout
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setOpaque(false);
        
        // Product panel with modern table
        productPanel = createTablePanel("Products", new String[]{"ID", "Name", "Category", "Price", "Rating", "Artisan"});
        productTable = (JTable) ((JScrollPane) productPanel.getComponent(1)).getViewport().getView();
        styleTable(productTable);
        
        // Cart panel with modern table
        cartPanel = createTablePanel("Shopping Cart", new String[]{"Product", "Quantity", "Price", "Total"});
        cartTable = (JTable) ((JScrollPane) cartPanel.getComponent(1)).getViewport().getView();
        styleTable(cartTable);
        
        // Order panel with modern table
        orderPanel = createTablePanel("Order History", new String[]{"Order ID", "Date", "Total", "Status"});
        orderTable = (JTable) ((JScrollPane) orderPanel.getComponent(1)).getViewport().getView();
        styleTable(orderTable);
        
        // Profile panel with modern form
        profilePanel = createProfilePanel();
        
        // Add panels to content panel
        contentPanel.add(productPanel, "Products");
        contentPanel.add(cartPanel, "Cart");
        contentPanel.add(orderPanel, "Orders");
        contentPanel.add(profilePanel, "Profile");
        
        // Add action listeners
        menuButtons[0].addActionListener(e -> showPanel("Products"));
        menuButtons[1].addActionListener(e -> showPanel("Cart"));
        menuButtons[2].addActionListener(e -> showPanel("Orders"));
        menuButtons[3].addActionListener(e -> showPanel("Profile"));
        menuButtons[4].addActionListener(e -> showSettings());
        menuButtons[5].addActionListener(e -> logout());
        
        searchButton.addActionListener(e -> searchProducts());
    }
    
    private JPanel createTablePanel(String title, String[] columns) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(0, 102, 204, 50));
        table.setSelectionForeground(new Color(0, 102, 204));
        table.setGridColor(new Color(200, 200, 200));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void styleTable(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(0, 102, 204));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
    }
    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JLabel titleLabel = new JLabel("Profile Settings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        String[] labels = {"Name:", "Email:", "Phone:", "Address:", "New Password:"};
        JTextField[] fields = new JTextField[labels.length];
        
        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setForeground(new Color(0, 102, 204));
            
            fields[i] = new JTextField(20);
            fields[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            fields[i].setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(5, 10, 5, 10)
            ));
            
            gbc.gridx = 0;
            gbc.gridy = i + 1;
            gbc.gridwidth = 1;
            panel.add(label, gbc);
            
            gbc.gridx = 1;
            panel.add(fields[i], gbc);
        }
        
        JButton saveButton = new JButton("Save Changes");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setBackground(new Color(0, 102, 204));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setPreferredSize(new Dimension(150, 40));
        
        gbc.gridx = 0;
        gbc.gridy = labels.length + 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(saveButton, gbc);
        
        return panel;
    }
    
    private void setupLayout() {
        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout(20, 0));
        centerPanel.setOpaque(false);
        centerPanel.add(sidebarPanel, BorderLayout.WEST);
        centerPanel.add(contentPanel, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private void showPanel(String panelName) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, panelName);
    }
    
    private void showSettings() {
            JOptionPane.showMessageDialog(this, 
            "Settings feature coming soon!",
            "Settings",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginForm().setVisible(true);
            dispose();
        }
    }
    
    private void searchProducts() {
        String searchText = searchField.getText().toLowerCase();
        String category = (String) categoryComboBox.getSelectedItem();
        
        productTableModel.setRowCount(0);
        for (Product product : products) {
            if ((category.equals("All Categories") || product.getCategory().equals(category)) &&
                (searchText.isEmpty() || product.getName().toLowerCase().contains(searchText))) {
                productTableModel.addRow(new Object[]{
                    product.getId(),
                    product.getName(),
                    product.getCategory(),
                    String.format("$%.2f", product.getPrice()),
                    String.format("%.1f", product.getRating()),
                    product.getArtisanName()
                });
            }
        }
    }
    
    private void loadCustomerData() {
        // Implementation of loadCustomerData method
    }
    
    private void loadProducts() {
        // Implementation of loadProducts method
    }
    
    private void loadCart() {
        // Implementation of loadCart method
    }
    
    private void loadOrders() {
        // Implementation of loadOrders method
    }
    
    private String getCustomerName(int customerId) {
        String name = "Customer";
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT name FROM Customers WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                name = rs.getString("name");
            }
        } catch (Exception ex) {
            System.err.println("Error retrieving customer name: " + ex.getMessage());
        }
        
        return name;
    }
    
    private JPanel createFeaturedProductCard(String name, double price, double rating) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        card.setPreferredSize(new Dimension(300, 350));
        
        // Add hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(0, 102, 204), 2, true),
                    new EmptyBorder(10, 10, 10, 10)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(200, 200, 200), 1, true),
                    new EmptyBorder(10, 10, 10, 10)
                ));
            }
        });
        
        // Product image with zoom effect
        JLabel imageLabel = new JLabel(new ImageIcon("resources/images/featured_" + name.toLowerCase().replace(" ", "_") + ".png"));
        imageLabel.setPreferredSize(new Dimension(280, 200));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(imageLabel, BorderLayout.NORTH);
        
        // Product info with modern styling
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        infoPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JLabel priceLabel = new JLabel(String.format("$%.2f", price));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        priceLabel.setForeground(new Color(0, 150, 0));
        
        // Star rating with icons
        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        ratingPanel.setOpaque(false);
        for (int i = 0; i < 5; i++) {
            JLabel star = new JLabel(new ImageIcon("resources/icons/" + (i < Math.floor(rating) ? "star_filled.png" : "star_empty.png")));
            ratingPanel.add(star);
        }
        ratingPanel.add(new JLabel(String.format("(%.1f)", rating)));
        
        // Quick action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        actionPanel.setOpaque(false);
        
        JButton quickViewBtn = new JButton("Quick View");
        quickViewBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        quickViewBtn.setBackground(new Color(0, 102, 204));
        quickViewBtn.setForeground(Color.WHITE);
        quickViewBtn.setFocusPainted(false);
        quickViewBtn.setBorderPainted(false);
        quickViewBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton addToCartBtn = new JButton("Add to Cart");
        addToCartBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addToCartBtn.setBackground(new Color(0, 150, 0));
        addToCartBtn.setForeground(Color.WHITE);
        addToCartBtn.setFocusPainted(false);
        addToCartBtn.setBorderPainted(false);
        addToCartBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        actionPanel.add(quickViewBtn);
        actionPanel.add(addToCartBtn);
        
        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);
        infoPanel.add(ratingPanel);
        infoPanel.add(actionPanel);
        
        card.add(infoPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createTrendingProductCard(String name, double price, double rating) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        card.setPreferredSize(new Dimension(200, 250));
        
        // Add hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(0, 102, 204), 2, true),
                    new EmptyBorder(10, 10, 10, 10)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(200, 200, 200), 1, true),
                    new EmptyBorder(10, 10, 10, 10)
                ));
            }
        });
        
        // Product image
        JLabel imageLabel = new JLabel(new ImageIcon("resources/images/trending_" + name.toLowerCase().replace(" ", "_") + ".png"));
        imageLabel.setPreferredSize(new Dimension(180, 150));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(imageLabel, BorderLayout.NORTH);
        
        // Product info
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel priceLabel = new JLabel(String.format("$%.2f", price));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceLabel.setForeground(new Color(0, 150, 0));
        
        // Star rating
        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        ratingPanel.setOpaque(false);
        for (int i = 0; i < 5; i++) {
            JLabel star = new JLabel(new ImageIcon("resources/icons/" + (i < Math.floor(rating) ? "star_filled.png" : "star_empty.png")));
            ratingPanel.add(star);
        }
        
        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);
        infoPanel.add(ratingPanel);
        
        card.add(infoPanel, BorderLayout.CENTER);
        
        // Add to cart button
        JButton addToCartBtn = new JButton("Add to Cart");
        addToCartBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addToCartBtn.setBackground(new Color(0, 102, 204));
        addToCartBtn.setForeground(Color.WHITE);
        addToCartBtn.setFocusPainted(false);
        addToCartBtn.setBorderPainted(false);
        addToCartBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.add(addToCartBtn, BorderLayout.SOUTH);
        
        return card;
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new EnhancedCustomerDashboard(1).setVisible(true);
        });
    }
} 