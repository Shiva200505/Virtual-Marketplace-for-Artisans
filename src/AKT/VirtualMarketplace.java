package AKT;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;

public class VirtualMarketplace extends JFrame {
	private JTextField nameField, emailField, phoneField, addressField;
    private JPasswordField passwordField;
    private JButton registerButton;
    
    public VirtualMarketplace() {
        // Call the ArtisanRegistration method to set up the UI
        ArtisanRegistration();
    }

    public void ArtisanRegistration() {
        setTitle("Artisan Registration");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        nameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        phoneField = new JTextField(20);
        addressField = new JTextField(20);
        registerButton = new JButton("Register");

        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(new JLabel("Phone:"));
        add(phoneField);
        add(new JLabel("Address:"));
        add(addressField);
        add(registerButton);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerArtisan();
            }
        });
    }

    private void registerArtisan() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String phone = phoneField.getText();
        String address = addressField.getText();

        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO Artisans (name, email, password, phone, address) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, phone);
            pstmt.setString(5, address);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registration successful!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
            new VirtualMarketplace().setVisible(true);
        });
    }
}