package dk.siema.siemaexamproject.app;

import dk.siema.siemaexamproject.gui.util.SceneManager;
import dk.siema.siemaexamproject.gui.util.ViewFactory;

public class ApplicationServices {

private final ViewFactory viewFactory;
private final SceneManager sceneManager;

// logic services here


    public ApplicationServices() {
        // here instantiate all DAO classes fx IUserDAO userDAO = new UserDAO();

        // here set all logic fx this.authenticationLogic = new AuthenticationLogic();

        this.viewFactory= new ViewFactory(this);
        this.sceneManager = new SceneManager(viewFactory);
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }
    // here getters for all logic
}
