package dk.siema.siemaexamproject.dal.interfaces;

import dk.siema.siemaexamproject.be.Setting;

import java.sql.SQLException;
import java.util.List;

public interface ISettingDAO {

    List<Setting> getAllSettings() throws SQLException;
}
