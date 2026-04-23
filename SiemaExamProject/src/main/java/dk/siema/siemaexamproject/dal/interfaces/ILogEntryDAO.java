package dk.siema.siemaexamproject.dal.interfaces;

import dk.siema.siemaexamproject.be.LogEntry;
import java.sql.SQLException;
import java.util.List;

public interface ILogEntryDAO extends IRepository<LogEntry> {
    List<LogEntry> getLogsByUser(int userId) throws SQLException;
}
/* Use ONE generic base interface (IRepository), and extend it per entity :) */
