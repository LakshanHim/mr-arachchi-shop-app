package lk.Arachchi.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lk.Arachchi.model.PackageModel;
import lk.Arachchi.tm.PackageTM;

import java.util.ArrayList;
import javafx.collections.ObservableList;

public class SupplierController {
    @FXML
    private TableView<PackageTM> tblItem;

    @FXML
    private TextField txtSearch;

    @FXML
    private ComboBox<String> select;

    private ObservableList<PackageTM> packageList;
    private ObservableList<String> packageNames = FXCollections.observableArrayList("Wedding Package", "Birthday Shoot");

    @FXML
    void btnRefresh(ActionEvent event) {
        tblItem.setItems(FXCollections.observableArrayList());

        // Optionally, clear the search field and ComboBox selection
        txtSearch.clear();  // Clears the search TextField
        select.getSelectionModel().clearSelection();
        // Reload the data from the model (or database, or wherever the data is coming from)
        ArrayList<PackageTM> updatedPackages = PackageModel.LoadForm();

        // Convert the updated list to an ObservableList
        ObservableList<PackageTM> updatedPackageList = FXCollections.observableList(updatedPackages);

        // Clear and set the new data to the TableView
        tblItem.setItems(updatedPackageList);

        // Optionally, clear any search or filters if needed
        txtSearch.clear();  // This will clear the search field if you want to reset it when refreshing
    }


    @FXML
    void initialize() {
        // Load the data from the model
        ArrayList<PackageTM> tms = PackageModel.LoadForm();
        packageList = FXCollections.observableList(tms);

        // Set the columns in the TableView
        tblItem.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblItem.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("package_Name"));
        tblItem.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("additional_Items"));
        tblItem.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("coverage_Time"));
        tblItem.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("price"));

        // Set the initial items in the TableView
        tblItem.setItems(packageList);

        // Set the items for ComboBox (Wedding Package, Birthday Shoot)
        select.setItems(packageNames);

        // Listen for ComboBox selection change
        select.setOnAction(this::updateTableOnPackageSelection);
    }

    @FXML
    void btnAddPackage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddPackage.fxml"));
            AnchorPane pane = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add New Package");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(pane));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnSearch(ActionEvent event) {
        String searchText = txtSearch.getText().toLowerCase();  // Get the text from the search field

        if (searchText == null || searchText.isEmpty()) {
            tblItem.setItems(packageList);  // Reset to full list if search field is empty
        } else {
            ObservableList<PackageTM> filteredList = FXCollections.observableArrayList();

            // Filter the list by package name
            for (PackageTM packageItem : packageList) {
                if (packageItem.getPackage_Name().toLowerCase().contains(searchText)) {
                    filteredList.add(packageItem);
                }
            }

            // Set filtered list to the TableView
            tblItem.setItems(filteredList);
        }
    }

    // Method to update the TableView based on the selected package type
    private void updateTableOnPackageSelection(ActionEvent event) {
        String selectedPackage = select.getValue();

        if (selectedPackage == null || selectedPackage.isEmpty()) {
            tblItem.setItems(packageList); // Show all if no package is selected
        } else {
            ObservableList<PackageTM> filteredList = FXCollections.observableArrayList();

            // Filter the packages based on selected type
            for (PackageTM packageItem : packageList) {
                if (packageItem.getPackage_Name().equalsIgnoreCase(selectedPackage)) {
                    filteredList.add(packageItem);
                }
            }

            // Update TableView with the filtered list
            tblItem.setItems(filteredList);
        }
    }
}
