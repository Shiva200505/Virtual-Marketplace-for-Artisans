package AKT; // Replace with your package name

//package com.virtualmarketplace; // Replace with your package name (e.g., AKT)

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/VirtualMarketplace"; // Database URL
    private static final String USER = "root"; // MySQL username
    private static final String PASSWORD = "Shivam@2005"; // MySQL password

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load the MySQL JDBC driver
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found!", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD); // Return the database connection
    }
}