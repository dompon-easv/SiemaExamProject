package dk.siema.siemaexamproject.bll.service;

import com.github.f4b6a3.uuid.UuidCreator;
import dk.siema.siemaexamproject.be.ProfileSetting;
import dk.siema.siemaexamproject.be.ScanningProfile;
import dk.siema.siemaexamproject.be.User;
import dk.siema.siemaexamproject.bll.exceptions.*;
import dk.siema.siemaexamproject.dal.interfaces.IUserDAO;
import dk.siema.siemaexamproject.bll.util.PasswordUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class UserService {

    private final IUserDAO userDAO;

    private final ClientProfileService clientProfileService;

    public UserService(IUserDAO userDAO, ClientProfileService clientProfileService) {
        this.userDAO = userDAO;
        this.clientProfileService = clientProfileService;
    }

    // CREATE
    public User createUser(User user) throws ServiceException {

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
} catch (Exception e) {throw new ServiceException("Error processing password", e);
}

        try {
            return userDAO.add(user);
        } catch (DalException e) {
            throw new DataAccessException("Error creating user", e);
        }
    }

    // READ
    public List<User> getAllUsers() throws ServiceException {
        try {
            return userDAO.getAll();
        } catch (DalException e) {
            throw new DataAccessException("Error fetching users", e);
        }
    }

    public User getUserById(UUID id) throws ServiceException {
        try {
            return userDAO.getById(id);
        } catch (DalException e) {
            throw new DataAccessException("Error fetching user", e);
        }
    }

    // UPDATE (non-sensitive fields)
    public void updateUser(User user) throws ServiceException {

        if (user.getId() == null) {
            throw new ValidationException("User ID is required");
        }

        try {
            userDAO.update(user);
        } catch (DalException e) {
            throw new DataAccessException("Error updating user", e);
        }
    }

    // DELETE
    public void deleteUser(UUID id) throws ServiceException {

        if (id == null) {
            throw new ValidationException("Invalid user ID");
        }

        try {
            userDAO.delete(id);
        } catch (DalException e) {
            throw new DataAccessException("Error deleting user", e);
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
            throw new DataAccessException("Error updating password", e);
        }
    }

    // AUTHENTICATION
    public User authenticate(String username, String password) throws ServiceException {

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
                throw new ServiceException("Error verifying password", e);
            }

            if (!valid) {
                throw new AuthenticationException("Invalid password");
            }

            return user;

        } catch (DalException e) {
            throw new DataAccessException("Error during authentication", e);
        }
    }

    public List<ScanningProfile> getProfilesForUser(UUID id) throws ServiceException {
        try {
            List<ScanningProfile> profiles = userDAO.getProfilesForUser(id);

            // Load settings for each profile
            for (ScanningProfile profile : profiles) {
                List<ProfileSetting> settings = clientProfileService.getSettingsForProfile(profile.getId());
                profile.setSettings(settings);
                System.out.println("Loaded " + settings.size() + " settings for profile: " + profile.getName());
            }

            return profiles;
        } catch (DalException e) {
            throw new DataAccessException("Error fetching profiles", e);
        }
    }

    public List<ProfileSetting> getProfileSettings(int profileId) throws ServiceException {
        return clientProfileService.getSettingsForProfile(profileId);
    }

    public void assignProfilesForUser(UUID id, int profileId) throws ServiceException {
        if (id == null) {
            throw new ValidationException("User ID is required");
        }
        if (profileId == 0) {
            throw new ValidationException("Profile ID is required");
        }
       try{
           userDAO.assignProfilesForUser(id, profileId);
       }catch(DalException e)
       { throw new ServiceException("Error assigning profiles", e);}
    }
    public void deleteProfilesFromUser(UUID id, int profileId) throws ServiceException {
        if (id == null) {
            throw new ValidationException("Nothing to delete");
        }
        if (profileId == 0) {
            throw new ValidationException("Profile ID is required");
        }
        try{
            userDAO.deleteProfilesFromUser(id, profileId);
        } catch (DalException e) {
            throw new DataAccessException("Error deleting profiles", e);
        }
    }
}