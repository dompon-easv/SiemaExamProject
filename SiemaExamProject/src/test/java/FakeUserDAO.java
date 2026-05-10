import dk.siema.siemaexamproject.be.ScanningProfile;
import dk.siema.siemaexamproject.be.User;
import dk.siema.siemaexamproject.dal.exception.DalException;
import dk.siema.siemaexamproject.dal.interfaces.IUserDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FakeUserDAO implements IUserDAO {

    @Override
    public User add(User entity) throws DalException {
        return entity;
    }

    @Override
    public void update(User entity) throws DalException {

    }

    @Override
    public void delete(UUID id) throws DalException {

    }

    @Override
    public User getById(UUID id) throws DalException {
        return null;
    }

    @Override
    public List<User> getAll() throws DalException {
        return new ArrayList<>();
    }

    @Override
    public User getByUsername(String username) throws DalException {
        return null;
    }

    @Override
    public void updatePassword(UUID id, String newHash) throws DalException {

    }

    @Override
    public List<ScanningProfile> getProfilesForUser(UUID id) throws DalException {
        return List.of();
    }

    @Override
    public void assignProfilesForUser(UUID id, int profileId) throws DalException {

    }

    @Override
    public void deleteProfilesFromUser(UUID id, int profileId) throws DalException {

    }
}
