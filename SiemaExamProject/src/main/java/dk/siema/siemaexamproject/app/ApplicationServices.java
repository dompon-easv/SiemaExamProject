package dk.siema.siemaexamproject.app;

import dk.siema.siemaexamproject.bll.api.ScannerService;
import dk.siema.siemaexamproject.bll.service.ClientProfileService;
import dk.siema.siemaexamproject.dal.dao.ClientDAO;
import dk.siema.siemaexamproject.dal.dao.ScanningProfileDAO;
import dk.siema.siemaexamproject.dal.dao.SettingDAO;
import dk.siema.siemaexamproject.dal.interfaces.IClientDAO;
import dk.siema.siemaexamproject.dal.interfaces.IScanningProfileDAO;
import dk.siema.siemaexamproject.dal.interfaces.ISettingDAO;
import dk.siema.siemaexamproject.gui.models.AdminModel;
import dk.siema.siemaexamproject.gui.models.ClientProfileModel;
import dk.siema.siemaexamproject.gui.models.ScannerModel;
import dk.siema.siemaexamproject.gui.models.MainModel;
import dk.siema.siemaexamproject.gui.util.SceneManager;
import dk.siema.siemaexamproject.gui.util.ViewFactory;
import dk.siema.siemaexamproject.bll.service.UserService;
import dk.siema.siemaexamproject.dal.dao.UserDAO;
import dk.siema.siemaexamproject.dal.interfaces.IUserDAO;

import dk.siema.siemaexamproject.bll.api.DocumentBuilderService;
import dk.siema.siemaexamproject.bll.api.TiffService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApplicationServices {

    private final ViewFactory viewFactory;
    private final SceneManager sceneManager;
    private final UserService userService;
    private final ClientProfileService clientProfileService;

    private final ExecutorService cpuExecutor;
    private final ExecutorService ioExecutor;
    private final TiffService tiffService;
    private final DocumentBuilderService documentBuilderService;
    private final ScannerService scannerService;


    private final MainModel mainModel;
    private final AdminModel adminModel;
    private final ScannerModel scannerModel;
    private final ClientProfileModel clientProfileModel;



// logic services here

    public ApplicationServices() {
        // here getters for all logic

        // here set all logic fx this.authenticationLogic = new AuthenticationLogic();
        this.viewFactory= new ViewFactory(this);
        this.sceneManager = new SceneManager(viewFactory);

        //CPU-bound tasks (barcode scanning)
        this.cpuExecutor = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()
        );

        //IO-bound tasks (image loading)
        this.ioExecutor = Executors.newCachedThreadPool();

        this.tiffService = new TiffService();
        this.documentBuilderService = new DocumentBuilderService();
        this.scannerService = new ScannerService(tiffService, documentBuilderService, cpuExecutor);

        /* User task*/
        IUserDAO userDAO = new UserDAO();
        this.userService = new UserService(userDAO);

        IClientDAO clientDAO = new ClientDAO();
        IScanningProfileDAO scanningProfileDAO = new ScanningProfileDAO();
        ISettingDAO settingDAO = new SettingDAO();
        this.clientProfileService = new ClientProfileService(clientDAO, scanningProfileDAO, settingDAO);




        this.mainModel = new MainModel();
        this.adminModel = new AdminModel(userService);
        this.scannerModel = new ScannerModel(ioExecutor, scannerService);
        this.clientProfileModel = new ClientProfileModel(clientProfileService);
    }

    public SceneManager getSceneManager() {return sceneManager;}

    public ViewFactory getViewFactory() {return viewFactory;}

    public ExecutorService getCpuExecutor() {return cpuExecutor;}
    public ExecutorService getIoExecutor() {return ioExecutor;}

    public TiffService getTiffService() {return tiffService;}

    public DocumentBuilderService getDocumentBuilderService() {return documentBuilderService;}

    public ScannerService getScannerService() {return scannerService;}

    public ScannerModel getScannerModel() {return scannerModel;}

    public void shutdown() {
        cpuExecutor.shutdown();
        ioExecutor.shutdown();

    }
    public UserService getUserService() {return userService;
    }
    public AdminModel getAdminModel() {return adminModel;
    }

    public ClientProfileModel getClientProfileModel() {
        return clientProfileModel;
    }
}