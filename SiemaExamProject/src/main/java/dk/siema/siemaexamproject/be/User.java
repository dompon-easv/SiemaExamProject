package dk.siema.siemaexamproject.be;


public class User {
    private int id;
    private String username;
    private String password;
    private UserRole role;

    public User(int id, String username, String password, UserRole role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }
    public int getId() {return id;}
    public String getName() {return username;}
    public String getPassword() {return password;}
    public UserRole getRole() {return role;}

    public void setName(String name) {this.username = username;}
    public void setPassword(String password) {this.password = password;}
    public void setRole(UserRole role) {this.role = role;}

}
