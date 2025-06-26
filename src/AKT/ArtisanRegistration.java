package AKT; // Replace with your package name

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class ArtisanRegistration extends JFrame {
    // Declare text fields and buttons
    private JTextField nameField, emailField, phoneField, addressField;
    private JPasswordField passwordField;
    private JButton registerButton, backButton;

    // Constructor for the registration form
    public ArtisanRegistration() {
        // Set the title of the window
        setTitle("Artisan Registration");
        // Set the size of the window (width, height)
        setSize(400, 350);
        // Close the application when the window is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Use BoxLayout to arrange components vertically
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // Initialize text fields and buttons
        nameField = new JTextField(20); // Text field for name
        emailField = new JTextField(20); // Text field for email
        passwordField = new JPasswordField(20); // Password field for password
        phoneField = new JTextField(20); // Text field for phone
        addressField = new JTextField(20); // Text field for address
        registerButton = new JButton("Register"); // Button to register
        backButton = new JButton("Back to Main Menu"); // Button to go back

        // Add components to the window
        add(new JLabel("Name:")); // Label for name
        add(nameField); // Text field for name
        add(new JLabel("Email:")); // Label for email
        add(emailField); // Text field for email
        add(new JLabel("Password:")); // Label for password
        add(passwordField); // Password field for password
        add(new JLabel("Phone:")); // Label for phone
        add(phoneField); // Text field for phone
        add(new JLabel("Address:")); // Label for address
        add(addressField); // Text field for address
        
        // Create a panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        add(buttonPanel);
        
        // Center the window on screen
        setLocationRelativeTo(null);

        // Add an action listener to the register button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerArtisan(); // Call the registerArtisan method when the button is clicked
            }
        });
        
        // Add an action listener to the back button
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Go back to the main launcher
                new MainLauncher().setVisible(true);
                dispose(); // Close the current window
            }
        });
    }

    // Method to handle artisan registration
    private void registerArtisan() {
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
            // SQL query to insert data into the Artisans table
            String query = "INSERT INTO Artisans (name, email, password, phone, address) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, phone);
            pstmt.setString(5, address);
            pstmt.executeUpdate(); // Execute the query
            JOptionPane.showMessageDialog(this, 
                "Registration successful! Your account will be reviewed by an admin.", 
                "Registration Complete", 
                JOptionPane.INFORMATION_MESSAGE); // Show success message
            
            // Go back to the login form
            new LoginForm().setVisible(true);
            dispose(); // Close the registration form
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE); // Show error message
        }
    }

    // Main method to run the application
    public static void main(String[] args) {
        // Use SwingUtilities to create and show the GUI
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new ArtisanRegistration().setVisible(true); // Make the window visible
        });
    }
}