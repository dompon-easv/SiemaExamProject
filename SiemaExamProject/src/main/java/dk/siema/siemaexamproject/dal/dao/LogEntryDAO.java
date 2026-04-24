/*package dk.siema.siemaexamproject.dal.dao;

import dk.siema.siemaexamproject.be.LogEntry;
import dk.siema.siemaexamproject.dal.ConnectionManager;
import dk.siema.siemaexamproject.dal.interfaces.ILogEntryDAO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LogEntryDAO implements ILogEntryDAO {

    @Override
    public LogEntry add(LogEntry log) throws SQLException {
        String sql = "INSERT INTO LogEntry (user_id, action, details, time) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, log.getUserId());
            stmt.setString(3, log.getDetails());
            stmt.setTimestamp(4, Timestamp.valueOf(log.getTime()));

            stmt.executeUpdate();

           // Get generated ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    log.setId(rs.getInt(1)); // assumes you have setId()
                }
            }

            return log; // return updated entity
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
        int fileId = rs.getInt("file_id");
        String action = rs.getString("action");
        String details = rs.getString("details");
        LocalDateTime time = rs.getTimestamp("time").toLocalDateTime();
        return new LogEntry(id, userId,fileId, action, details, time);
    }
}

 */