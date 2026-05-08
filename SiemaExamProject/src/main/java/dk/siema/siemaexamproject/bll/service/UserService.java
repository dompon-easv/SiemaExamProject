package dk.siema.siemaexamproject.bll.service;

import com.github.f4b6a3.uuid.UuidCreator;
import dk.siema.siemaexamproject.be.ScanningProfile;
import dk.siema.siemaexamproject.be.User;
import dk.siema.siemaexamproject.bll.exceptions.*;
import dk.siema.siemaexamproject.dal.exception.DalException;
import dk.siema.siemaexamproject.dal.interfaces.IUserDAO;
import dk.siema.siemaexamproject.bll.util.PasswordUtil;
import dk.siema.siemaexamproject.gui.util.AlertHelper;

import java.util.List;
import java.util.UUID;

public class UserService {

    private final IUserDAO userDAO;

    public UserService(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // CREATE
    public User createUser(User user) throws BackendFailureException, ValidationException {

        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new ValidationException("Username is required");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Email is required");
        }

        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            throw new ValidationException("Password is required");
        }

        UUID id = UuidCreator.getTimeOrderedEpoch();
        user.setId(id);
try {
    String hashed = PasswordUtil.hashPassword(user.getPasswordHash());
    user.changePassword(hashed);
} catch (Exception e) {throw new ValidationException("Error processing password");
}

        try {
            return userDAO.add(user);
        } catch (DalException e) {
            throw new BackendFailureException("Error creating user");
        }
    }

    // READ
    public List<User> getAllUsers() throws BackendFailureException {
        try {
            return userDAO.getAll();
        } catch (DalException e) {
            throw new BackendFailureException("Error fetching users");
        }
    }

    public User getUserById(UUID id) throws BackendFailureException {
        try {
            return userDAO.getById(id);
        } catch (DalException e) {
            throw new BackendFailureException("Error fetching user");
        }
    }

    // UPDATE (non-sensitive fields)
    public void updateUser(User user) throws BackendFailureException, ValidationException {

        if (user.getId() == null) {
             throw new ValidationException ("User ID is required");
        }

        try {
            userDAO.update(user);
        } catch (DalException e) {
            throw new BackendFailureException("Error updating user");
        }
    }

    // DELETE
    public void deleteUser(UUID id) throws BackendFailureException, ValidationException {

        if (id == null) {
            throw new ValidationException("Invalid user ID");
        }

        try {
            userDAO.delete(id);
        } catch (DalException e) {
            throw new BackendFailureException("Error deleting user");
        }
    }

    // PASSWORD UPDATE IKWIM
    public void updatePassword(UUID id, String newPassword) throws ServiceException {

        if (id == null) {
            throw new ValidationException("User ID is required");
        }

        if (newPassword == null || newPassword.isBlank()) {
            throw new ValidationException("Password cannot be empty");
        }

        String hashed;
        try {
            hashed = PasswordUtil.hashPassword(newPassword);
        } catch (Exception e) {throw new ServiceException("Error processing password", e);}

        try {
            userDAO.updatePassword(id, hashed);
        } catch (DalException e) {
            throw new BackendFailureException("Error updating password");
        }
    }

    // AUTHENTICATION
    public User authenticate(String username, String password) throws BackendFailureException, ValidationException,AuthenticationException {

        if (username == null || username.isBlank()) {
            throw new ValidationException("Username is required");
        }

        if (password == null || password.isBlank()) {
            throw new ValidationException("Password is required");
        }

        try {
            User user = userDAO.getByUsername(username);

            if (user == null) {
                throw new AuthenticationException("User not found");
            }

            boolean valid;
            try {
                valid = PasswordUtil.verifyPassword(password, user.getPasswordHash());
            } catch (Exception e) {
                throw new BackendFailureException("Error verifying password");
            }

            if (!valid) {
                throw new AuthenticationException("Invalid password");
            }

            return user;

        } catch (DalException e) {
            throw new BackendFailureException("Error during authentication");
        }
    }

    public List<ScanningProfile> getProfilesForUser(UUID id) throws BackendFailureException {
        try {
            return userDAO.getProfilesForUser(id);
        } catch (DalException e) {
            throw new BackendFailureException("Error fetching profiles");
        }
    }
    public void assignProfilesForUser(UUID id, int profileId) throws BackendFailureException, ValidationException {
        if (id == null) {
            throw new ValidationException("User ID is required");
        }
        if (profileId == 0) {
            throw new ValidationException("Profile ID is required");
        }
       try{
           userDAO.assignProfilesForUser(id, profileId);
       }catch(DalException e)
       { throw new BackendFailureException("Error assigning profiles");}
    }
    public void deleteProfilesFromUser(UUID id, int profileId) throws BackendFailureException, ValidationException {
        if (id == null) {
            throw new ValidationException("Nothing to delete");
        }
        if (profileId == 0) {
            throw new ValidationException("Profile ID is required");
        }
        try{
            userDAO.deleteProfilesFromUser(id, profileId);
        } catch (DalException e) {
            throw new BackendFailureException("Error deleting profiles");
        }
    }
}