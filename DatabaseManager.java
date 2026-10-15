import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * DatabaseManager handles all database operations using JDBC with parameterized queries
 * 
 * CONFIGURATION REQUIRED:
 * Before running, update the database credentials below:
 * - DB_URL: Your MySQL server URL (default: localhost:3306)
 * - DB_USER: Your MySQL username (default: root)
 * - DB_PASS: Your MySQL password (IMPORTANT: Set this before running!)
 * 
 * Make sure MySQL server is running and database 'lostfound_db' exists.
 * Run schema.sql first to create the database structure.
 */
public class DatabaseManager {
    // ==================== DATABASE CONFIGURATION ====================
    // TODO: Configure these values before running the application
    // ================================================================
    
    /** MySQL database URL */
    private static final String DB_URL = "jdbc:mysql://localhost:3306/lostfound_db?useSSL=false&serverTimezone=UTC";
    
    /** MySQL username */
    private static final String DB_USER = "root";
    
    /** MySQL password - CHANGE THIS before running! */
    private static final String DB_PASS = "your_password_here"; // ⚠️ CHANGE THIS!

    private Connection connection;

    /**
     * Establish database connection
     */
    public void connect() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Add mysql-connector-java to classpath.", e);
        }
    }

    /**
     * Close database connection
     */
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Hash password using SHA-256
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Register a new user
     */
    public boolean registerUser(String name, String email, String phone, String password, String role, String department) throws SQLException {
        String sql = "INSERT INTO users (name, email, phone, password_hash, role, department) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setString(4, hashPassword(password));
            pstmt.setString(5, role);
            pstmt.setString(6, department);
            pstmt.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false; // Email already exists
        }
    }

    /**
     * Login user and return User object if credentials are valid
     */
    public User loginUser(String email, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ? AND password_hash = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, hashPassword(password));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("password_hash"),
                    rs.getString("role"),
                    rs.getString("department"),
                    rs.getTimestamp("created_at")
                );
            }
        }
        return null; // Invalid credentials
    }

    /**
     * Report a new lost or found item
     */
    public boolean reportItem(int userId, String kind, String title, String description, String category, String location, String department) throws SQLException {
        String sql = "INSERT INTO items (user_id, kind, title, description, category, location, department) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, kind);
            pstmt.setString(3, title);
            pstmt.setString(4, description);
            pstmt.setString(5, category);
            pstmt.setString(6, location);
            pstmt.setString(7, department);
            pstmt.executeUpdate();
            return true;
        }
    }

    /**
     * Get all items of a specific kind (lost/found) with optional filters
     */
    public List<Item> getItems(String kind, String searchText, String categoryFilter, String departmentFilter) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT i.*, u.name as reporter_name, u.email as reporter_email, u.phone as reporter_phone ");
        sql.append("FROM items i JOIN users u ON i.user_id = u.id WHERE i.kind = ?");
        
        List<String> params = new ArrayList<>();
        params.add(kind);

        if (searchText != null && !searchText.trim().isEmpty()) {
            sql.append(" AND (LOWER(i.title) LIKE ? OR LOWER(i.description) LIKE ?)");
            String searchPattern = "%" + searchText.toLowerCase() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if (categoryFilter != null && !categoryFilter.equals("All Categories")) {
            sql.append(" AND i.category = ?");
            params.add(categoryFilter);
        }

        if (departmentFilter != null && !departmentFilter.equals("All Departments")) {
            sql.append(" AND i.department = ?");
            params.add(departmentFilter);
        }

        sql.append(" ORDER BY i.reported_at DESC");

        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setString(i + 1, params.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            List<Item> items = new ArrayList<>();
            while (rs.next()) {
                Item item = new Item(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("kind"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("category"),
                    rs.getString("location"),
                    rs.getString("department"),
                    rs.getTimestamp("reported_at")
                );
                item.setReporterName(rs.getString("reporter_name"));
                item.setReporterEmail(rs.getString("reporter_email"));
                item.setReporterPhone(rs.getString("reporter_phone"));
                items.add(item);
            }
            return items;
        }
    }

    /**
     * Find probable matches between lost and found items
     * Uses fuzzy matching: case-insensitive title match + category + location similarity
     */
    public List<Item> findMatches(Item referenceItem) throws SQLException {
        String oppositeKind = referenceItem.getKind().equals("lost") ? "found" : "lost";
        String sql = "SELECT i.*, u.name as reporter_name, u.email as reporter_email, u.phone as reporter_phone " +
                     "FROM items i JOIN users u ON i.user_id = u.id WHERE i.kind = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, oppositeKind);
            ResultSet rs = pstmt.executeQuery();
            
            List<Item> matches = new ArrayList<>();
            String refTitle = referenceItem.getTitle().toLowerCase();
            String refCategory = referenceItem.getCategory() != null ? referenceItem.getCategory().toLowerCase() : "";
            String refLocation = referenceItem.getLocation() != null ? referenceItem.getLocation().toLowerCase() : "";
            
            while (rs.next()) {
                Item item = new Item(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("kind"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("category"),
                    rs.getString("location"),
                    rs.getString("department"),
                    rs.getTimestamp("reported_at")
                );
                item.setReporterName(rs.getString("reporter_name"));
                item.setReporterEmail(rs.getString("reporter_email"));
                item.setReporterPhone(rs.getString("reporter_phone"));
                
                // Fuzzy matching algorithm
                String itemTitle = item.getTitle().toLowerCase();
                String itemCategory = item.getCategory() != null ? item.getCategory().toLowerCase() : "";
                String itemLocation = item.getLocation() != null ? item.getLocation().toLowerCase() : "";
                
                int matchScore = 0;
                
                // Title similarity (contains or partial match)
                if (itemTitle.contains(refTitle) || refTitle.contains(itemTitle)) {
                    matchScore += 3;
                } else {
                    // Check for common words
                    String[] refWords = refTitle.split("\\s+");
                    String[] itemWords = itemTitle.split("\\s+");
                    for (String refWord : refWords) {
                        for (String itemWord : itemWords) {
                            if (refWord.length() > 3 && itemWord.contains(refWord)) {
                                matchScore += 1;
                            }
                        }
                    }
                }
                
                // Category exact match
                if (!refCategory.isEmpty() && refCategory.equals(itemCategory)) {
                    matchScore += 2;
                }
                
                // Location similarity
                if (!refLocation.isEmpty() && itemLocation.contains(refLocation)) {
                    matchScore += 2;
                } else if (!refLocation.isEmpty() && !itemLocation.isEmpty()) {
                    String[] refLocWords = refLocation.split("\\s+");
                    String[] itemLocWords = itemLocation.split("\\s+");
                    for (String refWord : refLocWords) {
                        for (String itemWord : itemLocWords) {
                            if (refWord.length() > 3 && itemWord.contains(refWord)) {
                                matchScore += 1;
                            }
                        }
                    }
                }
                
                // If match score is high enough, add to matches
                if (matchScore >= 3) {
                    item.setMatch(true);
                    matches.add(item);
                }
            }
            
            return matches;
        }
    }

    /**
     * Send a chat message to a department channel
     */
    public boolean sendMessage(int senderId, String department, String content) throws SQLException {
        String sql = "INSERT INTO messages (sender_id, department, content) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, senderId);
            pstmt.setString(2, department);
            pstmt.setString(3, content);
            pstmt.executeUpdate();
            return true;
        }
    }

    /**
     * Get recent messages from a department channel
     */
    public List<Message> getMessages(String department, int limit) throws SQLException {
        String sql = "SELECT m.*, u.name as sender_name FROM messages m " +
                     "JOIN users u ON m.sender_id = u.id " +
                     "WHERE m.department = ? " +
                     "ORDER BY m.sent_at DESC LIMIT ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, department);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            
            List<Message> messages = new ArrayList<>();
            while (rs.next()) {
                Message msg = new Message(
                    rs.getInt("id"),
                    rs.getInt("sender_id"),
                    rs.getString("department"),
                    rs.getString("content"),
                    rs.getTimestamp("sent_at")
                );
                msg.setSenderName(rs.getString("sender_name"));
                messages.add(msg);
            }
            
            // Reverse to show oldest first
            List<Message> reversed = new ArrayList<>();
            for (int i = messages.size() - 1; i >= 0; i--) {
                reversed.add(messages.get(i));
            }
            return reversed;
        }
    }

    /**
     * Get user statistics (for dashboard)
     */
    public int getUserItemCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM items WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }
}
