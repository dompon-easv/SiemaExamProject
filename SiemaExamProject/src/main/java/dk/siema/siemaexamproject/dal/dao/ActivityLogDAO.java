package dk.siema.siemaexamproject.dal.dao;

import dk.siema.siemaexamproject.be.ActivityLog;
import dk.siema.siemaexamproject.be.enums.LogAction;
import dk.siema.siemaexamproject.dal.ConnectionManager;
import dk.siema.siemaexamproject.dal.exception.DalException;
import dk.siema.siemaexamproject.dal.interfaces.IActivityLogDAO;
import dk.siema.siemaexamproject.dal.util.BytesConverter;
import dk.siema.siemaexamproject.gui.ActivityLogsController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDAO implements IActivityLogDAO {

    @Override
    public void saveLogs(Connection con, List<ActivityLog> logs) throws DalException {
        if (logs == null || logs.isEmpty()) return;

        String logSql = "INSERT INTO ActivityLogs (user_id, file_id, action, details, timestamp) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement logStmt = con.prepareStatement(logSql)) {
            for (ActivityLog log : logs) {
                logStmt.setBytes(1, BytesConverter.uuidToBytes(log.getUserId()));
                logStmt.setBytes(2, BytesConverter.uuidToBytes(log.getFileId()));
                logStmt.setString(3, log.getAction().name());
                logStmt.setString(4, log.getDetails());
                logStmt.setTimestamp(5, java.sql.Timestamp.valueOf(log.getTime()));

                logStmt.addBatch();
            }
            logStmt.executeBatch();
        } catch (SQLException e) {
            throw new DalException("Error while saving logs into ActivityLogs", e);
        }
    }

    @Override
    public List<ActivityLog> getLogsFiltered(ActivityLogsController.FilterType type, String value) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT al.*, u.username FROM ActivityLogs al " +
                "JOIN Users u ON al.user_id = u.id ";

        switch (type) {
            case BOX -> sql += "JOIN FileEntities fe ON al.file_id = fe.reference_id " +
                    "JOIN Documents d ON fe.document_id = d.id WHERE d.box_id = ?";
            case DOCUMENT -> sql += "JOIN FileEntities fe ON al.file_id = fe.reference_id WHERE fe.document_id = ?";
            case FILE -> sql += "WHERE al.file_id = ?";
            case USER -> sql += "WHERE u.username LIKE ?";
        }

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Dynamic parameter setting based on type
            switch (type) {
                case USER ->
                        pstmt.setString(1, "%" + value + "%");

                case FILE ->
                    // Convert the String UUID from UI to bytes for the DB
                        pstmt.setBytes(1, BytesConverter.uuidToBytes(java.util.UUID.fromString(value)));

                case BOX, DOCUMENT ->
                    // These are likely INTs in your DB
                        pstmt.setInt(1, Integer.parseInt(value));
            }

            ResultSet rs = pstmt.executeQuery();
            // ... the rest of your while loop remains the same ...
            while (rs.next()) {
                ActivityLog log = new ActivityLog(
                        BytesConverter.bytesToUUID(rs.getBytes("user_id")),
                        BytesConverter.bytesToUUID(rs.getBytes("file_id")),
                        LogAction.valueOf(rs.getString("action")),
                        rs.getString("details"),
                        rs.getTimestamp("timestamp").toLocalDateTime()
                );
                log.setUsername(rs.getString("username")); // <--- Set the name here
                logs.add(log);
            }
        } catch (SQLException e) { e.printStackTrace();
        throw new DalException("Error while fetching logs from ActivityLogs", e); }
        return logs;
    }

}
