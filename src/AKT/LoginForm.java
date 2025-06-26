package AKT; // Replace with your package name
/*1.3 Explanation of the Code
Login Form:

Contains fields for email and password.

A Login button to validate the user.

loginUser() Method:

Checks if the user is an artisan or customer by querying the database.

If the login is successful, it opens the respective dashboard (ArtisanDashboard or CustomerDashboard).

ArtisanDashboard and CustomerDashboard:

We'll create these classes next.*/
import AKT.CustomerDashboard; // Replace with your package name (e.g., AKT)

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginForm extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, backButton;
    private JComboBox<String> userTypeComboBox;
    private JCheckBox rememberMeCheckBox;
    private JButton forgotPasswordButton;

    public LoginForm() {
        setTitle("Login - Virtual Marketplace");
        setSize(700, 600);
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
        
        // Create header panel with logo
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(40, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("Welcome Back!", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        headerLabel.setForeground(Color.WHITE);
        
        JLabel subHeaderLabel = new JLabel("Sign in to continue", SwingConstants.CENTER);
        subHeaderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subHeaderLabel.setForeground(new Color(220, 220, 220));
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setOpaque(false);
        titlePanel.add(headerLabel);
        titlePanel.add(subHeaderLabel);
        
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        
        // Create form panel with glass effect
        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // User Type Selection with modern styling
        JLabel userTypeLabel = new JLabel("Login as:");
        userTypeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userTypeLabel.setForeground(Color.WHITE);
        String[] userTypes = {"Customer", "Artisan"};
        userTypeComboBox = new JComboBox<>(userTypes);
        userTypeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userTypeComboBox.setBackground(new Color(255, 255, 255, 200));
        userTypeComboBox.setForeground(new Color(0, 102, 204));
        userTypeComboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Email field with icon
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setForeground(Color.WHITE);
        emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setBackground(new Color(255, 255, 255, 200));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Password field with icon
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setForeground(Color.WHITE);
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBackground(new Color(255, 255, 255, 200));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Remember me checkbox
        rememberMeCheckBox = new JCheckBox("Remember me");
        rememberMeCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rememberMeCheckBox.setForeground(Color.WHITE);
        rememberMeCheckBox.setOpaque(false);
        
        // Forgot password button
        forgotPasswordButton = new JButton("Forgot Password?");
        forgotPasswordButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotPasswordButton.setForeground(new Color(255, 255, 255));
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add components to form panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(userTypeLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(userTypeComboBox, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(emailLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        
        // Create options panel
        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.setOpaque(false);
        optionsPanel.add(rememberMeCheckBox, BorderLayout.WEST);
        optionsPanel.add(forgotPasswordButton, BorderLayout.EAST);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(optionsPanel, gbc);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(150, 40));
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(255, 255, 255, 200));
        loginButton.setForeground(new Color(0, 102, 204));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        
        backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setBackground(new Color(255, 255, 255, 200));
        backButton.setForeground(new Color(0, 102, 204));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        
        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);
        
        // Create footer panel with registration link
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel registerPrompt = new JLabel("Don't have an account?");
        registerPrompt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        registerPrompt.setForeground(Color.WHITE);
        
        JButton registerLink = new JButton("Register now");
        registerLink.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerLink.setBorderPainted(false);
        registerLink.setContentAreaFilled(false);
        registerLink.setForeground(new Color(255, 255, 255));
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        footerPanel.add(registerPrompt);
        footerPanel.add(registerLink);
        
        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Center window on screen
        setLocationRelativeTo(null);
        
        // Add action listeners
        loginButton.addActionListener(e -> loginUser());
        backButton.addActionListener(e -> {
            new MainLauncher().setVisible(true);
            dispose();
        });
        registerLink.addActionListener(e -> {
            new CustomerRegistration().setVisible(true);
            dispose();
        });
        forgotPasswordButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Please contact the administrator to reset your password.",
                "Password Reset",
                JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void loginUser() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String userType = (String) userTypeComboBox.getSelectedItem();

        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both email and password", 
                "Login Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String table = userType.equals("Artisan") ? "Artisans" : "Customers";
            String query = "SELECT * FROM " + table + " WHERE email = ? AND password = ?";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("id");
                JOptionPane.showMessageDialog(this, "Login successful!");
                
                if (userType.equals("Artisan")) {
                    new ArtisanDashboard(userId).setVisible(true);
                } else {
                    new EnhancedCustomerDashboard(userId).setVisible(true);
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Invalid email or password!", 
                    "Login Failed", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + ex.getMessage(), 
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
            new LoginForm().setVisible(true);
        });
    }
}