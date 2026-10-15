@echo off
REM Compile script for Lost & Found Management System
REM Edit these paths according to your installation

SET JAVAFX_PATH=C:\javafx-sdk-11\lib
SET MYSQL_JAR=C:\mysql-connector-java-8.0.33.jar

echo ========================================
echo Compiling Lost & Found System...
echo ========================================

javac --module-path %JAVAFX_PATH% --add-modules javafx.controls -cp %MYSQL_JAR% *.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Compilation successful!
    echo ========================================
    echo.
    echo To run the application, execute: run.bat
) else (
    echo.
    echo ========================================
    echo Compilation failed! Please check errors above.
    echo ========================================
    echo.
    echo Common issues:
    echo - JavaFX SDK path incorrect
    echo - MySQL JAR path incorrect
    echo - Java version incompatible (need 11+)
)

pause
