# Virtual Marketplace for Artisans

A Java Swing application that provides a platform for artisans to sell their handcrafted products and for customers to discover and purchase unique items.

## Project Screenshots

| Main Launcher | Admin Dashboard | Artisan Dashboard |
|:------------:|:--------------:|:----------------:|
| ![Main Launcher](resources/images/screenshot_main_launcher.png) | ![Admin Dashboard](resources/images/screenshot_admin_dashboard.png) | ![Artisan Dashboard](resources/images/screenshot_artisan_dashboard.png) |

| Customer Dashboard | Product Details | Shopping Cart |
|:------------------:|:--------------:|:-------------:|
| ![Customer Dashboard](resources/images/screenshot_customer_dashboard.png) | ![Product Details](resources/images/screenshot_product_details.png) | ![Shopping Cart](resources/images/screenshot_shopping_cart.png) |

## Features

### Customer Module
- Registration and login/logout functionality
- Enhanced dashboard with product recommendations
- Browse products by category
- View product details and make purchases
- Manage shopping cart
- View purchase history

### Artisan Module
- Registration and login/logout functionality (requires admin approval)
- Add and manage products
- View customer purchase history for their products
- Dashboard with sales statistics

### Admin Module
- Approve or reject artisan accounts
- Manage all users (customers and artisans)
- View and manage products
- System statistics dashboard

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- MySQL Database Server
- JDBC Connector for MySQL

### Database Setup
1. Install MySQL if you don't have it already
2. Create a database named `VirtualMarketplace`
3. Run the SQL script in `src/AKT/DatabaseSetup.sql` to set up the database schema

```bash
mysql -u root -p < src/AKT/DatabaseSetup.sql
```

### Configuration
1. Open `src/AKT/DBConnection.java`
2. Update the database connection settings if needed:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/VirtualMarketplace";
   private static final String USER = "root"; // Change to your MySQL username
   private static final String PASSWORD = "yourpassword"; // Change to your MySQL password
   ```

### Compiling and Running
1. Compile the Java files
   ```bash
   javac -d bin src/AKT/*.java
   ```
2. Run the application
   ```bash
   java -cp bin:mysql-connector-java.jar AKT.MainLauncher
   ```

## Usage

### Admin Login
- Username: admin
- Password: admin123

### Artisan Registration & Login
1. Register as a new artisan through the main launcher
2. Login as an admin to approve the artisan account
3. After approval, the artisan can login and start adding products

### Customer Registration & Login
1. Register as a new customer through the main launcher
2. Login with your credentials
3. Browse and purchase products

## Recommendation System

The system includes a basic recommendation engine that suggests products to customers based on:
- Their past purchases
- Products they have viewed or rated
- Popular products in categories they've shown interest in

In a production environment, this recommendation system could be enhanced with more sophisticated machine learning algorithms.

## Security Note

This application uses plaintext password storage for demonstration purposes only. In a production environment, passwords should be hashed using a secure algorithm (e.g., bcrypt) and proper security measures should be implemented.

## License

This project is licensed under the MIT License - see the LICENSE file for details.