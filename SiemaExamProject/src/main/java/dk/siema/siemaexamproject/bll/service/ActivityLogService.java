package dk.siema.siemaexamproject.bll.service;

import dk.siema.siemaexamproject.be.ActivityLog;
import dk.siema.siemaexamproject.dal.dao.ActivityLogDAO;
import dk.siema.siemaexamproject.dal.interfaces.IActivityLogDAO;
import dk.siema.siemaexamproject.be.enums.FilterType;

import java.util.ArrayList;
import java.util.List;

public class ActivityLogService {
    private IActivityLogDAO logDAO;

    public ActivityLogService(){
        this.logDAO = new ActivityLogDAO();
    }

    public void setApplicationServices(IActivityLogDAO logDAO) {
        this.logDAO = logDAO;
    }


    public List<ActivityLog> getFilteredLogs(FilterType type, String value) {
        if (value == null || value.trim().isEmpty()) {
            // Business logic: if no value, maybe return all logs or an empty list
            return new ArrayList<>();
        }
        return logDAO.getLogsFiltered(type, value.trim());
    }

}