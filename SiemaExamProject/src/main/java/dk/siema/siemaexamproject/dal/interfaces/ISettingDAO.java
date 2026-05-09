package dk.siema.siemaexamproject.dal.interfaces;

import dk.siema.siemaexamproject.be.ProfileSetting;
import dk.siema.siemaexamproject.be.Setting;
import dk.siema.siemaexamproject.bll.exceptions.DalException;
import dk.siema.siemaexamproject.bll.exceptions.DataAccessException;

import java.sql.SQLException;
import java.util.List;

public interface ISettingDAO {

    List<Setting> getAllSettings() throws DalException;

    List<ProfileSetting> getSettingsForProfile(int profileId) throws DalException;
}
