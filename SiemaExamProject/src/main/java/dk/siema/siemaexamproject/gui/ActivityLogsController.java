package dk.siema.siemaexamproject.gui;

import dk.siema.siemaexamproject.app.ApplicationServices;
import dk.siema.siemaexamproject.app.ApplicationServicesAware;
import dk.siema.siemaexamproject.be.ActivityLog;
import dk.siema.siemaexamproject.be.enums.FilterType;
import dk.siema.siemaexamproject.gui.models.ScannerModel;
import dk.siema.siemaexamproject.gui.util.AlertHelper;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;


public class ActivityLogsController implements ApplicationServicesAware {

    @FXML private TextField boxFilterField;
    @FXML private TextField docFilterField;
    @FXML private TextField fileFilterField;
    @FXML private TextField userFilterField;
    @FXML
    private TableView<ActivityLog> logsTable;
    @FXML private TableColumn<ActivityLog, String> timestampColumn;
    @FXML private TableColumn<ActivityLog, String> userColumn;
    @FXML private TableColumn<ActivityLog, String> actionColumn;
    @FXML private TableColumn<ActivityLog, String> detailsColumn;
    @FXML private TableColumn<ActivityLog, String> fileIdColumn;

    @FXML private TextField searchField;


    private ScannerModel scannerModel;
    @Override
    public void setApplicationServices(ApplicationServices services) {
        this.scannerModel = services.getScannerModel();
    }

    public record LogRow(String timestamp, String user, String fileId, String action, String details) {}

    @FXML
    private void initialize() {
        userColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
        detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));
        fileIdColumn.setCellValueFactory(new PropertyValueFactory<>("fileId"));

    }

    @FXML
    public void handleApplyFilters(ActionEvent event) {
        System.out.println("button apply filters");
        String box = boxFilterField.getText().trim();
        String doc = docFilterField.getText().trim();
        String user = userFilterField.getText().trim();
        String file = fileFilterField.getText().trim();

        FilterType selectedType = null;
        String filterValue = "";

        if(!box.isEmpty()) {
            selectedType = FilterType.BOX;
            filterValue = box;
        } else if(!doc.isEmpty()) {
            selectedType = FilterType.DOCUMENT;
            filterValue = doc;
        } else if(!user.isEmpty()) {
            selectedType = FilterType.USER;
            filterValue = user;
            System.out.println("filtering for user" + user);
        } else if(!file.isEmpty()) {
            selectedType = FilterType.FILE;
            filterValue = file;
        }
        if(selectedType == null){
            AlertHelper.error("Filter error", "Please select one valid filter type");
            return;
        }
        scannerModel.loadLogs(selectedType, filterValue);
        logsTable.setItems(scannerModel.getLogEntries());
    }

}
