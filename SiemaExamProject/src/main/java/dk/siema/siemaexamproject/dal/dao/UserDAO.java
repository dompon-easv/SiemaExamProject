package dk.siema.siemaexamproject.dal.dao;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.siema.siemaexamproject.be.ScanningProfile;
import dk.siema.siemaexamproject.be.User;
import dk.siema.siemaexamproject.be.enums.UserRole;
import dk.siema.siemaexamproject.bll.exceptions.ServiceException;
import dk.siema.siemaexamproject.dal.ConnectionManager;
import dk.siema.siemaexamproject.dal.interfaces.IUserDAO;
import dk.siema.siemaexamproject.dal.util.BytesConverter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDAO implements IUserDAO {


    //ADD
    @Override
    public User add(User user) throws SQLException {
        String sql = "INSERT INTO dbo.Users (id, username, email, password_hash, role) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBytes(1, BytesConverter.uuidToBytes(user.getId()));
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPasswordHash());
            stmt.setString(5, user.getRole().name());

            stmt.executeUpdate();

            return user;
        }
    }

    //GETTERS
    @Override
    public List<User> getAll() throws SQLException {
        String sql = "SELECT * FROM dbo.Users";

        List<User> users = new ArrayList<>();

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }

        return users;
    }


    @Override
    public User getById(UUID id) throws SQLException {
        String sql = "SELECT * FROM dbo.Users WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBytes(1, BytesConverter.uuidToBytes(id));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }

        return null;
    }


    @Override
    public User getByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM dbo.Users WHERE username = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }

        return null;
    }

    //UPDATE
    @Override
    public void update(User user) throws SQLException {
        String sql = "UPDATE dbo.Users SET username = ?, email = ?, role = ? WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getRole().name());
            stmt.setBytes(4, BytesConverter.uuidToBytes(user.getId()));

            stmt.executeUpdate();
        }
    }
    @Override
    public void updatePassword(UUID id, String newHash) throws SQLException {
        String sql = "UPDATE dbo.Users SET password_hash = ? WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newHash);
            stmt.setBytes(2, BytesConverter.uuidToBytes(id));

            stmt.executeUpdate();
        }
    }

     //DELETE
    @Override
    public void delete(UUID id) throws SQLException {
        String sql = "DELETE FROM dbo.Users WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBytes(1, BytesConverter.uuidToBytes(id));
            stmt.executeUpdate();
        }
    }

    /* MAPPER */
    private User mapUser(ResultSet rs) throws SQLException {

        UUID id = BytesConverter.bytesToUUID(rs.getBytes("id"));
        String username = rs.getString("username");
        String email = rs.getString("email");
        String password = rs.getString("password_hash");
        UserRole role = UserRole.valueOf(rs.getString("role"));

        return new User(id, username, email, password, role);
    }

    public List<ScanningProfile> getProfilesForUser(UUID id) throws SQLException {
        List<ScanningProfile> assignedProfiles = new ArrayList<>();

        String sql = "SELECT sp.* FROM dbo.ScanningProfiles sp " +
                "JOIN dbo.UserProfiles up ON sp.id = up.profile_id "
                + "WHERE up.user_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, BytesConverter.uuidToBytes(id));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ScanningProfile profile = new ScanningProfile(
                        rs.getInt("client_id"),
                        rs.getString("profile_name"),
                        rs.getString("description"),
                        new ArrayList<>()
                );
                profile.setId(rs.getInt("id"));
                assignedProfiles.add(profile);
            }
            return assignedProfiles;
        }
    }

    @Override
    public void assignProfilesForUser(UUID id, int profileId) throws SQLException {
        String sql = "INSERT INTO dbo.UserProfiles (user_id, profile_id) VALUES (?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, BytesConverter.uuidToBytes(id));
            stmt.setInt(2, profileId);
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteProfilesFromUser(UUID id, int profileId) throws SQLException {
        String sql = "DELETE FROM dbo.UserProfiles WHERE user_id = ? AND profile_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, BytesConverter.uuidToBytes(id));
            stmt.setInt(2, profileId);
            stmt.executeUpdate();
        }
    }
}