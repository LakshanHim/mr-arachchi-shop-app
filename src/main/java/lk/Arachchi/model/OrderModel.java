package lk.Arachchi.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import lk.Arachchi.db.DBConnection;
import lk.Arachchi.dto.OrderDto;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class OrderModel {

    public static boolean addOrder(OrderDto order) {
        try {
            Connection connection = DBConnection.getDBConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("insert into addorder(name,type,phone,email,statu,note,Date ) values(?,?,?,?,?,?,?)");
            preparedStatement.setObject(1, order.getName());
            preparedStatement.setObject(2, order.getType());
            preparedStatement.setObject(3, order.getPhone());
            preparedStatement.setObject(4, order.getEmail());
            preparedStatement.setObject(5, order.getStatus());
            preparedStatement.setObject(6, order.getNote());
            preparedStatement.setObject(7, order.getFormatDate());
            int i = preparedStatement.executeUpdate();
            if(i>0){
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setHeaderText("Order Added Successfully");
                successAlert.setContentText("The order has been successfully added!");
                successAlert.showAndWait();
                return true;
            }
            else{
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Failed to Add Order");
                errorAlert.setContentText("Something went wrong. Please try again.");
                errorAlert.showAndWait();
            }
        } catch (Exception e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("An Exception Occurred");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
        }
        return false;
    }


}
