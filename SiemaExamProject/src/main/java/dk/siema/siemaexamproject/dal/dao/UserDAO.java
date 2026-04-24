package dk.siema.siemaexamproject.dal.dao;

import dk.siema.siemaexamproject.be.User;
import dk.siema.siemaexamproject.be.enums.UserRole;
import dk.siema.siemaexamproject.dal.ConnectionManager;
import dk.siema.siemaexamproject.dal.interfaces.IUserDAO;

import java.nio.ByteBuffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDAO implements IUserDAO {

    //ADD
    @Override
    public User add(User user) throws SQLException {
        String sql = "INSERT INTO Users (id, username, email, password_hash, role) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBytes(1, uuidToBytes(user.getId()));
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getRole().name());

            stmt.executeUpdate();

            return user;
        }
    }

    //GETTERS
    @Override
    public List<User> getAll() throws SQLException {
        String sql = "SELECT * FROM Users";

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
        String sql = "SELECT * FROM Users WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBytes(1, uuidToBytes(id));

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
        String sql = "SELECT * FROM Users WHERE username = ?";

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
        String sql = "UPDATE Users SET username = ?, email = ?, role = ? WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getRole().name());
            stmt.setBytes(4, uuidToBytes(user.getId()));

            stmt.executeUpdate();
        }
    }
    @Override
    public void updatePassword(UUID id, String newHash) throws SQLException {
        String sql = "UPDATE Users SET password_hash = ? WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newHash);
            stmt.setBytes(2, uuidToBytes(id));

            stmt.executeUpdate();
        }
    }

     //DELETE
    @Override
    public void delete(UUID id) throws SQLException {
        String sql = "DELETE FROM Users WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBytes(1, uuidToBytes(id));
            stmt.executeUpdate();
        }
    }

    /* MAPPER */
    private User mapUser(ResultSet rs) throws SQLException {

        UUID id = bytesToUUID(rs.getBytes("id"));
        String username = rs.getString("username");
        String email = rs.getString("email");
        String password = rs.getString("password_hash");
        UserRole role = UserRole.valueOf(rs.getString("role"));

        return new User(id, username, email, password, role);
    }

    /* UUID → BINARY(16) */
    private byte[] uuidToBytes(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }

    /* BINARY(16) → UUID */
    private UUID bytesToUUID(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long high = buffer.getLong();
        long low = buffer.getLong();
        return new UUID(high, low);
    }
}