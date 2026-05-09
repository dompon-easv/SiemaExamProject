package dk.siema.siemaexamproject.dal.dao;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.siema.siemaexamproject.be.ProfileSetting;
import dk.siema.siemaexamproject.be.Setting;
import dk.siema.siemaexamproject.bll.exceptions.DalException;
import dk.siema.siemaexamproject.bll.exceptions.DataAccessException;
import dk.siema.siemaexamproject.dal.ConnectionManager;
import dk.siema.siemaexamproject.dal.interfaces.ISettingDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SettingDAO implements ISettingDAO {
    @Override
    public List<Setting> getAllSettings() throws DalException {
        String sql = "SELECT * FROM settings";
        List<Setting> settings = new ArrayList<>();

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String defaultValue = rs.getString("default_value");

                settings.add(new Setting(id, name, defaultValue));
            }

        } catch (SQLException e) {
            throw new DalException(e.getMessage());
        }
        return settings;
    }

    // In SettingDAO.java - add this method
    @Override
    public List<ProfileSetting> getSettingsForProfile(int profileId) throws DalException {
        String sql = "SELECT ps.setting_id, ps.value, s.name " +
                "FROM ProfileSettings ps " +
                "JOIN Settings s ON ps.setting_id = s.id " +
                "WHERE ps.profile_id = ?";

        List<ProfileSetting> settings = new ArrayList<>();

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, profileId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Setting setting = new Setting(rs.getInt("setting_id"), rs.getString("name"));
                    ProfileSetting ps = new ProfileSetting(setting, rs.getString("value"));
                    settings.add(ps);
                }
            }
        } catch (SQLException e) {
            throw new DalException("Error getting settings for profile " + profileId, e);
        }

        return settings;
    }
}
