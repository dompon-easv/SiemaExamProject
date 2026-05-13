package dk.siema.siemaexamproject.gui.models;

import dk.siema.siemaexamproject.be.ProfileSetting;
import dk.siema.siemaexamproject.be.ScanningProfile;
import dk.siema.siemaexamproject.be.User;
import dk.siema.siemaexamproject.bll.exceptions.AuthenticationException;
import dk.siema.siemaexamproject.bll.exceptions.BackendFailureException;
import dk.siema.siemaexamproject.bll.exceptions.ServiceException;
import dk.siema.siemaexamproject.bll.exceptions.ValidationException;
import dk.siema.siemaexamproject.bll.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.UUID;

public class AdminModel {

    private final UserService userService;

    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final ObservableList<ScanningProfile> profilesForUser = FXCollections.observableArrayList();

    public AdminModel(UserService userService) {
        this.userService = userService;
    }

    // expose observable list to UI
    public ObservableList<User> getUsers() {
        return users;
    }

    /*load all users*/
    public void loadUsers() throws BackendFailureException {
        users.setAll(userService.getAllUsers());
    }

    // create user
    public void createUser(User user) throws BackendFailureException, ValidationException {
        userService.createUser(user);
    }

    /*update user*/
    public void updateUser(User user) throws BackendFailureException, ValidationException {
        userService.updateUser(user);

        // force UI refresh (JavaFX needs this sometimes)
        int index = users.indexOf(user);
        if (index >= 0) {
            users.set(index, user);
        }
    }

    /*delete user*/
    public void deleteUser(User user) throws BackendFailureException, ValidationException {
        userService.deleteUser(user.getId());
        users.remove(user);
    }

    /*update password*/
    public void updatePassword(User user, String newPassword) throws ServiceException {
        userService.updatePassword(user.getId(), newPassword);
    }
    public User authenticate(String username, String password) throws BackendFailureException, ValidationException, AuthenticationException {

        return userService.authenticate(username, password);
    }

    public void loadProfilesForUser(UUID id) throws BackendFailureException {
        profilesForUser.clear();
        List<ScanningProfile> profiles = userService.getProfilesForUser(id);
        // Settings are already loaded by UserService now
        profilesForUser.setAll(profiles);

        // Debug output
        for (ScanningProfile profile : profiles) {
            System.out.println("Profile: " + profile.getName() +
                    ", Settings loaded: " + (profile.getProfileSettings() != null ? profile.getProfileSettings().size() : 0));
        }
    }

    public ObservableList<ScanningProfile> getProfilesForUser()  {
        return profilesForUser;
    }

    public void assignProfilesForUser(UUID id, int profileID) throws BackendFailureException, ValidationException {
        userService.assignProfilesForUser(id, profileID);
    }

    public void deleteProfilesForUser(UUID id, int profileID) throws BackendFailureException, ValidationException {
        userService.deleteProfilesFromUser(id, profileID);
    }
}