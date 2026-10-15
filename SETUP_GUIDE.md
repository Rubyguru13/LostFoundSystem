# 🚀 Quick Setup Guide

## Prerequisites Checklist

Before you begin, ensure you have:

- [ ] **Java JDK 11 or higher** installed
- [ ] **JavaFX SDK 11 or higher** downloaded
- [ ] **MySQL Server 8.0+** installed and running
- [ ] **MySQL Connector/J** (JDBC driver) downloaded

---

## Step-by-Step Setup

### 1️⃣ Install Java JDK

**Download**: https://www.oracle.com/java/technologies/downloads/

**Verify Installation**:
```bash
java -version
javac -version
```

You should see version 11 or higher.

---

### 2️⃣ Download JavaFX SDK

**Download**: https://openjfx.io/

1. Download JavaFX SDK for your OS
2. Extract to a permanent location (e.g., `C:\javafx-sdk-11` on Windows)
3. Note the path to the `lib` folder

---

### 3️⃣ Install MySQL Server

**Download**: https://dev.mysql.com/downloads/mysql/

1. Install MySQL Server
2. Set root password during installation
3. Start MySQL service
4. Verify it's running

**Verify**:
```bash
mysql --version
```

---

### 4️⃣ Download MySQL Connector/J

**Download**: https://dev.mysql.com/downloads/connector/j/

1. Download the JAR file (e.g., `mysql-connector-java-8.0.33.jar`)
2. Extract it to a known location
3. Note the full path to the JAR file

---

### 5️⃣ Setup Database

**Option A: Using MySQL Command Line**
```bash
mysql -u root -p < schema.sql
```

**Option B: Using MySQL Workbench**
1. Open MySQL Workbench
2. Connect to your MySQL server
3. File → Open SQL Script → Select `schema.sql`
4. Execute the script

**Verify Database**:
```sql
mysql -u root -p
USE lostfound_db;
SHOW TABLES;
```

You should see: `users`, `items`, `messages`

---

### 6️⃣ Configure Database Connection

Open `DatabaseManager.java` and update lines 13-15:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/lostfound_db?useSSL=false&serverTimezone=UTC";
private static final String DB_USER = "root";
private static final String DB_PASS = "your_mysql_password";  // ⚠️ CHANGE THIS!
```

**Important**: Replace `your_mysql_password` with your actual MySQL root password.

---

### 7️⃣ Update Compilation Scripts (Windows Only)

Edit `compile.bat` and `run.bat`:

```batch
SET JAVAFX_PATH=C:\javafx-sdk-11\lib
SET MYSQL_JAR=C:\mysql-connector-java-8.0.33.jar
```

Replace with your actual paths.

---

### 8️⃣ Compile the Application

#### Windows:
```bash
# Method 1: Using batch script
compile.bat

# Method 2: Manual compilation
javac --module-path C:\javafx-sdk-11\lib --add-modules javafx.controls -cp C:\mysql-connector-java-8.0.33.jar *.java
```

#### Linux/Mac:
```bash
JAVAFX_PATH="/path/to/javafx-sdk-11/lib"
MYSQL_JAR="/path/to/mysql-connector-java-8.0.33.jar"

javac --module-path $JAVAFX_PATH --add-modules javafx.controls -cp $MYSQL_JAR *.java
```

---

### 9️⃣ Run the Application

#### Windows:
```bash
# Method 1: Using batch script
run.bat

# Method 2: Manual execution
java --module-path C:\javafx-sdk-11\lib --add-modules javafx.controls -cp ".;C:\mysql-connector-java-8.0.33.jar" LostFoundSystemStudentUI
```

#### Linux/Mac:
```bash
JAVAFX_PATH="/path/to/javafx-sdk-11/lib"
MYSQL_JAR="/path/to/mysql-connector-java-8.0.33.jar"

java --module-path $JAVAFX_PATH --add-modules javafx.controls -cp ".:$MYSQL_JAR" LostFoundSystemStudentUI
```

---

### 🔟 First Time Use

1. **Launch the application** - The login screen will appear
2. **Click "Register here"** link
3. **Fill in registration form**:
   - Full Name
   - Email (must be unique)
   - Phone (optional)
   - Password (min 6 characters)
   - Confirm Password
   - Select Role (student/staff/admin)
   - Select Department
4. **Click "Register"**
5. **Login** with your credentials
6. **Explore the features**!

---

## 🐛 Troubleshooting

### "Failed to connect to database"
- ✅ Check MySQL is running: `mysql -V`
- ✅ Verify database exists: `USE lostfound_db;`
- ✅ Check credentials in `DatabaseManager.java`
- ✅ Ensure MySQL JDBC driver is in classpath

### "JavaFX runtime components are missing"
- ✅ Verify `--module-path` points to JavaFX `lib` folder
- ✅ Ensure `--add-modules javafx.controls` is included
- ✅ Check JavaFX SDK version matches Java version

### "Class not found" errors
- ✅ Ensure all `.java` files are in same directory
- ✅ Verify MySQL JDBC JAR is in classpath with `-cp` flag
- ✅ Check Java version is 11 or higher

### Compilation errors
- ✅ Check Java version: `java -version`
- ✅ Verify all paths in compile command are correct
- ✅ Ensure no spaces in directory names (or use quotes)

### Application won't start
- ✅ Run from correct directory containing `.class` files
- ✅ Check console for error messages
- ✅ Verify all dependencies are in classpath

---

## 📞 Need Help?

1. Check the [README.md](README.md) for detailed documentation
2. Review [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines
3. Create an issue on GitHub with your problem

---

## ✅ Verification Checklist

After setup, verify everything works:

- [ ] Application launches without errors
- [ ] Login screen appears with blue theme
- [ ] Can register a new account
- [ ] Can login with registered account
- [ ] Can report lost items
- [ ] Can report found items
- [ ] Can search and filter items
- [ ] Can view item details
- [ ] Find matches feature works
- [ ] Department chat works
- [ ] Messages send and receive

---

## 🎉 You're All Set!

Congratulations! Your Lost & Found Management System is ready to use.

**Next Steps**:
- Star ⭐ the repository if you find it helpful
- Share with your institution
- Contribute improvements
- Report bugs or suggest features

Happy tracking! 🔍✨
