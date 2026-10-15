import java.sql.Timestamp;

/**
 * User model class representing users in the Lost & Found system
 * Supports student, staff, and admin roles
 */
public class User {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String passwordHash;
    private String role; // student, staff, admin
    private String department;
    private Timestamp createdAt;

    // Constructor for new user (before DB insertion)
    public User(String name, String email, String phone, String passwordHash, String role, String department) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.role = role;
        this.department = department;
    }

    // Constructor for existing user (from DB)
    public User(int id, String name, String email, String phone, String passwordHash, String role, String department, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.role = role;
        this.department = department;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return name + " (" + role + ")";
    }
}
