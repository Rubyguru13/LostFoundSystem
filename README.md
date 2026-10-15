# 🔍 Lost & Found Management System

[![Java](https://img.shields.io/badge/Java-11%2B-orange.svg)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/JavaFX-11%2B-blue.svg)](https://openjfx.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0%2B-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

> A comprehensive JavaFX-based Lost & Found Management System designed for educational institutions. Features a modern, student-friendly blue theme with user authentication, item reporting, smart matching algorithm, and real-time department chat.

## 📸 Screenshots

*Coming soon - Add your screenshots after running the application!*

## 🌟 Key Features

### 🔐 User Management
- **Registration & Login**: Secure authentication with SHA-256 password hashing
- **Role-based Access**: Support for student, staff, and admin roles
- **Department Assignment**: Users belong to specific departments (CSE, AIML, DS, CS, IOT, ECE, EEE, MECH, CIVIL, MBA)

### 📝 Item Management
- **Report Lost Items**: Users can report items they've lost with detailed information
- **Report Found Items**: Users can report items they've found
- **Rich Details**: Title, description, category, location, department, and timestamp
- **Search & Filter**: Search by keywords, filter by category and department

### ✨ Smart Matching Algorithm
- **Fuzzy Matching**: Case-insensitive matching between lost and found items
- **Multi-criteria**: Matches based on title similarity, category, and location
- **Highlighted Matches**: Probable matches are highlighted for easy identification
- **Contact Information**: Direct access to reporter contact details

### 💬 Department Chat
- **Channel-based**: Separate chat channels for each department
- **Real-time Updates**: Messages refresh every 3 seconds (DB polling)
- **User-friendly**: Simple send/receive interface with timestamps

### 🎨 Modern UI
- **Student-friendly Blue Theme**: Clean, professional appearance
- **Responsive Layout**: Adaptive design for different screen sizes
- **Inline Validation**: Real-time form validation with helpful error messages
- **Confirmation Dialogs**: Safe operations with user confirmation
- **Tabbed Interface**: Easy navigation between features

## Technology Stack

- **Frontend**: JavaFX (Java 11+)
- **Backend**: JDBC with MySQL
- **Database**: MySQL 8.0+
- **Architecture**: Multi-file modular design

## Project Structure

```
LostFoundSystem/
├── User.java                      # User model class
├── Item.java                      # Item model class
├── Message.java                   # Message model class
├── DatabaseManager.java           # Database operations with JDBC
├── LostFoundSystemStudentUI.java  # Main JavaFX application
├── schema.sql                     # MySQL database schema
└── README.md                      # This file
```

## Prerequisites

1. **Java Development Kit (JDK) 11 or higher**
   - Download from: https://www.oracle.com/java/technologies/downloads/
   - Verify: `java -version`

2. **JavaFX SDK 11 or higher**
   - Download from: https://openjfx.io/
   - Extract to a known location (e.g., `C:\javafx-sdk-11`)

3. **MySQL Server 8.0+**
   - Download from: https://dev.mysql.com/downloads/mysql/
   - Install and start the MySQL service

4. **MySQL JDBC Driver (Connector/J)**
   - Download from: https://dev.mysql.com/downloads/connector/j/
   - Extract the JAR file (e.g., `mysql-connector-java-8.0.33.jar`)

## Setup Instructions

### Step 1: Database Setup

1. Start MySQL Server
2. Open MySQL Command Line or MySQL Workbench
3. Run the schema file:

```sql
mysql -u root -p < schema.sql
```

Or manually execute the schema:

```sql
CREATE DATABASE IF NOT EXISTS lostfound_db;
USE lostfound_db;

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

CREATE TABLE messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    department VARCHAR(50),
    content TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### Step 2: Configure Database Connection

Edit `DatabaseManager.java` and update the database credentials:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/lostfound_db?useSSL=false&serverTimezone=UTC";
private static final String DB_USER = "root";
private static final String DB_PASS = "your_mysql_password";  // Change this!
```

### Step 3: Compile the Application

#### Windows (PowerShell):

```powershell
# Set paths (adjust to your installation)
$JAVAFX_PATH = "C:\javafx-sdk-11\lib"
$MYSQL_JAR = "C:\mysql-connector-java-8.0.33.jar"

# Navigate to project directory
cd C:\Users\v.sujay\CascadeProjects\LostFoundSystem

# Compile all Java files
javac --module-path $JAVAFX_PATH --add-modules javafx.controls -cp $MYSQL_JAR *.java
```

#### Linux/Mac:

```bash
# Set paths (adjust to your installation)
JAVAFX_PATH="/path/to/javafx-sdk-11/lib"
MYSQL_JAR="/path/to/mysql-connector-java-8.0.33.jar"

# Navigate to project directory
cd /path/to/LostFoundSystem

# Compile all Java files
javac --module-path $JAVAFX_PATH --add-modules javafx.controls -cp $MYSQL_JAR *.java
```

### Step 4: Run the Application

#### Windows (PowerShell):

```powershell
$JAVAFX_PATH = "C:\javafx-sdk-11\lib"
$MYSQL_JAR = "C:\mysql-connector-java-8.0.33.jar"

java --module-path $JAVAFX_PATH --add-modules javafx.controls -cp ".;$MYSQL_JAR" LostFoundSystemStudentUI
```

#### Linux/Mac:

```bash
JAVAFX_PATH="/path/to/javafx-sdk-11/lib"
MYSQL_JAR="/path/to/mysql-connector-java-8.0.33.jar"

java --module-path $JAVAFX_PATH --add-modules javafx.controls -cp ".:$MYSQL_JAR" LostFoundSystemStudentUI
```

## Quick Start Guide

### 1. First Launch
- The login screen will appear
- Click "Don't have an account? Register here"

### 2. Register a New Account
- Fill in your details:
  - Full Name
  - Email Address (must be unique)
  - Phone Number (optional)
  - Password (minimum 6 characters)
  - Confirm Password
  - Select Role (student/staff/admin)
  - Select Department
- Click "Register"
- You'll be redirected to login

### 3. Login
- Enter your email and password
- Click "Login"

### 4. Use the System

#### Report a Lost Item
1. Go to "📝 Report Item" tab
2. Select "Lost" radio button
3. Fill in item details:
   - Title (e.g., "Blue Backpack")
   - Description (detailed information)
   - Category (Electronics, Books, etc.)
   - Location (where you lost it)
   - Department
4. Click "Submit Report"

#### Report a Found Item
1. Go to "📝 Report Item" tab
2. Select "Found" radio button
3. Fill in item details
4. Click "Submit Report"

#### View Lost/Found Items
1. Go to "🔎 Lost Items" or "✅ Found Items" tab
2. Use search box to find specific items
3. Filter by Category and Department
4. Click on an item to view full details including reporter contact info

#### Find Matches
1. Go to "✨ Find Matches" tab
2. Select "My Lost Items" or "My Found Items"
3. Select an item from your list
4. Click "Find Matches"
5. The system will show probable matches with highlighted borders
6. Click on a match to view contact details

#### Department Chat
1. Go to "💬 Department Chat" tab
2. Select your department channel
3. Type a message and click "Send" (or press Enter)
4. Messages auto-refresh every 3 seconds
5. Chat with other users in your department about lost/found items

## Troubleshooting

### Database Connection Error
**Problem**: "Failed to connect to database"

**Solutions**:
- Ensure MySQL server is running
- Verify database credentials in `DatabaseManager.java`
- Check that `lostfound_db` database exists
- Ensure MySQL JDBC driver is in classpath

### JavaFX Module Error
**Problem**: "Error: JavaFX runtime components are missing"

**Solutions**:
- Ensure JavaFX SDK is downloaded
- Verify `--module-path` points to JavaFX lib folder
- Check `--add-modules javafx.controls` is included

### Compilation Error
**Problem**: "Class not found" or "Package does not exist"

**Solutions**:
- Ensure all `.java` files are in the same directory
- Verify MySQL JDBC driver JAR is in classpath with `-cp` flag
- Check Java version (must be 11+)

### Chat Not Updating
**Problem**: Messages don't refresh automatically

**Solutions**:
- Chat polling uses Timeline animation (3-second interval)
- Check if you're on the Chat tab (polling only runs when tab is active)
- Verify database connection is stable

## Security Features

- **Password Hashing**: SHA-256 algorithm for secure password storage
- **Parameterized Queries**: All SQL uses prepared statements to prevent SQL injection
- **Role-based Access**: Different permission levels (student, staff, admin)
- **Input Validation**: Client-side validation for all forms

## Database Schema

### Users Table
- `id`: Primary key (auto-increment)
- `name`: User's full name
- `email`: Unique email address (login credential)
- `phone`: Contact phone number
- `password_hash`: SHA-256 hashed password
- `role`: User role (student/staff/admin)
- `department`: User's department
- `created_at`: Registration timestamp

### Items Table
- `id`: Primary key (auto-increment)
- `user_id`: Foreign key to users table
- `kind`: Item type (lost/found)
- `title`: Item title/name
- `description`: Detailed description
- `category`: Item category
- `location`: Where item was lost/found
- `department`: Related department
- `reported_at`: Report timestamp

### Messages Table
- `id`: Primary key (auto-increment)
- `sender_id`: Foreign key to users table
- `department`: Chat channel/department
- `content`: Message text
- `sent_at`: Message timestamp

## Matching Algorithm

The system uses a fuzzy matching algorithm with scoring:

1. **Title Similarity** (3 points)
   - Exact substring match (case-insensitive)
   - Word-level matching for common terms

2. **Category Match** (2 points)
   - Exact category match

3. **Location Similarity** (1-2 points)
   - Full location match (2 points)
   - Partial word match (1 point)

**Threshold**: Items with score ≥ 3 are considered probable matches

## Future Enhancements

- Image upload for items
- Email notifications for matches
- Admin dashboard with statistics
- Item resolution/closure workflow
- WebSocket-based real-time chat
- Mobile responsive design
- Export reports to PDF

## License

This project is created for educational purposes.

## Support

For issues or questions:
1. Check the Troubleshooting section
2. Verify all prerequisites are installed
3. Ensure database is properly configured
4. Check MySQL and Java logs for errors

## Version

**Version**: 1.0.0  
**Last Updated**: October 2025  
**Java Version**: 11+  
**JavaFX Version**: 11+  
**MySQL Version**: 8.0+


## Source attribution

This repository imports the LostFoundSystem project from [sujay119/LostFoundSystem](https://github.com/sujay119/LostFoundSystem) as a portfolio starting point. The imported application code is unchanged. The original MIT copyright and license are preserved in [LICENSE-UPSTREAM](LICENSE-UPSTREAM).

The proposed image uploads, hidden item details, and security-question claim verification are not implemented in this imported version.
