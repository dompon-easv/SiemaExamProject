package dk.siema.siemaexamproject.be;

public class ProfileSetting {
    private Setting setting;
    private String value;

    public ProfileSetting(Setting setting, String value) {
        this.setting = setting;
        this.value = value;
    }

    public Setting getSetting() {
        return setting;
    }
    public String getValue() {
        return value;
    }

    public void setSetting() {
        this.setting = setting;
    }
}
