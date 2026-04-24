package dk.siema.siemaexamproject.be;

import dk.siema.siemaexamproject.be.enums.UserRole;
import java.util.UUID;

public class User {

    private UUID id;
    private String username;
    private String email;
    private String password; // holds HASH, not raw password
    private UserRole role;

    //Constructor (without id - for creation)
    public User(String username, String email, String password, UserRole role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    //Constructor (with id - from DB)
    public User(UUID id, String username, String email, String password, UserRole role) {
        this. id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }


    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void changePassword(String hashedPassword) {this.password = hashedPassword;}

    public UserRole getRole() { return role; }
    public void changeRole(UserRole role) {this.role = role;}

    /* controlled changes/hybrid ... IkwIm*/
}
