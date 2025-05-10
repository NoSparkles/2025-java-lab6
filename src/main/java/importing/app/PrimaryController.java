package importing.app;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class PrimaryController {

    @FXML private TableView<DataModel> dataTable;
    @FXML private TableColumn<DataModel, Integer> colId;
    @FXML private TableColumn<DataModel, String> colFirstName, colLastName, colEmail, colGender, colCountry, colDomain, colBirthDate;
    
    
    @FXML private DatePicker datePickerStart;
    @FXML private DatePicker datePickerEnd;

    @FXML private CheckBox chkId;
    @FXML private CheckBox chkIdReverse;

    @FXML private CheckBox chkFirstName;
    @FXML private CheckBox chkFirstNameReverse;

    @FXML private CheckBox chkLastName;
    @FXML private CheckBox chkLastNameReverse;

    @FXML private CheckBox chkEmail;
    @FXML private CheckBox chkEmailReverse;

    @FXML private CheckBox chkGender;
    @FXML private CheckBox chkGenderReverse;

    @FXML private CheckBox chkCountry;
    @FXML private CheckBox chkCountryReverse;

    @FXML private CheckBox chkDomain;
    @FXML private CheckBox chkDomainReverse;

    @FXML private VBox errorBox;
    private final Queue<Label> errorQueue = new ArrayDeque<>();

    private final ObservableList<DataModel> data = FXCollections.observableArrayList();
    private final ObservableList<DataModel> originalData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Bind table columns correctly with property methods
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colFirstName.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        colLastName.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        colEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        colGender.setCellValueFactory(cellData -> cellData.getValue().genderProperty());
        colCountry.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        colDomain.setCellValueFactory(cellData -> cellData.getValue().domainProperty());
        colBirthDate.setCellValueFactory(cellData -> cellData.getValue().birthDateProperty());

        // Ensure the TableView uses the correct ObservableList
        dataTable.setItems(this.data);
    }

    @FXML
    private void loadData() {
        DataLoader.loadDataFromFiles(this.data, this::queueError); // Pass error handling function
        originalData.clear();
        originalData.addAll(this.data);
    }

    private void queueError(String errorMessage) {
        Platform.runLater(() -> {
            Label errorLabel = new Label(errorMessage);
            errorBox.getChildren().add(0, errorLabel); // Add new error on top
            errorQueue.offer(errorLabel); // Enqueue the new error

            if (errorQueue.size() > 5) {
                Label oldestError = errorQueue.poll(); // Remove the oldest error
                errorBox.getChildren().remove(oldestError);
            }
        });
    }

    @FXML
    private void filterByDate() {
        LocalDate nuoDate = datePickerStart.getValue();
        LocalDate ikiDate = datePickerEnd.getValue();

        if (nuoDate == null || ikiDate == null) {
            showAlert("Warning", "Check the date fields!");
            return;
        }

        List<DataModel> filtered = data.stream()
                .filter(entry -> LocalDate.parse(entry.getBirthDate()).isAfter(nuoDate) &&
                                 LocalDate.parse(entry.getBirthDate()).isBefore(ikiDate))
                .collect(Collectors.toList());

        data.setAll(filtered);
    }

    @FXML
    private void resetDateFilter() {
        data.setAll(this.originalData); // Restore the full dataset
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void applySort() {
        Comparator<DataModel> comparator = Comparator.comparing(DataModel::getId); // Default sorting by ID

        if (chkId.isSelected()) {
            comparator = chkIdReverse.isSelected()
                    ? comparator.thenComparing(Comparator.comparingInt(DataModel::getId).reversed())
                    : comparator.thenComparing(Comparator.comparingInt(DataModel::getId));
        }
        if (chkFirstName.isSelected()) {
            comparator = chkFirstNameReverse.isSelected()
                    ? comparator.thenComparing(Comparator.comparing(DataModel::getFirstName).reversed())
                    : comparator.thenComparing(Comparator.comparing(DataModel::getFirstName));
        }
        if (chkLastName.isSelected()) {
            comparator = chkLastNameReverse.isSelected()
                    ? comparator.thenComparing(Comparator.comparing(DataModel::getLastName).reversed())
                    : comparator.thenComparing(Comparator.comparing(DataModel::getLastName));
        }
        if (chkEmail.isSelected()) {
            comparator = chkEmailReverse.isSelected()
                    ? comparator.thenComparing(Comparator.comparing(DataModel::getEmail).reversed())
                    : comparator.thenComparing(Comparator.comparing(DataModel::getEmail));
        }
        if (chkGender.isSelected()) {
            comparator = chkGenderReverse.isSelected()
                    ? comparator.thenComparing(Comparator.comparing(DataModel::getGender).reversed())
                    : comparator.thenComparing(Comparator.comparing(DataModel::getGender));
        }
        if (chkCountry.isSelected()) {
            comparator = chkCountryReverse.isSelected()
                    ? comparator.thenComparing(Comparator.comparing(DataModel::getCountry).reversed())
                    : comparator.thenComparing(Comparator.comparing(DataModel::getCountry));
        }
        if (chkDomain.isSelected()) {
            comparator = chkDomainReverse.isSelected()
                    ? comparator.thenComparing(Comparator.comparing(DataModel::getDomain).reversed())
                    : comparator.thenComparing(Comparator.comparing(DataModel::getDomain));
        }

        // Apply sorting
        List<DataModel> sorted = data.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
        data.setAll(sorted);
    }
}