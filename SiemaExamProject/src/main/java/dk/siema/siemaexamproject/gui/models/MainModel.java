package dk.siema.siemaexamproject.gui.models;

import dk.siema.siemaexamproject.be.User;
import dk.siema.siemaexamproject.be.enums.UserRole;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class MainModel {

    private final ObjectProperty<User> currentUser = new SimpleObjectProperty<>();

    public void setCurrentUser(User user) {
        currentUser.set(user);
    }

    public User getCurrentUser() {
        return currentUser.get();
    }

    /* --- PROPERTY (for binding later if needed) ---*/
    public ObjectProperty<User> currentUserProperty() {
        return currentUser;
    }

    /* --- ROLE CHECK (We might need it IDK) ---*/
    public boolean isAdmin() {
        return currentUser.get() != null &&
                currentUser.get().getRole() == UserRole.ADMIN;
    }

    public boolean isEmployee() {
        return currentUser.get() != null &&
                currentUser.get().getRole() == UserRole.EMPLOYEE;
    }

    /* --- LOG-OUT!...drum roles joke ---*/
    public void logout() {
        currentUser.set(null);
    }
}