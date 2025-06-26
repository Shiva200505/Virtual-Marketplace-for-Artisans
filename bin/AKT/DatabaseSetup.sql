-- Database setup script for Virtual Marketplace
-- This script creates all necessary tables for the Virtual Marketplace application

-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS VirtualMarketplace;

-- Use the database
USE VirtualMarketplace;

-- -----------------------------------------------------
-- Table for Admins
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Admins (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------
-- Table for Artisans
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Artisans (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  phone VARCHAR(20),
  address VARCHAR(255),
  approved BOOLEAN DEFAULT FALSE,
  bio TEXT,
  profile_image VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------
-- Table for Customers
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Customers (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  phone VARCHAR(20),
  address VARCHAR(255),
  profile_image VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------
-- Table for Products
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Products (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  category VARCHAR(50) NOT NULL,
  description TEXT,
  image VARCHAR(255),
  stock_quantity INT DEFAULT 0,
  artisan_id INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (artisan_id) REFERENCES Artisans(id) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Table for Product Ratings
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS ProductRatings (
  id INT AUTO_INCREMENT PRIMARY KEY,
  product_id INT NOT NULL,
  customer_id INT NOT NULL,
  rating INT NOT NULL,  -- 1 to 5 stars
  review TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
  FOREIGN KEY (customer_id) REFERENCES Customers(id) ON DELETE CASCADE,
  UNIQUE KEY unique_rating (product_id, customer_id)  -- One rating per product per customer
);

-- -----------------------------------------------------
-- Table for Orders
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Orders (
  id INT AUTO_INCREMENT PRIMARY KEY,
  customer_id INT NOT NULL,
  order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  status ENUM('Pending', 'Processing', 'Shipped', 'Delivered', 'Cancelled') DEFAULT 'Pending',
  total_amount DECIMAL(10,2) NOT NULL,
  shipping_address TEXT NOT NULL,
  payment_method VARCHAR(50),
  payment_status ENUM('Pending', 'Completed', 'Failed') DEFAULT 'Pending',
  FOREIGN KEY (customer_id) REFERENCES Customers(id) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Table for Order Items
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS OrderItems (
  id INT AUTO_INCREMENT PRIMARY KEY,
  order_id INT NOT NULL,
  product_id INT,  -- Changed from NOT NULL to allow NULL when product is deleted
  quantity INT NOT NULL,
  price DECIMAL(10,2) NOT NULL,  -- Price at the time of purchase
  FOREIGN KEY (order_id) REFERENCES Orders(id) ON DELETE CASCADE,
  FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE SET NULL
);

-- -----------------------------------------------------
-- Table for Customer Cart
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Cart (
  id INT AUTO_INCREMENT PRIMARY KEY,
  customer_id INT NOT NULL,
  product_id INT NOT NULL,
  quantity INT NOT NULL DEFAULT 1,
  added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (customer_id) REFERENCES Customers(id) ON DELETE CASCADE,
  FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
  UNIQUE KEY unique_cart_item (customer_id, product_id)  -- Prevent duplicate products in cart
);

-- -----------------------------------------------------
-- Table for Product Categories
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Categories (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE,
  description TEXT
);

-- -----------------------------------------------------
-- Insert default admin user
-- -----------------------------------------------------
INSERT INTO Admins (username, password, email, name)
VALUES ('admin', 'admin123', 'admin@virtualmarketplace.com', 'System Administrator')
ON DUPLICATE KEY UPDATE username = 'admin';

-- -----------------------------------------------------
-- Insert some sample categories
-- -----------------------------------------------------
INSERT INTO Categories (name, description) VALUES 
('Pottery', 'Handcrafted ceramic items like vases, bowls, and decorative pieces'),
('Textiles', 'Handwoven fabrics, clothing, and home decor'),
('Woodworking', 'Hand-carved wooden items and furniture'),
('Jewelry', 'Handcrafted jewelry and accessories'),
('Paper Crafts', 'Handmade paper products, cards, and stationery'),
('Glass Art', 'Blown glass items and stained glass')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- -----------------------------------------------------
-- Insert sample artisans (all pre-approved for testing)
-- -----------------------------------------------------
INSERT INTO Artisans (name, email, password, phone, address, approved, bio) VALUES
('John Smith', 'john@artisan.com', 'password123', '555-123-4567', '123 Craft St, Artville, AR 12345', TRUE, 'Master potter with 15 years of experience specializing in traditional techniques'),
('Maria Garcia', 'maria@artisan.com', 'password123', '555-234-5678', '456 Weaver Ln, Craftstown, CT 23456', TRUE, 'Textile artist creating handwoven pieces inspired by traditional patterns'),
('David Chen', 'david@artisan.com', 'password123', '555-345-6789', '789 Woodwork Ave, Craftville, CV 34567', TRUE, 'Woodworker specializing in handcrafted furniture using reclaimed materials'),
('Aisha Johnson', 'aisha@artisan.com', 'password123', '555-456-7890', '101 Jewel Rd, Gemtown, GT 45678', TRUE, 'Contemporary jewelry designer combining traditional and modern techniques')
ON DUPLICATE KEY UPDATE email = VALUES(email);

-- -----------------------------------------------------
-- Insert sample customers
-- -----------------------------------------------------
INSERT INTO Customers (name, email, password, phone, address) VALUES
('Emily Wilson', 'emily@customer.com', 'password123', '555-567-8901', '123 Home St, Hometown, HT 56789'),
('Robert Taylor', 'robert@customer.com', 'password123', '555-678-9012', '456 Main Ave, Cityville, CV 67890'),
('Jennifer Lee', 'jennifer@customer.com', 'password123', '555-789-0123', '789 Park Blvd, Townsburg, TB 78901'),
('Michael Brown', 'michael@customer.com', 'password123', '555-890-1234', '101 Oak Dr, Villagetown, VT 89012')
ON DUPLICATE KEY UPDATE email = VALUES(email);

-- -----------------------------------------------------
-- Insert sample products
-- -----------------------------------------------------
-- Pottery Products
INSERT INTO Products (name, price, category, description, image, stock_quantity, artisan_id) VALUES
('Handcrafted Ceramic Vase', 45.99, 'Pottery', 'Beautiful handcrafted vase with traditional glazing techniques. Perfect for fresh or dried flowers.', 'pottery_vase.jpg', 10, 1),
('Decorative Bowl Set', 65.50, 'Pottery', 'Set of 3 decorative bowls in varying sizes with a unique glaze finish. Great for serving or display.', 'pottery_bowl_set.jpg', 5, 1),
('Ceramic Plant Pot', 35.25, 'Pottery', 'Hand-thrown ceramic plant pot with drainage hole. Perfect for indoor plants.', 'pottery_plant_pot.jpg', 15, 1),
('Coffee Mug Set', 48.00, 'Pottery', 'Set of 4 handmade coffee mugs, each with a unique design. Microwave and dishwasher safe.', 'pottery_mug_set.jpg', 8, 1),
('Ceramic Dinner Plates', 95.00, 'Pottery', 'Set of 6 hand-glazed dinner plates with unique rustic finish. Each piece is one-of-a-kind.', 'pottery_plates.jpg', 3, 1);

-- Textile Products
INSERT INTO Products (name, price, category, description, image, stock_quantity, artisan_id) VALUES
('Handwoven Wall Hanging', 78.50, 'Textiles', 'Intricate handwoven wall hanging made with natural fibers and traditional techniques.', 'textile_wall_hanging.jpg', 4, 2),
('Hand-dyed Table Runner', 42.00, 'Textiles', 'Beautiful table runner hand-dyed using natural plant dyes with traditional patterns.', 'textile_table_runner.jpg', 7, 2),
('Woven Throw Blanket', 120.00, 'Textiles', 'Soft and warm throw blanket handwoven from 100% natural wool with traditional patterns.', 'textile_blanket.jpg', 6, 2),
('Embroidered Cushion Cover', 35.99, 'Textiles', 'Handmade cushion cover with intricate embroidery. Made with natural cotton fabric.', 'textile_cushion.jpg', 12, 2),
('Handcrafted Tapestry', 145.00, 'Textiles', 'Large decorative tapestry handwoven with intricate details and natural dyes.', 'textile_tapestry.jpg', 2, 2);

-- Woodworking Products
INSERT INTO Products (name, price, category, description, image, stock_quantity, artisan_id) VALUES
('Hand-carved Wooden Bowl', 55.00, 'Woodworking', 'Beautiful hand-carved wooden bowl made from sustainable maple wood. Perfect for fruit or decorative use.', 'wood_bowl.jpg', 8, 3),
('Wooden Cutting Board', 65.00, 'Woodworking', 'Handcrafted cutting board made from reclaimed walnut and maple. Each piece is unique.', 'wood_cutting_board.jpg', 10, 3),
('Wooden Jewelry Box', 85.50, 'Woodworking', 'Intricately carved wooden jewelry box with velvet lining. Perfect for storing precious items.', 'wood_jewelry_box.jpg', 5, 3),
('Handmade Wooden Spoons', 30.00, 'Woodworking', 'Set of 3 hand-carved wooden spoons made from cherry wood. Great for cooking and serving.', 'wood_spoons.jpg', 15, 3),
('Wooden Wall Art', 110.00, 'Woodworking', 'Geometric wooden wall art handcrafted from various types of reclaimed wood.', 'wood_wall_art.jpg', 3, 3);

-- Jewelry Products
INSERT INTO Products (name, price, category, description, image, stock_quantity, artisan_id) VALUES
('Handcrafted Silver Necklace', 95.00, 'Jewelry', 'Beautiful sterling silver necklace with a unique pendant design. Each piece is handcrafted.', 'jewelry_silver_necklace.jpg', 7, 4),
('Beaded Bracelet Set', 45.00, 'Jewelry', 'Set of 3 handmade beaded bracelets made with semi-precious stones and sterling silver clasps.', 'jewelry_bracelet_set.jpg', 12, 4),
('Copper Earrings', 35.50, 'Jewelry', 'Handcrafted copper earrings with a hammered finish. Lightweight and perfect for everyday wear.', 'jewelry_copper_earrings.jpg', 15, 4),
('Mixed Metal Ring', 60.00, 'Jewelry', 'Handcrafted ring made with a combination of sterling silver and brass. Adjustable size.', 'jewelry_mixed_metal_ring.jpg', 8, 4),
('Gemstone Pendant', 75.00, 'Jewelry', 'Beautiful handmade pendant featuring a natural gemstone set in sterling silver.', 'jewelry_gemstone_pendant.jpg', 10, 4);

-- -----------------------------------------------------
-- Insert sample product ratings
-- -----------------------------------------------------
INSERT INTO ProductRatings (product_id, customer_id, rating, review) VALUES
(1, 1, 5, 'This vase is absolutely beautiful! The craftsmanship is exceptional.'),
(2, 1, 4, 'Love the bowl set, each one is unique.'),
(3, 2, 5, 'Perfect pot for my houseplants, great quality.'),
(5, 3, 4, 'The plates are beautiful, though slightly different sizes.'),
(6, 1, 5, 'Stunning wall hanging, adds so much character to my room.'),
(7, 2, 4, 'Love the natural dyes used in this runner.'),
(8, 4, 5, 'This blanket is so cozy and beautifully made.'),
(11, 1, 5, 'The wooden bowl is beautiful and well-crafted.'),
(13, 3, 5, 'This jewelry box is even more beautiful in person.'),
(16, 2, 4, 'Love this necklace, wear it almost every day.'),
(17, 4, 5, 'The bracelet set is gorgeous, get compliments every time I wear them.');

-- -----------------------------------------------------
-- Insert sample orders
-- -----------------------------------------------------
INSERT INTO Orders (customer_id, status, total_amount, shipping_address, payment_method, payment_status) VALUES
(1, 'Delivered', 160.49, '123 Home St, Hometown, HT 56789', 'Credit Card', 'Completed'),
(2, 'Shipped', 215.00, '456 Main Ave, Cityville, CV 67890', 'PayPal', 'Completed'),
(3, 'Processing', 95.00, '789 Park Blvd, Townsburg, TB 78901', 'Credit Card', 'Completed'),
(4, 'Delivered', 165.00, '101 Oak Dr, Villagetown, VT 89012', 'PayPal', 'Completed'),
(1, 'Pending', 120.00, '123 Home St, Hometown, HT 56789', 'Credit Card', 'Pending');

-- -----------------------------------------------------
-- Insert sample order items
-- -----------------------------------------------------
INSERT INTO OrderItems (order_id, product_id, quantity, price) VALUES
(1, 1, 1, 45.99),
(1, 3, 1, 35.25),
(1, 4, 1, 48.00),
(1, 17, 1, 45.00),
(2, 8, 1, 120.00),
(2, 11, 1, 55.00),
(2, 16, 1, 95.00),
(3, 5, 1, 95.00),
(4, 11, 1, 55.00),
(4, 13, 1, 85.50),
(4, 12, 1, 65.00),
(5, 8, 1, 120.00); 