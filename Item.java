import java.sql.Timestamp;

/**
 * Item model class representing lost or found items
 */
public class Item {
    private int id;
    private int userId;
    private String kind; // "lost" or "found"
    private String title;
    private String description;
    private String category;
    private String location;
    private String department;
    private Timestamp reportedAt;
    
    // Additional fields for display
    private String reporterName;
    private String reporterEmail;
    private String reporterPhone;
    private boolean isMatch; // For highlighting probable matches

    // Constructor for new item (before DB insertion)
    public Item(int userId, String kind, String title, String description, String category, String location, String department) {
        this.userId = userId;
        this.kind = kind;
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.department = department;
    }

    // Constructor for existing item (from DB)
    public Item(int id, int userId, String kind, String title, String description, String category, 
                String location, String department, Timestamp reportedAt) {
        this.id = id;
        this.userId = userId;
        this.kind = kind;
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.department = department;
        this.reportedAt = reportedAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Timestamp getReportedAt() { return reportedAt; }
    public void setReportedAt(Timestamp reportedAt) { this.reportedAt = reportedAt; }

    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }

    public String getReporterEmail() { return reporterEmail; }
    public void setReporterEmail(String reporterEmail) { this.reporterEmail = reporterEmail; }

    public String getReporterPhone() { return reporterPhone; }
    public void setReporterPhone(String reporterPhone) { this.reporterPhone = reporterPhone; }

    public boolean isMatch() { return isMatch; }
    public void setMatch(boolean match) { isMatch = match; }

    @Override
    public String toString() {
        return kind.toUpperCase() + ": " + title + " (" + category + ")";
    }
}
