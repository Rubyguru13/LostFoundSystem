import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.collections.*;
import java.sql.SQLException;
import java.util.*;
import java.text.SimpleDateFormat;
import javafx.animation.*;
import javafx.util.Duration;

/**
 * Lost & Found Management System - Student UI
 * A comprehensive JavaFX application for managing lost and found items
 * Features: User authentication, item reporting, search/filter, matching algorithm, department chat
 */
public class LostFoundSystemStudentUI extends Application {
    
    // Database manager
    private DatabaseManager dbManager;
    
    // Current logged-in user
    private User currentUser;
    
    // Primary stage
    private Stage primaryStage;
    
    // Constants
    private static final String[] CATEGORIES = {"All Categories", "Electronics", "Books", "Clothing", "Accessories", "Documents", "Keys", "Bags", "Other"};
    private static final String[] DEPARTMENTS = {"All Departments", "CSE", "AIML", "DS", "CS", "IOT", "ECE", "EEE", "MECH", "CIVIL", "MBA"};
    
    // Timeline for chat polling
    private Timeline chatPollingTimeline;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Lost & Found - Student Management System");
        
        // Initialize database connection
        dbManager = new DatabaseManager();
        try {
            dbManager.connect();
            showLoginScreen();
        } catch (SQLException e) {
            showError("Database Connection Error", 
                     "Failed to connect to database. Please check:\n" +
                     "1. MySQL is running\n" +
                     "2. Database 'lostfound_db' exists (run schema.sql)\n" +
                     "3. DB credentials in DatabaseManager.java are correct\n\n" +
                     "Error: " + e.getMessage());
            Platform.exit();
        }
    }
    
    @Override
    public void stop() {
        if (chatPollingTimeline != null) {
            chatPollingTimeline.stop();
        }
        if (dbManager != null) {
            dbManager.disconnect();
        }
    }
    
    /**
     * Show login screen
     */
    private void showLoginScreen() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #E3F2FD, #BBDEFB);");
        
        // Title
        Label titleLabel = new Label("🔍 Lost & Found System");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #0D47A1;");
        
        Label subtitleLabel = new Label("Student Portal");
        subtitleLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #1565C0;");
        
        // Login form
        VBox formBox = new VBox(15);
        formBox.setMaxWidth(400);
        formBox.setAlignment(Pos.CENTER);
        formBox.setStyle("-fx-background-color: rgba(255,255,255,0.9); " +
                        "-fx-padding: 30; " +
                        "-fx-background-radius: 15; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");
        
        Label formTitle = createLabel("Login to Your Account", 20, true);
        
        TextField emailField = createTextField("Email Address");
        emailField.setPrefWidth(350);
        
        PasswordField passwordField = createPasswordField("Password");
        passwordField.setPrefWidth(350);
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 12px;");
        errorLabel.setVisible(false);
        
        Button loginBtn = createButton("Login", "#2196F3");
        loginBtn.setPrefWidth(350);
        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            
            if (email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please fill in all fields");
                errorLabel.setVisible(true);
                return;
            }
            
            try {
                User user = dbManager.loginUser(email, password);
                if (user != null) {
                    currentUser = user;
                    showDashboard();
                } else {
                    errorLabel.setText("Invalid email or password");
                    errorLabel.setVisible(true);
                }
            } catch (SQLException ex) {
                showError("Login Error", "Database error: " + ex.getMessage());
            }
        });
        
        Hyperlink registerLink = new Hyperlink("Don't have an account? Register here");
        registerLink.setStyle("-fx-text-fill: #1976D2;");
        registerLink.setOnAction(e -> showRegisterScreen());
        
        formBox.getChildren().addAll(formTitle, emailField, passwordField, errorLabel, loginBtn, registerLink);
        root.getChildren().addAll(titleLabel, subtitleLabel, new Label(), formBox);
        
        Scene scene = new Scene(root, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Show registration screen
     */
    private void showRegisterScreen() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #E3F2FD, #BBDEFB);");
        
        Label titleLabel = new Label("Create New Account");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #0D47A1;");
        
        // Registration form
        VBox formBox = new VBox(12);
        formBox.setMaxWidth(450);
        formBox.setAlignment(Pos.CENTER);
        formBox.setStyle("-fx-background-color: rgba(255,255,255,0.9); " +
                        "-fx-padding: 30; " +
                        "-fx-background-radius: 15; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");
        
        TextField nameField = createTextField("Full Name");
        TextField emailField = createTextField("Email Address");
        TextField phoneField = createTextField("Phone Number");
        PasswordField passwordField = createPasswordField("Password");
        PasswordField confirmPasswordField = createPasswordField("Confirm Password");
        
        ComboBox<String> roleCombo = createComboBox(new String[]{"student", "staff", "admin"});
        roleCombo.setValue("student");
        roleCombo.setPrefWidth(350);
        
        String[] depts = Arrays.copyOfRange(DEPARTMENTS, 1, DEPARTMENTS.length);
        ComboBox<String> deptCombo = createComboBox(depts);
        deptCombo.setValue("CSE");
        deptCombo.setPrefWidth(350);
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 12px;");
        errorLabel.setVisible(false);
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button registerBtn = createButton("Register", "#4CAF50");
        registerBtn.setPrefWidth(170);
        registerBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            String role = roleCombo.getValue();
            String department = deptCombo.getValue();
            
            // Validation
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please fill in all required fields");
                errorLabel.setVisible(true);
                return;
            }
            
            if (!email.contains("@")) {
                errorLabel.setText("Please enter a valid email address");
                errorLabel.setVisible(true);
                return;
            }
            
            if (password.length() < 6) {
                errorLabel.setText("Password must be at least 6 characters");
                errorLabel.setVisible(true);
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                errorLabel.setText("Passwords do not match");
                errorLabel.setVisible(true);
                return;
            }
            
            try {
                boolean success = dbManager.registerUser(name, email, phone, password, role, department);
                if (success) {
                    showInfo("Success", "Account created successfully! Please login.");
                    showLoginScreen();
                } else {
                    errorLabel.setText("Email already registered");
                    errorLabel.setVisible(true);
                }
            } catch (SQLException ex) {
                showError("Registration Error", "Database error: " + ex.getMessage());
            }
        });
        
        Button backBtn = createButton("Back to Login", "#757575");
        backBtn.setPrefWidth(170);
        backBtn.setOnAction(e -> showLoginScreen());
        
        buttonBox.getChildren().addAll(registerBtn, backBtn);
        
        formBox.getChildren().addAll(
            createLabel("Personal Information", 16, true),
            nameField, emailField, phoneField, passwordField, confirmPasswordField,
            createLabel("Role & Department", 16, true),
            roleCombo, deptCombo,
            errorLabel, buttonBox
        );
        
        root.getChildren().addAll(titleLabel, formBox);
        
        Scene scene = new Scene(root, 900, 700);
        primaryStage.setScene(scene);
    }
    
    /**
     * Show main dashboard with tabs
     */
    private void showDashboard() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #E3F2FD, #BBDEFB);");
        
        // Top bar with user info
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #1976D2; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);");
        
        Label welcomeLabel = new Label("Welcome, " + currentUser.getName() + " (" + currentUser.getRole().toUpperCase() + ")");
        welcomeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 8 15 8 15; -fx-background-radius: 5;");
        logoutBtn.setOnAction(e -> {
            if (showConfirm("Logout", "Are you sure you want to logout?")) {
                if (chatPollingTimeline != null) {
                    chatPollingTimeline.stop();
                }
                currentUser = null;
                showLoginScreen();
            }
        });
        
        topBar.getChildren().addAll(welcomeLabel, spacer, logoutBtn);
        root.setTop(topBar);
        
        // Tab pane
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: transparent;");
        
        // Create tabs
        Tab lostTab = new Tab("🔎 Lost Items", createLostItemsTab());
        Tab foundTab = new Tab("✅ Found Items", createFoundItemsTab());
        Tab reportTab = new Tab("📝 Report Item", createReportItemTab());
        Tab chatTab = new Tab("💬 Department Chat", createChatTab());
        Tab matchesTab = new Tab("✨ Find Matches", createMatchesTab());
        
        tabPane.getTabs().addAll(lostTab, foundTab, reportTab, matchesTab, chatTab);
        
        root.setCenter(tabPane);
        
        Scene scene = new Scene(root, 1100, 750);
        primaryStage.setScene(scene);
    }
    
    /**
     * Get embedded CSS for student-friendly blue theme
     */
    private String getCSS() {
        return "-fx-base: #E3F2FD; " +
               "-fx-background: linear-gradient(to bottom, #E3F2FD, #BBDEFB); " +
               "-fx-accent: #2196F3; ";
    }
    
    /**
     * Show error alert
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Show info alert
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Show confirmation dialog
     */
    private boolean showConfirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    /**
     * Create styled button
     */
    private Button createButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; " +
                     "-fx-text-fill: white; " +
                     "-fx-font-size: 14px; " +
                     "-fx-padding: 10 20 10 20; " +
                     "-fx-background-radius: 5; " +
                     "-fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setOpacity(0.8));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
        return btn;
    }
    
    /**
     * Create styled label
     */
    private Label createLabel(String text, int fontSize, boolean bold) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: " + fontSize + "px; " +
                      (bold ? "-fx-font-weight: bold; " : "") +
                      "-fx-text-fill: #1565C0;");
        return label;
    }
    
    /**
     * Create styled text field
     */
    private TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle("-fx-background-color: white; " +
                      "-fx-border-color: #2196F3; " +
                      "-fx-border-radius: 5; " +
                      "-fx-background-radius: 5; " +
                      "-fx-padding: 8;");
        return field;
    }
    
    /**
     * Create styled password field
     */
    private PasswordField createPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setStyle("-fx-background-color: white; " +
                      "-fx-border-color: #2196F3; " +
                      "-fx-border-radius: 5; " +
                      "-fx-background-radius: 5; " +
                      "-fx-padding: 8;");
        return field;
    }
    
    /**
     * Create styled text area
     */
    private TextArea createTextArea(String prompt) {
        TextArea area = new TextArea();
        area.setPromptText(prompt);
        area.setWrapText(true);
        area.setStyle("-fx-control-inner-background: white; " +
                     "-fx-border-color: #2196F3; " +
                     "-fx-border-radius: 5; " +
                     "-fx-background-radius: 5;");
        return area;
    }
    
    /**
     * Create styled combo box
     */
    private ComboBox<String> createComboBox(String[] items) {
        ComboBox<String> combo = new ComboBox<>(FXCollections.observableArrayList(items));
        combo.setStyle("-fx-background-color: white; " +
                      "-fx-border-color: #2196F3; " +
                      "-fx-border-radius: 5; " +
                      "-fx-background-radius: 5;");
        return combo;
    }
    
    /**
     * Create Lost Items tab with search and filter
     */
    private VBox createLostItemsTab() {
        return createItemsListTab("lost");
    }
    
    /**
     * Create Found Items tab with search and filter
     */
    private VBox createFoundItemsTab() {
        return createItemsListTab("found");
    }
    
    /**
     * Generic method to create items list tab
     */
    private VBox createItemsListTab(String kind) {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: transparent;");
        
        // Title
        Label title = createLabel(kind.equals("lost") ? "Lost Items" : "Found Items", 24, true);
        
        // Search and filter bar
        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        
        TextField searchField = createTextField("Search by title or description...");
        searchField.setPrefWidth(300);
        
        ComboBox<String> categoryFilter = createComboBox(CATEGORIES);
        categoryFilter.setValue("All Categories");
        categoryFilter.setPrefWidth(150);
        
        ComboBox<String> deptFilter = createComboBox(DEPARTMENTS);
        deptFilter.setValue("All Departments");
        deptFilter.setPrefWidth(150);
        
        Button searchBtn = createButton("Search", "#2196F3");
        Button refreshBtn = createButton("Refresh", "#4CAF50");
        
        searchBar.getChildren().addAll(new Label("Search:"), searchField, 
                                        new Label("Category:"), categoryFilter,
                                        new Label("Department:"), deptFilter,
                                        searchBtn, refreshBtn);
        
        // Items list
        ListView<String> itemsList = new ListView<>();
        itemsList.setStyle("-fx-background-color: white; -fx-border-color: #2196F3; -fx-border-radius: 5;");
        itemsList.setPrefHeight(400);
        
        // Details panel
        VBox detailsPanel = new VBox(10);
        detailsPanel.setPadding(new Insets(15));
        detailsPanel.setStyle("-fx-background-color: white; -fx-border-color: #2196F3; -fx-border-radius: 5; -fx-background-radius: 5;");
        detailsPanel.setPrefHeight(150);
        Label detailsLabel = createLabel("Select an item to view details", 14, false);
        detailsPanel.getChildren().add(detailsLabel);
        
        // Load items method
        Runnable loadItems = () -> {
            try {
                String search = searchField.getText().trim();
                String category = categoryFilter.getValue();
                String dept = deptFilter.getValue();
                
                List<Item> items = dbManager.getItems(kind, search, category, dept);
                ObservableList<String> displayItems = FXCollections.observableArrayList();
                
                for (Item item : items) {
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
                    String display = String.format("%s | %s | %s | Reported: %s",
                        item.getTitle(),
                        item.getCategory(),
                        item.getDepartment(),
                        sdf.format(item.getReportedAt()));
                    displayItems.add(display);
                }
                
                itemsList.setItems(displayItems);
                
                // Item selection handler
                itemsList.setOnMouseClicked(event -> {
                    int selectedIdx = itemsList.getSelectionModel().getSelectedIndex();
                    if (selectedIdx >= 0 && selectedIdx < items.size()) {
                        Item item = items.get(selectedIdx);
                        detailsPanel.getChildren().clear();
                        
                        VBox details = new VBox(8);
                        details.getChildren().addAll(
                            createLabel("Title: " + item.getTitle(), 16, true),
                            createLabel("Category: " + item.getCategory(), 13, false),
                            createLabel("Location: " + item.getLocation(), 13, false),
                            createLabel("Department: " + item.getDepartment(), 13, false),
                            createLabel("Description: " + item.getDescription(), 13, false),
                            createLabel("Reported by: " + item.getReporterName(), 13, false),
                            createLabel("Contact: " + item.getReporterEmail() + " | " + item.getReporterPhone(), 13, false)
                        );
                        detailsPanel.getChildren().add(details);
                    }
                });
                
            } catch (SQLException e) {
                showError("Error", "Failed to load items: " + e.getMessage());
            }
        };
        
        searchBtn.setOnAction(e -> loadItems.run());
        refreshBtn.setOnAction(e -> loadItems.run());
        
        // Initial load
        loadItems.run();
        
        container.getChildren().addAll(title, searchBar, itemsList, detailsPanel);
        return container;
    }
    
    /**
     * Create Report Item tab
     */
    private VBox createReportItemTab() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: transparent;");
        
        Label title = createLabel("Report Lost or Found Item", 24, true);
        
        // Form
        VBox formBox = new VBox(12);
        formBox.setMaxWidth(600);
        formBox.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-border-color: #2196F3; -fx-border-radius: 10; -fx-background-radius: 10;");
        
        // Kind selection
        HBox kindBox = new HBox(15);
        kindBox.setAlignment(Pos.CENTER_LEFT);
        Label kindLabel = createLabel("Item Type:", 14, true);
        ToggleGroup kindGroup = new ToggleGroup();
        RadioButton lostRadio = new RadioButton("Lost");
        lostRadio.setToggleGroup(kindGroup);
        lostRadio.setSelected(true);
        RadioButton foundRadio = new RadioButton("Found");
        foundRadio.setToggleGroup(kindGroup);
        kindBox.getChildren().addAll(kindLabel, lostRadio, foundRadio);
        
        // Input fields
        TextField titleField = createTextField("Item Title (e.g., Blue Backpack)");
        titleField.setPrefWidth(550);
        
        TextArea descField = createTextArea("Detailed description...");
        descField.setPrefHeight(80);
        descField.setPrefWidth(550);
        
        ComboBox<String> categoryCombo = createComboBox(Arrays.copyOfRange(CATEGORIES, 1, CATEGORIES.length));
        categoryCombo.setValue("Other");
        categoryCombo.setPrefWidth(550);
        
        TextField locationField = createTextField("Location (e.g., Library 2nd Floor)");
        locationField.setPrefWidth(550);
        
        String[] depts = Arrays.copyOfRange(DEPARTMENTS, 1, DEPARTMENTS.length);
        ComboBox<String> deptCombo = createComboBox(depts);
        deptCombo.setValue(currentUser.getDepartment() != null ? currentUser.getDepartment() : "CSE");
        deptCombo.setPrefWidth(550);
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 12px;");
        errorLabel.setVisible(false);
        
        Button submitBtn = createButton("Submit Report", "#4CAF50");
        submitBtn.setPrefWidth(550);
        submitBtn.setOnAction(e -> {
            String kind = lostRadio.isSelected() ? "lost" : "found";
            String itemTitle = titleField.getText().trim();
            String desc = descField.getText().trim();
            String category = categoryCombo.getValue();
            String location = locationField.getText().trim();
            String dept = deptCombo.getValue();
            
            if (itemTitle.isEmpty() || desc.isEmpty() || location.isEmpty()) {
                errorLabel.setText("Please fill in all required fields");
                errorLabel.setVisible(true);
                return;
            }
            
            try {
                boolean success = dbManager.reportItem(currentUser.getId(), kind, itemTitle, desc, category, location, dept);
                if (success) {
                    showInfo("Success", "Item reported successfully!");
                    // Clear form
                    titleField.clear();
                    descField.clear();
                    locationField.clear();
                    categoryCombo.setValue("Other");
                    errorLabel.setVisible(false);
                } else {
                    showError("Error", "Failed to report item");
                }
            } catch (SQLException ex) {
                showError("Database Error", "Failed to report item: " + ex.getMessage());
            }
        });
        
        formBox.getChildren().addAll(
            kindBox,
            createLabel("Title:", 13, true), titleField,
            createLabel("Description:", 13, true), descField,
            createLabel("Category:", 13, true), categoryCombo,
            createLabel("Location:", 13, true), locationField,
            createLabel("Department:", 13, true), deptCombo,
            errorLabel, submitBtn
        );
        
        container.getChildren().addAll(title, formBox);
        return container;
    }
    
    /**
     * Create Matches tab - shows probable matches between lost and found items
     */
    private VBox createMatchesTab() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: transparent;");
        
        Label title = createLabel("Find Probable Matches", 24, true);
        Label subtitle = createLabel("Select an item to find potential matches", 14, false);
        
        HBox selectBox = new HBox(15);
        selectBox.setAlignment(Pos.CENTER_LEFT);
        
        Label selectLabel = createLabel("Select Item:", 14, true);
        ComboBox<String> itemTypeCombo = createComboBox(new String[]{"My Lost Items", "My Found Items"});
        itemTypeCombo.setValue("My Lost Items");
        itemTypeCombo.setPrefWidth(200);
        
        Button findMatchesBtn = createButton("Find Matches", "#4CAF50");
        
        selectBox.getChildren().addAll(selectLabel, itemTypeCombo, findMatchesBtn);
        
        // Items to match
        ListView<String> myItemsList = new ListView<>();
        myItemsList.setStyle("-fx-background-color: white; -fx-border-color: #2196F3; -fx-border-radius: 5;");
        myItemsList.setPrefHeight(200);
        
        Label matchesLabel = createLabel("Probable Matches:", 16, true);
        matchesLabel.setVisible(false);
        
        // Matches list
        ListView<String> matchesList = new ListView<>();
        matchesList.setStyle("-fx-background-color: white; -fx-border-color: #FF9800; -fx-border-width: 2; -fx-border-radius: 5;");
        matchesList.setPrefHeight(250);
        matchesList.setVisible(false);
        
        // Details panel
        VBox detailsPanel = new VBox(10);
        detailsPanel.setPadding(new Insets(15));
        detailsPanel.setStyle("-fx-background-color: #FFF3E0; -fx-border-color: #FF9800; -fx-border-radius: 5; -fx-background-radius: 5;");
        detailsPanel.setVisible(false);
        
        // Load user items
        Runnable loadMyItems = () -> {
            try {
                String kind = itemTypeCombo.getValue().contains("Lost") ? "lost" : "found";
                List<Item> items = dbManager.getItems(kind, "", "All Categories", "All Departments");
                
                // Filter to current user's items
                List<Item> myItems = new ArrayList<>();
                for (Item item : items) {
                    if (item.getUserId() == currentUser.getId()) {
                        myItems.add(item);
                    }
                }
                
                ObservableList<String> displayItems = FXCollections.observableArrayList();
                for (Item item : myItems) {
                    displayItems.add(item.getTitle() + " - " + item.getCategory());
                }
                
                myItemsList.setItems(displayItems);
                
                // Find matches button handler
                findMatchesBtn.setOnAction(e -> {
                    int selectedIdx = myItemsList.getSelectionModel().getSelectedIndex();
                    if (selectedIdx < 0) {
                        showError("Selection Required", "Please select an item to find matches");
                        return;
                    }
                    
                    try {
                        Item selectedItem = myItems.get(selectedIdx);
                        List<Item> matches = dbManager.findMatches(selectedItem);
                        
                        if (matches.isEmpty()) {
                            showInfo("No Matches", "No probable matches found for this item.");
                            matchesLabel.setVisible(false);
                            matchesList.setVisible(false);
                            detailsPanel.setVisible(false);
                            return;
                        }
                        
                        // Show matches
                        ObservableList<String> matchDisplays = FXCollections.observableArrayList();
                        for (Item match : matches) {
                            matchDisplays.add("✨ " + match.getTitle() + " | " + match.getCategory() + " | " + match.getLocation());
                        }
                        
                        matchesList.setItems(matchDisplays);
                        matchesLabel.setVisible(true);
                        matchesList.setVisible(true);
                        
                        // Match selection handler
                        matchesList.setOnMouseClicked(event -> {
                            int matchIdx = matchesList.getSelectionModel().getSelectedIndex();
                            if (matchIdx >= 0 && matchIdx < matches.size()) {
                                Item match = matches.get(matchIdx);
                                detailsPanel.getChildren().clear();
                                
                                VBox details = new VBox(8);
                                details.getChildren().addAll(
                                    createLabel("✨ MATCH FOUND!", 18, true),
                                    createLabel("Title: " + match.getTitle(), 14, true),
                                    createLabel("Category: " + match.getCategory(), 12, false),
                                    createLabel("Location: " + match.getLocation(), 12, false),
                                    createLabel("Department: " + match.getDepartment(), 12, false),
                                    createLabel("Description: " + match.getDescription(), 12, false),
                                    createLabel("Reported by: " + match.getReporterName(), 12, false),
                                    createLabel("Contact: " + match.getReporterEmail() + " | " + match.getReporterPhone(), 12, true)
                                );
                                detailsPanel.getChildren().add(details);
                                detailsPanel.setVisible(true);
                            }
                        });
                        
                    } catch (SQLException ex) {
                        showError("Error", "Failed to find matches: " + ex.getMessage());
                    }
                });
                
            } catch (SQLException e) {
                showError("Error", "Failed to load items: " + e.getMessage());
            }
        };
        
        itemTypeCombo.setOnAction(e -> loadMyItems.run());
        loadMyItems.run();
        
        container.getChildren().addAll(title, subtitle, selectBox, 
                                       createLabel("Your Items:", 14, true), myItemsList,
                                       matchesLabel, matchesList, detailsPanel);
        return container;
    }
    
    /**
     * Create Chat tab with department channels
     */
    private VBox createChatTab() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: transparent;");
        
        Label title = createLabel("Department Chat", 24, true);
        
        // Department selector
        HBox deptBox = new HBox(10);
        deptBox.setAlignment(Pos.CENTER_LEFT);
        Label deptLabel = createLabel("Channel:", 14, true);
        String[] depts = Arrays.copyOfRange(DEPARTMENTS, 1, DEPARTMENTS.length);
        ComboBox<String> deptCombo = createComboBox(depts);
        deptCombo.setValue(currentUser.getDepartment() != null ? currentUser.getDepartment() : "CSE");
        deptCombo.setPrefWidth(200);
        deptBox.getChildren().addAll(deptLabel, deptCombo);
        
        // Chat messages area
        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.setStyle("-fx-control-inner-background: white; " +
                         "-fx-border-color: #2196F3; " +
                         "-fx-border-width: 2; " +
                         "-fx-border-radius: 5; " +
                         "-fx-font-family: 'Consolas', monospace; " +
                         "-fx-font-size: 13px;");
        chatArea.setPrefHeight(400);
        
        // Message input
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);
        TextField messageField = createTextField("Type your message...");
        messageField.setPrefWidth(800);
        Button sendBtn = createButton("Send", "#2196F3");
        inputBox.getChildren().addAll(messageField, sendBtn);
        
        // Load messages function
        Runnable loadMessages = () -> {
            try {
                String dept = deptCombo.getValue();
                List<Message> messages = dbManager.getMessages(dept, 50);
                
                StringBuilder chatText = new StringBuilder();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                for (Message msg : messages) {
                    chatText.append(String.format("[%s] %s: %s\n",
                        sdf.format(msg.getSentAt()),
                        msg.getSenderName(),
                        msg.getContent()));
                }
                
                chatArea.setText(chatText.toString());
                chatArea.setScrollTop(Double.MAX_VALUE); // Scroll to bottom
                
            } catch (SQLException e) {
                showError("Error", "Failed to load messages: " + e.getMessage());
            }
        };
        
        // Send message handler
        sendBtn.setOnAction(e -> {
            String message = messageField.getText().trim();
            if (message.isEmpty()) {
                return;
            }
            
            try {
                String dept = deptCombo.getValue();
                boolean success = dbManager.sendMessage(currentUser.getId(), dept, message);
                if (success) {
                    messageField.clear();
                    loadMessages.run();
                }
            } catch (SQLException ex) {
                showError("Error", "Failed to send message: " + ex.getMessage());
            }
        });
        
        // Enter key to send
        messageField.setOnAction(e -> sendBtn.fire());
        
        // Department change handler
        deptCombo.setOnAction(e -> loadMessages.run());
        
        // Initial load
        loadMessages.run();
        
        // Real-time polling (every 3 seconds)
        if (chatPollingTimeline != null) {
            chatPollingTimeline.stop();
        }
        chatPollingTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> loadMessages.run()));
        chatPollingTimeline.setCycleCount(Timeline.INDEFINITE);
        chatPollingTimeline.play();
        
        Label infoLabel = createLabel("💡 Messages refresh every 3 seconds", 11, false);
        infoLabel.setStyle("-fx-text-fill: #757575;");
        
        container.getChildren().addAll(title, deptBox, chatArea, inputBox, infoLabel);
        return container;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}