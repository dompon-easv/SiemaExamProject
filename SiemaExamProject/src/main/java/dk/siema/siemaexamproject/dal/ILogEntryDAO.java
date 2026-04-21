package dk.siema.siemaexamproject.dal;

import dk.siema.siemaexamproject.be.LogEntry;
import java.sql.SQLException;
import java.util.List;

public interface ILogEntryDAO {
    void createLog(LogEntry log) throws SQLException;
    List<LogEntry> getLogsByUser(int userId) throws SQLException;
    List<LogEntry> getAllLogs() throws SQLException;
}
/* the curent interface is a placeholder,
* for us to refactor it ASAP
* to be implemented as a single repo-interface
* for all the DAOs this time around :) */
