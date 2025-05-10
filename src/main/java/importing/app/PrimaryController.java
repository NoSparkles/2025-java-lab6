package importing.app;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class PrimaryController {

    @FXML private TableView<DataModel> dataTable;
    @FXML private TableColumn<DataModel, Integer> colId;
    @FXML private TableColumn<DataModel, String> colFirstName, colLastName, colEmail, colGender, colCountry, colDomain, colBirthDate;
    
    @FXML private Label labelFile1;
    @FXML private Label labelFile2;
    @FXML private Label labelFile3;

    @FXML private Label loadedLabel;

    private final ObservableList<Integer> rowCounts = FXCollections.observableArrayList(0, 0, 0);

    @FXML private AnchorPane uiContainer;

    @FXML private DatePicker datePickerStart;
    @FXML private DatePicker datePickerEnd;

    @FXML private ChoiceBox<String> sortByChoiceBox;
    @FXML private CheckBox ascendingCheckBox;

    @FXML private VBox errorBox;
    private final Queue<Label> errorQueue = new ArrayDeque<>();

    private final ObservableList<DataModel> data = FXCollections.observableArrayList();
    private final ObservableList<DataModel> originalData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        labelFile1.textProperty().bind(Bindings.createStringBinding(() -> rowCounts.get(0) + " rows loaded", this.rowCounts));
        labelFile2.textProperty().bind(Bindings.createStringBinding(() -> rowCounts.get(1) + " rows loaded", this.rowCounts));
        labelFile3.textProperty().bind(Bindings.createStringBinding(() -> rowCounts.get(2) + " rows loaded", this.rowCounts));

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
        uiContainer.setDisable(true);

        // Initialize the choice box with sorting options
        sortByChoiceBox.getItems().addAll("ID", "First Name", "Last Name", "Email", "Gender", "Country", "Domain", "Birth Date");
        sortByChoiceBox.setValue("ID"); // Default selection
        ascendingCheckBox.setSelected(true); // Default to ascending order
    }

    @FXML
    private void loadData() {
        loadedLabel.setText("Loading data...");
        uiContainer.setDisable(true);
        DataLoader.loadDataFromFiles(this.data, this.rowCounts, this::queueError, this::onDataLoadComplete); // Pass error handling function
    }

    private void onDataLoadComplete() {
        loadedLabel.setText("Data loaded successfully!");
        this.originalData.setAll(this.data); // Store the original data for reset
        Platform.runLater(() -> uiContainer.setDisable(false)); // Re-enable UI when loading is complete
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
        LocalDate fromDate = datePickerStart.getValue();
        LocalDate toDate = datePickerEnd.getValue();

        System.out.println("Start Date: " + fromDate);
        System.out.println("End Date: " + toDate);

        if (fromDate == null || toDate == null) {
            this.showAlert("Warning", "Please select a start and end date.");
            return;
        }

        if (toDate.isBefore(fromDate) || toDate.isEqual(fromDate)) {
            showAlert("Warning", "End date must be after start date.");
            return;
        }

        List<DataModel> filtered = data.stream()
                .filter(entry -> {
                    try {
                        LocalDate birthDate = LocalDate.parse(entry.getBirthDate());
                        return (birthDate.isAfter(fromDate) && birthDate.isBefore(toDate));
                    } catch (Exception e) {
                        return false; // Skip invalid dates
                    }
                })
                .collect(Collectors.toList());

        data.setAll(filtered);
    }

    @FXML
    private void resetDateFilter() {
        this.datePickerStart.setValue(null); // Reset date pickers
        this.datePickerEnd.setValue(null);
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
        String sortBy = sortByChoiceBox.getValue();
        boolean ascending = ascendingCheckBox.isSelected();

        Comparator<DataModel> comparator;

        switch (sortBy) {
            case "ID":
                comparator = Comparator.comparingInt(DataModel::getId);
                break;
            case "First Name":
                comparator = Comparator.comparing(DataModel::getFirstName);
                break;
            case "Last Name":
                comparator = Comparator.comparing(DataModel::getLastName);
                break;
            case "Email":
                comparator = Comparator.comparing(DataModel::getEmail);
                break;
            case "Gender":
                comparator = Comparator.comparing(DataModel::getGender);
                break;
            case "Country":
                comparator = Comparator.comparing(DataModel::getCountry);
                break;
            case "Domain":
                comparator = Comparator.comparing(DataModel::getDomain);
                break;
            case "Birth Date":
                comparator = Comparator.comparing(dataModel -> {
                    try {
                        return LocalDate.parse(dataModel.getBirthDate());
                    } catch (Exception e) {
                        return LocalDate.MAX; // Handle invalid dates
                    }
                });
                break;
            default:
                return; // Exit early if no valid selection
        }

        if (!ascending) {
            comparator = comparator.reversed();
        }

        data.setAll(data.stream().sorted(comparator).collect(Collectors.toList()));
    }
}