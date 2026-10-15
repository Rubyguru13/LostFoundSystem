-- Lost & Found Management System - Database Schema
-- MySQL Database

CREATE DATABASE IF NOT EXISTS lostfound_db;
USE lostfound_db;

-- Users table: stores student, staff, and admin information
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) UNIQUE NOT NULL,
    phone VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('student','staff','admin') DEFAULT 'student',
    department VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Items table: stores lost and found items
CREATE TABLE items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    kind ENUM('lost','found') NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    category VARCHAR(80),
    location VARCHAR(120),
    department VARCHAR(50),
    reported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Messages table: stores chat messages by department channels
CREATE TABLE messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    department VARCHAR(50),
    content TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_items_kind ON items(kind);
CREATE INDEX idx_items_category ON items(category);
CREATE INDEX idx_items_department ON items(department);
CREATE INDEX idx_messages_department ON messages(department);
CREATE INDEX idx_messages_sent_at ON messages(sent_at);
