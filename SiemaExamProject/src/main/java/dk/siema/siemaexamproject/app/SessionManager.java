package dk.siema.siemaexamproject.app;

import dk.siema.siemaexamproject.be.User;

public class SessionManager {
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser=user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void clearSession()
    {
        currentUser=null;
    }
}
