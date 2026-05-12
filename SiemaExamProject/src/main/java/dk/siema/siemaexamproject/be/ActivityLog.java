package dk.siema.siemaexamproject.be;

import dk.siema.siemaexamproject.be.enums.LogAction;

import java.time.LocalDateTime;
import java.util.UUID;

public class ActivityLog {
    private int id; //for db row
    private UUID userId;
    private UUID fileId;
    private LogAction action;
    private String details;
    private LocalDateTime time;
    private String username;

    public ActivityLog(UUID userId, UUID fileId, LogAction action, String details, LocalDateTime time) {
        this.userId = userId;
        this.fileId = fileId;
        this.action = action;
        this.details = details;
        this.time = time;
    }

    public UUID getFileId() {
        return fileId;
    }

    public UUID getUserId() {return userId;}
    public LogAction getAction() {return action;}
    public String getDetails() {return details;}
    public LocalDateTime getTime() {return time;}
    public int getId() { return id; }
    public String getUsername() {return username;}
    public void setId(int id) {this.id = id;}
    public void setUsername(String username) {this.username = username;}/* to set generated id*/
}
