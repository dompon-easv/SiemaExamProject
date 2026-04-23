package dk.siema.siemaexamproject.app;

import dk.siema.siemaexamproject.gui.models.AdminModel;
import dk.siema.siemaexamproject.gui.models.ScannerModel;
import dk.siema.siemaexamproject.gui.models.SessionModel;
import dk.siema.siemaexamproject.gui.util.SceneManager;
import dk.siema.siemaexamproject.gui.util.ViewFactory;

public class ApplicationServices {

private final ViewFactory viewFactory;
private final SceneManager sceneManager;

private final SessionModel sessionModel;
private final AdminModel adminModel;
private final ScannerModel scannerModel;



// logic services here


    public ApplicationServices() {
        // here instantiate all DAO classes fx IUserDAO userDAO = new UserDAO();

        // here set all logic fx this.authenticationService = new AuthenticationService();

        this.viewFactory= new ViewFactory(this);
        this.sceneManager = new SceneManager(viewFactory);





        this.sessionModel = new SessionModel();
        this.adminModel = new AdminModel();
        this.scannerModel = new ScannerModel();
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }
    // here getters for all logic
}
