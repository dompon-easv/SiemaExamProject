package dk.siema.siemaexamproject.dal.interfaces;

import dk.siema.siemaexamproject.be.ActivityLog;
import dk.siema.siemaexamproject.dal.exception.DalException;
import dk.siema.siemaexamproject.gui.ActivityLogsController;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IActivityLogDAO {
    void saveLogs(Connection con, List<ActivityLog> logs) throws DalException;
    void createLog(ActivityLog log) throws DalException;
    List<ActivityLog> getLogsFiltered(ActivityLogsController.FilterType type, String value) throws DalException;
}
