package AKT;

//package com.virtualmarketplace;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MainLauncher extends JFrame {
    
    public MainLauncher() {
        setTitle("Virtual Marketplace");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create main panel with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 102, 204), 0, getHeight(), new Color(0, 51, 102));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        // Create banner panel with modern design
        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setOpaque(false);
        bannerPanel.setPreferredSize(new Dimension(getWidth(), 200));
        bannerPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        JLabel titleLabel = new JLabel("Virtual Marketplace for Artisans");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Connect with artisans and discover unique handcrafted products");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(220, 220, 220));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(10));
        titlePanel.add(subtitleLabel);
        
        bannerPanel.add(titlePanel, BorderLayout.CENTER);
        
        // Create content panel with glass effect
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(40, 60, 40, 60));
        
        // Create buttons panel with modern cards
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        buttonsPanel.setOpaque(false);
        
        // Create stylish buttons for different functionalities
        JButton artisanRegBtn = createModernCardButton("Artisan Registration", "Register as an artisan to sell your products", new Color(255, 102, 0));
        JButton customerRegBtn = createModernCardButton("Customer Registration", "Register as a customer to buy products", new Color(0, 153, 0));
        JButton loginBtn = createModernCardButton("User Login", "Login as an artisan or customer", new Color(0, 102, 204));
        JButton adminBtn = createModernCardButton("Admin Login", "Login as an administrator", new Color(102, 0, 204));
        
        // Add buttons to panel
        buttonsPanel.add(artisanRegBtn);
        buttonsPanel.add(customerRegBtn);
        buttonsPanel.add(loginBtn);
        buttonsPanel.add(adminBtn);
        
        // Add content to GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 20, 20, 20);
        
        contentPanel.add(buttonsPanel, gbc);
        
        // Add footer panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JButton exitBtn = new JButton("Exit");
        exitBtn.setPreferredSize(new Dimension(100, 40));
        exitBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        exitBtn.setBackground(new Color(255, 255, 255, 150));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFocusPainted(false);
        exitBtn.setBorderPainted(false);
        
        footerPanel.add(exitBtn);
        
        // Add panels to main container
        mainPanel.add(bannerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Center window on screen
        setLocationRelativeTo(null);
        
        // Add action listeners
        exitBtn.addActionListener(e -> System.exit(0));
        artisanRegBtn.addActionListener(e -> {
            new ArtisanRegistration().setVisible(true);
            dispose();
        });
        customerRegBtn.addActionListener(e -> {
            new CustomerRegistration().setVisible(true);
            dispose();
        });
        loginBtn.addActionListener(e -> {
            new LoginForm().setVisible(true);
            dispose();
        });
        adminBtn.addActionListener(e -> {
            new AdminLogin().setVisible(true);
            dispose();
        });
    }
    
    private JButton createModernCardButton(String title, String description, Color color) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 150));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        
        button.setLayout(new BorderLayout(10, 10));
        button.setPreferredSize(new Dimension(300, 150));
        button.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        button.setFocusPainted(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(240, 240, 240));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(descLabel);
        
        button.add(textPanel, BorderLayout.CENTER);
        
        return button;
    }
    
    public static void main(String[] args) {
        try {
            // Set look and feel to the system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Create directories if needed
            setupApplicationDirectories();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new MainLauncher().setVisible(true);
        });
    }
    
    private static void setupApplicationDirectories() {
        // Create necessary directories
        File resourcesDir = new File("resources");
        if (!resourcesDir.exists()) {
            resourcesDir.mkdir();
        }
        
        File imagesDir = new File("resources/images");
        if (!imagesDir.exists()) {
            imagesDir.mkdir();
            System.out.println("Image directories created at: " + imagesDir.getAbsolutePath());
            System.out.println("Add product images to this directory with the names specified in the database.");
        }
    }
}