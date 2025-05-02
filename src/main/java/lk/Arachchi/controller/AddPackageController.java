package lk.Arachchi.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lk.Arachchi.dto.PackageDto;
import lk.Arachchi.model.PackageModel;

import java.sql.SQLException;

public class AddPackageController {

    @FXML
    private TextField txtPackageName;

    @FXML
    private TextArea txtAdditionalItems;

    @FXML
    private TextField txtCoverageTime;

    @FXML
    private TextField txtPrice;


    @FXML
    void btnSavePackage(ActionEvent event) throws SQLException, ClassNotFoundException {
        String packageName = txtPackageName.getText();
        String additionalItems = txtAdditionalItems.getText();
        String coverageTime = txtCoverageTime.getText();
        double price = Double.parseDouble(txtPrice.getText());

        boolean b = PackageModel.addPackage(new PackageDto(packageName, additionalItems, coverageTime, price));
        if (b){
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setHeaderText("Operation Successful");
            successAlert.setContentText("The item was successfully added.");
            successAlert.showAndWait();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }
        else {
            Alert failureAlert = new Alert(Alert.AlertType.WARNING);
            failureAlert.setHeaderText("Operation Failed");
            failureAlert.setContentText("The item could not be added. Please try again.");
            failureAlert.showAndWait();

        }

    }
}

