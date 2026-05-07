import dk.siema.siemaexamproject.be.ScanningProfile;
import dk.siema.siemaexamproject.be.User;
import dk.siema.siemaexamproject.dal.interfaces.IUserDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FakeUserDAO implements IUserDAO {

    @Override
    public User add(User entity) throws SQLException {
        return entity;
    }

    @Override
    public void update(User entity) throws SQLException {

    }

    @Override
    public void delete(UUID id) throws SQLException {

    }

    @Override
    public User getById(UUID id) throws SQLException {
        return null;
    }

    @Override
    public List<User> getAll() throws SQLException {
        return new ArrayList<>();
    }

    @Override
    public User getByUsername(String username) throws SQLException {
        return null;
    }

    @Override
    public void updatePassword(UUID id, String newHash) throws SQLException {

    }

    @Override
    public List<ScanningProfile> getProfilesForUser(UUID id) throws SQLException {
        return List.of();
    }

    @Override
    public void assignProfilesForUser(UUID id, int profileId) throws SQLException {

    }

    @Override
    public void deleteProfilesFromUser(UUID id, int profileId) throws SQLException {

    }
}
