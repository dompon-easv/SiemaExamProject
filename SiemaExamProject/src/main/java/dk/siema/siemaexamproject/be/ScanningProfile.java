package dk.siema.siemaexamproject.be;

public class ScanningProfile {
    private int id;
    private int clientId;
    private String profileName;

    public ScanningProfile(int id, int clientId, String profileName) {
        this.id = id;
        this.clientId = clientId;
        this.profileName = profileName;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getClientId() {
        return clientId;
    }

    public String getName() {
        return profileName;
    }
}
