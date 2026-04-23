package dk.siema.siemaexamproject.dal.interfaces;

import dk.siema.siemaexamproject.be.LogEntry;
import java.sql.SQLException;
import java.util.List;

public interface ILogEntryDAO extends IRepository<LogEntry> {
    List<LogEntry> getLogsByUser(int userId) throws SQLException;
}
/* the current interface is a placeholder,
* for us to refactor it ASAP
* to be implemented as a single repo-interface
* for all the DAOs this time around :) */
