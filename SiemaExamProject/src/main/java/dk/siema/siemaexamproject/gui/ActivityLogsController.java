package dk.siema.siemaexamproject.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ActivityLogsController {

    @FXML
    private TableView<LogRow> logsTable;
    @FXML private TableColumn<LogRow, String> timestampColumn;
    @FXML private TableColumn<LogRow, String> userColumn;
    @FXML private TableColumn<LogRow, String> actionColumn;
    @FXML private TableColumn<LogRow, String> detailsColumn;
    @FXML private TextField searchField;

    @FXML
    private void initialize() {
        timestampColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().timestamp()));
        userColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().user()));
        actionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().action()));
        detailsColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().details()));

        FilteredList<LogRow> filteredList = new FilteredList<>(FXCollections.observableArrayList(
                new LogRow("2026-04-22 10:30:15", "scanner_user", "Created case", "Invoice_2026_001.tiff"),
                new LogRow("2026-04-22 11:15:42", "scanner_user", "Scanned document", "Receipt_Store_A.tiff"),
                new LogRow("2026-04-22 14:20:33", "qa_user", "Started QA", "Contract_Draft.tiff"),
                new LogRow("2026-04-22 15:05:12", "admin", "Created user", "qa_user")
        ));

        searchField.textProperty().addListener((observable, oldValue, newValue) -> filteredList.setPredicate(logRow -> {
            if (newValue == null || newValue.isBlank()) {
                return true;
            }
            String filter = newValue.toLowerCase();
            return logRow.timestamp().toLowerCase().contains(filter)
                    || logRow.user().toLowerCase().contains(filter)
                    || logRow.action().toLowerCase().contains(filter)
                    || logRow.details().toLowerCase().contains(filter);
        }));

        logsTable.setItems(filteredList);
    }

    public record LogRow(String timestamp, String user, String action, String details) {}


}
