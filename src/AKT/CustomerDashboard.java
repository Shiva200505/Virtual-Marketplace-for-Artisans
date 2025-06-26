package AKT;	// Replace with your package name (e.g., AKT)

//package com.virtualmarketplace; // Replace with your package name (e.g., AKT)

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomerDashboard extends JFrame {
    private JButton browseProductsButton, logoutButton;

    public CustomerDashboard() {
        setTitle("Customer Dashboard");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        browseProductsButton = new JButton("Browse Products");
        logoutButton = new JButton("Logout");

        add(browseProductsButton);
        add(logoutButton);

        browseProductsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new BrowseProductsForm().setVisible(true); // Open Browse Products Form
                dispose(); // Close the dashboard
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginForm().setVisible(true); // Open Login Form
                dispose(); // Close the dashboard
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CustomerDashboard().setVisible(true);
        });
    }
}