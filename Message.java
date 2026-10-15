import java.sql.Timestamp;

/**
 * Message model class for department-based chat system
 */
public class Message {
    private int id;
    private int senderId;
    private String department;
    private String content;
    private Timestamp sentAt;
    
    // Additional fields for display
    private String senderName;

    // Constructor for new message (before DB insertion)
    public Message(int senderId, String department, String content) {
        this.senderId = senderId;
        this.department = department;
        this.content = content;
    }

    // Constructor for existing message (from DB)
    public Message(int id, int senderId, String department, String content, Timestamp sentAt) {
        this.id = id;
        this.senderId = senderId;
        this.department = department;
        this.content = content;
        this.sentAt = sentAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Timestamp getSentAt() { return sentAt; }
    public void setSentAt(Timestamp sentAt) { this.sentAt = sentAt; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    @Override
    public String toString() {
        return senderName + ": " + content;
    }
}
