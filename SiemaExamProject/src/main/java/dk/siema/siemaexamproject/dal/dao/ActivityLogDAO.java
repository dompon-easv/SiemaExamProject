package dk.siema.siemaexamproject.dal.dao;

import dk.siema.siemaexamproject.be.ActivityLog;
import dk.siema.siemaexamproject.dal.exception.DalException;
import dk.siema.siemaexamproject.dal.interfaces.IActivityLogDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ActivityLogDAO implements IActivityLogDAO {

    @Override
    public void saveLogs(Connection con, List<ActivityLog> logs) throws DalException {
        if (logs == null || logs.isEmpty()) return;

        String logSql = "INSERT INTO ActivityLogs (userId, fileId, action, details, timestamp) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement logStmt = con.prepareStatement(logSql)) {
            for (ActivityLog log : logs) {
                logStmt.setObject(1, log.getUserId());
                logStmt.setObject(2, log.getFileId());
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
}
