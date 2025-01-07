package lk.Arachchi.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.Arachchi.dto.OrderDto;
import lk.Arachchi.model.OrderModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class AddOrderController {

    @FXML
    private AnchorPane rootOrderDetail;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtName;

    @FXML
    private TextArea txtNotes;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextField txtStatus;

    @FXML
    private DatePicker datePicker;


    @FXML
    private TextField txtType;

    @FXML
    void btnSubmit(ActionEvent event) {
        LocalDate selectedDate = datePicker.getValue();
        String name = txtName.getText();
        String type = txtType.getText();
        int phone = Integer.parseInt(txtPhone.getText());
        String email = txtEmail.getText();
        String status = txtStatus.getText();
        String notes = txtNotes.getText();

        boolean b = OrderModel.addOrder(new OrderDto(name, type, phone, email, status, notes, selectedDate));
        if(b){
            Stage stage = (Stage) rootOrderDetail.getScene().getWindow();
            stage.close();

        }

    }

}