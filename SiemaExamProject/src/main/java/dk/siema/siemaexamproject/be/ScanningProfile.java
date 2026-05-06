package dk.siema.siemaexamproject.be;

import java.util.List;

public class ScanningProfile {
    private int id;
    private int clientId;
    private String profileName;
    private String description;
    private List<ProfileSetting> profileSettings;

    public ScanningProfile(int clientId, String profileName, String description, List<ProfileSetting> profileSettings) {
        this.clientId = clientId;
        this.profileName = profileName;
        this.description = description;
        this.profileSettings = profileSettings;
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

    public String getDescription() {
        return description;
    }
    public List<ProfileSetting> getProfileSettings() {
        return profileSettings;
    }
    public void setSettings(List<ProfileSetting> profileSettings) {
        this.profileSettings = profileSettings;
    }

    public void setName(String name) {
        this.profileName = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setClient(int clientId) {
        this.clientId = clientId;
    }
}
