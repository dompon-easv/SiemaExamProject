package dk.siema.siemaexamproject.gui.models;

import dk.siema.siemaexamproject.be.User;
import dk.siema.siemaexamproject.bll.exceptions.ServiceException;
import dk.siema.siemaexamproject.bll.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AdminModel {

    private final UserService userService;

    private final ObservableList<User> users = FXCollections.observableArrayList();

    public AdminModel(UserService userService) {
        this.userService = userService;
    }

    // expose observable list to UI
    public ObservableList<User> getUsers() {
        return users;
    }

    /*load all users*/
    public void loadUsers() throws ServiceException {
        users.setAll(userService.getAllUsers());
    }

    // create user
    public void createUser(User user) throws ServiceException {
        userService.createUser(user);
        users.add(user); // immediate UI update (no reload)
    }

     /*update user*/
    public void updateUser(User user) throws ServiceException {
        userService.updateUser(user);

        // force UI refresh (JavaFX needs this sometimes)
        int index = users.indexOf(user);
        if (index >= 0) {
            users.set(index, user);
        }
    }

    /*delete user*/
    public void deleteUser(User user) throws ServiceException {
        userService.deleteUser(user.getId());
        users.remove(user);
    }

    /*update password*/
    public void updatePassword(User user, String newPassword) throws ServiceException {
        userService.updatePassword(user.getId(), newPassword);
    }
}