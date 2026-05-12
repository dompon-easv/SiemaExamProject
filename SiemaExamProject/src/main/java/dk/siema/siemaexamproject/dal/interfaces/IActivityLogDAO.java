package dk.siema.siemaexamproject.dal.interfaces;

import dk.siema.siemaexamproject.be.ActivityLog;
import dk.siema.siemaexamproject.dal.exception.DalException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IActivityLogDAO {
    void saveLogs(Connection con, List<ActivityLog> logs) throws DalException;
}
