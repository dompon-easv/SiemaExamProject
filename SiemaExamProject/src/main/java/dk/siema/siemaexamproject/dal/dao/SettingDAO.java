package dk.siema.siemaexamproject.dal.dao;

import dk.siema.siemaexamproject.be.Setting;
import dk.siema.siemaexamproject.dal.exception.DalException;
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
}
