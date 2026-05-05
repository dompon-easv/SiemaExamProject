package dk.siema.siemaexamproject.dal.dao;

import dk.siema.siemaexamproject.be.ScanningProfile;
import dk.siema.siemaexamproject.dal.ConnectionManager;
import dk.siema.siemaexamproject.dal.interfaces.IScanningProfileDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScanningProfileDAO implements IScanningProfileDAO {

    @Override
    public ScanningProfile add(ScanningProfile profile) throws SQLException {
        String sql = "INSERT INTO dbo.ScanningProfiles (profile_name, client_id) VALUES (?,?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, profile.getName());
            stmt.setInt(2, profile.getClientId());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    profile.setId(newId);

                }

            }
        }
        return profile;

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


    private ScanningProfile mapProfile(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("profile_name");
        int clientId = rs.getInt("client_id");

        return new ScanningProfile(id, clientId, name);
    }

}



