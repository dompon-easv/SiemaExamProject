package dk.siema.siemaexamproject.app;

import dk.siema.siemaexamproject.bll.api.TiffService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApplicationServices {

    // here all logic fx     private final AuthenticationLogic authenticationLogic;
    private final ExecutorService executorService;
    private final TiffService tiffService;

    public ApplicationServices() {
        // here instantiate all DAO classes fx IUserDAO userDAO = new UserDAO();
        this.executorService = Executors.newFixedThreadPool(2);
        this.tiffService = new TiffService();

        // here set all logic fx this.authenticationLogic = new AuthenticationLogic();
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public TiffService getTiffService() {
        return tiffService;
    }

    public void shutdown() {
        executorService.shutdown();
    }
    // here getters for all logic
}
