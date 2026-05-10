package dk.siema.siemaexamproject.dal.interfaces;

import dk.siema.siemaexamproject.be.ScanningProfile;
import dk.siema.siemaexamproject.be.User;
import dk.siema.siemaexamproject.dal.exception.DalException;

import java.util.List;
import java.util.UUID;

public interface IUserDAO {

    User add(User entity) throws DalException;

    void update(User entity) throws DalException;

    void delete(UUID id) throws DalException;

    User getById(UUID id) throws DalException;

    List<User> getAll() throws DalException;

    User getByUsername(String username) throws DalException;

    void updatePassword(UUID id, String newHash) throws DalException;

    List<ScanningProfile> getProfilesForUser(UUID id) throws DalException;

    void assignProfilesForUser(UUID id, int profileId) throws DalException;

    void deleteProfilesFromUser(UUID id, int profileId) throws DalException;
}