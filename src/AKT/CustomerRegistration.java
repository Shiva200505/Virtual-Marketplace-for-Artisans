package AKT;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class CustomerRegistration extends JFrame {
    private JTextField nameField, emailField, phoneField, addressField;
    private JPasswordField passwordField;
    private JButton registerButton, backButton;

    public CustomerRegistration() {
        setTitle("Customer Registration");
        setSize(600, 600);
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
        
        // Create header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(40, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("Create Your Account");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        
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
        
        // Initialize fields with modern styling
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        phoneField = new JTextField(20);
        addressField = new JTextField(20);
        
        // Style all text fields
        for (JTextField field : new JTextField[]{nameField, emailField, phoneField, addressField}) {
            field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            field.setBackground(new Color(255, 255, 255, 200));
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
        }
        
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBackground(new Color(255, 255, 255, 200));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Add form fields with labels
        addFormField(formPanel, "Name:", nameField, gbc, 0);
        addFormField(formPanel, "Email:", emailField, gbc, 1);
        addFormField(formPanel, "Password:", passwordField, gbc, 2);
        addFormField(formPanel, "Phone:", phoneField, gbc, 3);
        addFormField(formPanel, "Address:", addressField, gbc, 4);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        
        registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(150, 40));
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerButton.setBackground(new Color(255, 255, 255, 200));
        registerButton.setForeground(new Color(0, 102, 204));
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        
        backButton = new JButton("Back to Menu");
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setBackground(new Color(255, 255, 255, 200));
        backButton.setForeground(new Color(0, 102, 204));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        
        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Center window on screen
        setLocationRelativeTo(null);

        // Add listeners
        registerButton.addActionListener(e -> registerCustomer());
        backButton.addActionListener(e -> {
            new MainLauncher().setVisible(true);
            dispose();
        });
    }
    
    // Helper method to add form fields
    private void addFormField(JPanel panel, String label, JComponent field, GridBagConstraints gbc, int row) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelComponent.setForeground(Color.WHITE);
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.1;
        panel.add(labelComponent, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }
    
    private void registerCustomer() {
        // Get data from the text fields
        String name = nameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String phone = phoneField.getText();
        String address = addressField.getText();
        
        // Validate input
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Name, email, and password are required fields!", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Connect to the database and insert data
        try (Connection conn = DBConnection.getConnection()) {
            // SQL query to insert data into the Customers table
            String query = "INSERT INTO Customers (name, email, password, phone, address) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, phone);
            pstmt.setString(5, address);
            pstmt.executeUpdate(); // Execute the query
            
            JOptionPane.showMessageDialog(this, 
                "Registration successful! You can now login with your credentials.", 
                "Registration Complete", 
                JOptionPane.INFORMATION_MESSAGE); // Show success message
            
            // Go back to the login form
            new LoginForm().setVisible(true);
            dispose(); // Close the registration form
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + ex.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE); // Show error message
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new CustomerRegistration().setVisible(true);
        });
    }
} 