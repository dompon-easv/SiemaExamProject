package dk.siema.siemaexamproject.dal.dao;

import dk.siema.siemaexamproject.be.ProfileSetting;
import dk.siema.siemaexamproject.be.ScanningProfile;
import dk.siema.siemaexamproject.be.Setting;
import dk.siema.siemaexamproject.dal.exception.DalException;
import dk.siema.siemaexamproject.dal.ConnectionManager;
import dk.siema.siemaexamproject.dal.interfaces.IScanningProfileDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScanningProfileDAO implements IScanningProfileDAO {

    @Override
    public ScanningProfile add(ScanningProfile profile) throws DalException {
        String sql = "INSERT INTO dbo.ScanningProfiles (client_id, profile_name, description) VALUES (?,?,?)";
        String sql1 = "INSERT INTO dbo.ProfileSettings (profile_id, setting_id, value) VALUES (?,?,?)";

        Connection conn = null; // Declare outside the try block

        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false); // Start the transaction

            int newProfileId = 0;

            // 1. Insert the Profile
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, profile.getClientId());
                stmt.setString(2, profile.getName());
                stmt.setString(3, profile.getDescription());
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        newProfileId = rs.getInt(1);
                        profile.setId(newProfileId);
                    }
                }
            }

            // 2. Insert the Settings
            try (PreparedStatement stmt1 = conn.prepareStatement(sql1)) {
                for (ProfileSetting setting : profile.getProfileSettings()) {
                    stmt1.setInt(1, newProfileId);
                    stmt1.setInt(2, setting.getSetting().getId());
                    stmt1.setString(3, setting.getValue());
                    stmt1.addBatch();
                }
                stmt1.executeBatch();
            }

            conn.commit(); // Everything worked! Commit to the database.
            return profile;

        } catch (SQLException e) {

            // 3. The Safe Rollback
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    // If rollback fails, we attach it to the main error so we don't lose the original stack trace!
                    e.addSuppressed(rollbackEx);
                }
            }

            // 4. Wrap and throw the DalException
            throw new DalException("Failed to save the scanning profile and settings. Changes were rolled back.", e);

        } finally {

            // 5. The Safe Cleanup
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset for the connection pool
                    conn.close();
                } catch (SQLException closeEx) {
                    // We ignore closing errors here so they don't overwrite our DalException
                    System.err.println("Failed to close connection: " + closeEx.getMessage());
                }
            }
        }
    }

    @Override
    public List<ScanningProfile> getProfilesbyClient(int clientId) throws DalException {
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
        } catch (SQLException e) {
            throw new DalException("Error getting profiles by client id " + clientId, e);
        }

        return profiles;
    }

    @Override
    public ScanningProfile getByProfileName(String name) throws DalException {
        String sql = "SELECT * FROM dbo.ScanningProfiles WHERE profile_name = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapProfile(rs);
                }
            }
        } catch (SQLException e) {
            throw new DalException("Error getting profile by name", e);
        }

        return null;
    }

    @Override
    public void deleteProfile(ScanningProfile profile) throws DalException {
        String sql = "DELETE FROM dbo.ScanningProfiles WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, profile.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DalException("Problem while deleting profile", e);
        }

    }

    @Override
    public List<ScanningProfile> getAllProfiles() throws DalException {
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
        } catch (SQLException e) {
            throw new DalException("Error while getting all scanning profiles", e);
        }
        return allProfiles;
    }

    @Override
    public void updateProfile(ScanningProfile profileToEdit) throws DalException {
        String sql = "UPDATE dbo.ScanningProfiles SET client_id = ?, profile_name = ?, description = ? WHERE id = ?";
        String sql2 = "INSERT INTO dbo.ProfileSettings (profile_id, setting_id, value) VALUES (?,?,?)";
        String sql1 = "DELETE FROM dbo.ProfileSettings  WHERE profile_id = ?";

        try (Connection conn = ConnectionManager.getConnection()) {
            try {
                conn.setAutoCommit(false);
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, profileToEdit.getClientId());
                    stmt.setString(2, profileToEdit.getName());
                    stmt.setString(3, profileToEdit.getDescription());
                    stmt.setInt(4, profileToEdit.getId());
                    stmt.executeUpdate();
                }
                try (PreparedStatement stmt1 = conn.prepareStatement(sql1)) {
                    stmt1.setInt(1, profileToEdit.getId());
                    stmt1.executeUpdate();
                }
                try (PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
                    for (ProfileSetting setting : profileToEdit.getProfileSettings()) {
                        stmt2.setInt(1, profileToEdit.getId());
                        stmt2.setInt(2, setting.getSetting().getId());
                        stmt2.setString(3, setting.getValue());
                        stmt2.addBatch();
                    }
                    stmt2.executeBatch();
                }
                conn.commit();
            } catch (
                    SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DalException("Error updating profile", e);
        }
    }
    private List<ProfileSetting> getSettingsForProfile(int id, Connection conn) throws DalException {
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
        } catch (SQLException e) {
            throw new DalException("Error getting profile settings for profile id " + id, e);
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




