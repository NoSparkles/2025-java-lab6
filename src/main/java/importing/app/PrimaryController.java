package importing.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class PrimaryController {

    @FXML private TableView<DataModel> dataTable;
    @FXML private TableColumn<DataModel, Integer> colId;
    @FXML private TableColumn<DataModel, String> colFirstName, colLastName, colEmail, colGender, colCountry, colDomain, colBirthDate;
    
    @FXML private Button btnLoad, btnFilterDate, btnSortName;

    private final ObservableList<DataModel> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Bind table columns
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colFirstName.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        colLastName.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        colEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        colGender.setCellValueFactory(cellData -> cellData.getValue().genderProperty());
        colCountry.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        colDomain.setCellValueFactory(cellData -> cellData.getValue().domainProperty());
        colBirthDate.setCellValueFactory(cellData -> cellData.getValue().birthDateProperty());

        dataTable.setItems(this.data);
    }

    @FXML
    private void loadData() {
        DataLoader.loadDataFromFiles(this.data); // Now updates the table directly
    }
}