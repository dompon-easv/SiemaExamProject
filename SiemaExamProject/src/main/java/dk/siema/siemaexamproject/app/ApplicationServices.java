package dk.siema.siemaexamproject.app;

import dk.siema.siemaexamproject.gui.util.SceneManager;
import dk.siema.siemaexamproject.gui.util.ViewFactory;

import dk.siema.siemaexamproject.bll.api.DocumentBuilderService;
import dk.siema.siemaexamproject.bll.api.TiffService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApplicationServices {

private final ViewFactory viewFactory;
private final SceneManager sceneManager;
private final ExecutorService executorService;
private final TiffService tiffService;
private final DocumentBuilderService documentBuilderService;

// logic services here

    public ApplicationServices() {
        // here getters for all logic

        // here set all logic fx this.authenticationLogic = new AuthenticationLogic();
        this.viewFactory= new ViewFactory(this);
        this.sceneManager = new SceneManager(viewFactory);
        this.executorService = Executors.newFixedThreadPool(2);
        this.tiffService = new TiffService();
        this.documentBuilderService = new DocumentBuilderService();
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public TiffService getTiffService() {
        return tiffService;
    }

    public DocumentBuilderService getDocumentBuilderService() {
        return documentBuilderService;
    }

    public void shutdown() {
        executorService.shutdown();
    }
}