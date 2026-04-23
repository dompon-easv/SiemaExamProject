package dk.siema.siemaexamproject.be;

import java.time.LocalDateTime;

public class LogEntry {
    private int id;
    private int userId;
    private int fileId;
    private String action;
    private String details;
    private LocalDateTime time;

    public LogEntry(int userId, String action, String details) {
        this.userId = userId;
        this.action = action;
        this.details = details;
        this.time = LocalDateTime.now();
    }
    public LogEntry(int id, int userId,int fileId, String action, String details, LocalDateTime time) {
        this.id = id;
        this.userId = userId;
        this.fileId = fileId;
        this.action = action;
        this.details = details;
        this.time = time;
    }
    public int getUserId() {return userId;}
    public String getAction() {return action;}
    public String getDetails() {return details;}
    public LocalDateTime getTime() {return time;}
    public int getId() { return id; }
    public void setId(int id) {this.id = id;} /* to set generated id*/
}
