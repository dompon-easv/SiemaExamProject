package dk.siema.siemaexamproject.dal.dao;

import dk.siema.siemaexamproject.be.LogEntry;
import dk.siema.siemaexamproject.dal.ConnectionManager;
import dk.siema.siemaexamproject.dal.interfaces.ILogEntryDAO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LogEntryDAO implements ILogEntryDAO {

    @Override
    public void add(LogEntry log) throws SQLException {
        String sql = "INSERT INTO LogEntry (user_id, action, details, time) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, log.getUserId());
            stmt.setString(2, log.getAction());
            stmt.setString(3, log.getDetails());
            stmt.setTimestamp(4, Timestamp.valueOf(log.getTime()));

            stmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public List<LogEntry> getLogsByUser(int userId) throws SQLException {
        String sql = "SELECT * FROM LogEntry WHERE user_id = ? ORDER BY time DESC";

        List<LogEntry> logs = new ArrayList<>();

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                logs.add(mapLogEntry(rs));
            }
        }

        return logs;
    }

    @Override
    public List<LogEntry> getAll() throws SQLException {
        String sql = "SELECT * FROM LogEntry ORDER BY time DESC";

        List<LogEntry> logs = new ArrayList<>();

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                logs.add(mapLogEntry(rs));
            }
        }

        return logs;
    }

    private LogEntry mapLogEntry(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int userId = rs.getInt("user_id");
        String action = rs.getString("action");
        String details = rs.getString("details");
        LocalDateTime time = rs.getTimestamp("time").toLocalDateTime();
        return new LogEntry(id, userId, action, details, time);
    }
}