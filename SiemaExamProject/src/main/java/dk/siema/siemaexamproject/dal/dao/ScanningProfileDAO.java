package dk.siema.siemaexamproject.dal.dao;

import dk.siema.siemaexamproject.be.ProfileSetting;
import dk.siema.siemaexamproject.be.ScanningProfile;
import dk.siema.siemaexamproject.be.Setting;
import dk.siema.siemaexamproject.dal.ConnectionManager;
import dk.siema.siemaexamproject.dal.interfaces.IScanningProfileDAO;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScanningProfileDAO implements IScanningProfileDAO {

    @Override
    public ScanningProfile add(ScanningProfile profile) throws SQLException {
        String sql = "INSERT INTO dbo.ScanningProfiles (client_id, profile_name, description) VALUES (?,?,?)";
        String sql1 = "INSERT INTO dbo.ProfileSettings (profile_id, setting_id, value) VALUES (?,?,?)";

        Connection conn = ConnectionManager.getConnection();
        try {
            conn.setAutoCommit(false);
            int newProfileId = 0;
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, profile.getClientId());
                stmt.setString(2, profile.getName());
                stmt.setString(3, profile.getDescription());
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();

                if (rs.next()) {
                    newProfileId = rs.getInt(1);
                    profile.setId(newProfileId);
                }
            }
            try (PreparedStatement stmt1 = conn.prepareStatement(sql1)) {
                for (ProfileSetting setting : profile.getProfileSettings()) {
                    stmt1.setInt(1, newProfileId);
                    stmt1.setInt(2, setting.getSetting().getId());
                    stmt1.setString(3, setting.getValue());

                    stmt1.addBatch();
                }
                stmt1.executeBatch();
            }
            conn.commit();
            return profile;
        } catch (
                SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }


    @Override
    public List<ScanningProfile> getProfilesbyClient(int clientId) throws SQLException {
        String sql = "SELECT * FROM dbo.ScanningProfiles WHERE client_id = ?";

        List<ScanningProfile> profiles = new ArrayList<>();

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        { stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                profiles.add(mapProfile(rs));
            }
        }
        }

        return profiles;
    }

    @Override
    public ScanningProfile getByProfileName(String name) throws SQLException {
        String sql = "SELECT * FROM dbo.ScanningProfiles WHERE profile_name = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapProfile(rs);
                }
            }
        }

        return null;
    }

    @Override
    public void deleteProfile(ScanningProfile profile) throws SQLException {
        String sql = "DELETE FROM dbo.ScanningProfiles WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, profile.getId());
            stmt.executeUpdate();
        }

    }

    @Override
    public List<ScanningProfile> getAllProfiles() throws SQLException {
        String sql = "SELECT * FROM dbo.ScanningProfiles";
        List<ScanningProfile> allProfiles = new ArrayList<>();

        try (Connection conn = ConnectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    ScanningProfile profile = mapProfile(rs);
                    List<ProfileSetting> settings = getSettingsForProfile(profile.getId(), conn);
                    profile.setSettings(settings);

                    allProfiles.add(profile);
                }
            }
        }
        return allProfiles;
    }

    private List<ProfileSetting> getSettingsForProfile(int id, Connection conn) throws SQLException {
        List<ProfileSetting> settings = new ArrayList<>();
        String sql = "SELECT ps.setting_id, ps.value, s.name " +
                "FROM ProfileSettings ps " +
                "JOIN Settings s ON ps.setting_id = s.id " +
                "WHERE ps.profile_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // 1. Create the base Setting object (the definition)
                    Setting s = new Setting(rs.getInt("setting_id"), rs.getString("name"));

                    // 2. Create the ProfileSetting object (the specific value for this profile)
                    ProfileSetting ps = new ProfileSetting(s, rs.getString("value"));
                    ps.setSetting(s);
                    ps.setValue(rs.getString("value"));

                    settings.add(ps);
                }
            }
        }
        return settings;
    }


    private ScanningProfile mapProfile(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("profile_name");
        int clientId = rs.getInt("client_id");
        String description = rs.getString("description");

        ScanningProfile profile = new ScanningProfile(clientId, name, description, new ArrayList<>());
        profile.setId(id);
        return profile;
    }

}



