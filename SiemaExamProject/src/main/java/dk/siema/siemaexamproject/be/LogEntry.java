package dk.siema.siemaexamproject.be;

import java.time.LocalDateTime;

public class LogEntry {
    private int id;
    private int userId;
    private String action;
    private String details;
    private LocalDateTime time;

    public LogEntry(int userId, String action, String details) {
        this.userId = userId;
        this.action = action;
        this.details = details;
        this.time = LocalDateTime.now();
    }
    public int getUserId() {return userId;}
    public String getAction() {return action;}
    public String getDetails() {return details;}
    public LocalDateTime getTime() {return time;}
}
