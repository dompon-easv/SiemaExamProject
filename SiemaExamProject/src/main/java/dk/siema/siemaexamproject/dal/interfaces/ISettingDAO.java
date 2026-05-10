package dk.siema.siemaexamproject.dal.interfaces;

import dk.siema.siemaexamproject.be.ProfileSetting;
import dk.siema.siemaexamproject.be.Setting;
import dk.siema.siemaexamproject.dal.exception.DalException;

import java.util.List;

public interface ISettingDAO {

    List<Setting> getAllSettings() throws DalException;

    List<ProfileSetting> getSettingsForProfile(int profileId) throws DalException;
}
