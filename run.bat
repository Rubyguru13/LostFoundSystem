@echo off
REM Run script for Lost & Found Management System
REM Edit these paths according to your installation

SET JAVAFX_PATH=C:\javafx-sdk-11\lib
SET MYSQL_JAR=C:\mysql-connector-java-8.0.33.jar

echo ========================================
echo Starting Lost & Found System...
echo ========================================
echo.
echo Make sure MySQL server is running!
echo Database: lostfound_db
echo.

java --module-path %JAVAFX_PATH% --add-modules javafx.controls -cp ".;%MYSQL_JAR%" LostFoundSystemStudentUI

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ========================================
    echo Application exited with errors!
    echo ========================================
    echo.
    echo Common issues:
    echo - MySQL server not running
    echo - Database not created (run schema.sql first)
    echo - Database credentials incorrect in DatabaseManager.java
    echo - JavaFX runtime components missing
    pause
)
