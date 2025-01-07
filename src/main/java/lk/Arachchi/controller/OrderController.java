package lk.Arachchi.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lk.Arachchi.db.DBConnection;
import lk.Arachchi.dto.OrderDto;
import lk.Arachchi.model.OrderModel;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderController {
    @FXML
    private BorderPane rootOrderPage;

    @FXML
    private GridPane cardGrid;

    @FXML
    void btnButton(ActionEvent event) throws IOException {
        try {
            // Load the new FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AddOrder-page.fxml"));
            Parent newPage = fxmlLoader.load();

            // Create a new stage (window)
            Stage newStage = new Stage();
            Scene newScene = new Scene(newPage);
            newStage.setScene(newScene);

            // Set window title and style
            newStage.setTitle("Add Order");
            newStage.initModality(Modality.WINDOW_MODAL); // Make it a modal window
            newStage.initOwner(rootOrderPage.getScene().getWindow()); // Set the owner to the main stage
            newStage.centerOnScreen();
            newStage.showAndWait(); // Show the new stage and wait until it's closed

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {


        cardGrid.getChildren().clear();
        cardGrid.setHgap(10);
        cardGrid.setVgap(10);

        try {
            // Load items from the database
            Connection connection = DBConnection.getDBConnection().getConnection();
            String query = "SELECT * FROM addorder";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            int column = 0;
            int row = 0;

            while (resultSet.next()) {
                // Correct column names and phone number retrieval
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String type = resultSet.getString("type");
                int phone = resultSet.getInt("phone");  // Use getInt() for phone as it's an int type
                String email = resultSet.getString("email");
                String status = resultSet.getString("statu");
                String note = resultSet.getString("note");
                LocalDate selectedDate = resultSet.getDate("Date") != null ? resultSet.getDate("Date").toLocalDate() : null;


                VBox card = createCard(id,name, type, String.valueOf(phone), email, status, note, selectedDate); // Convert phone to String for display
                cardGrid.add(card, column, row);

                column++;
                if (column == 5) {
                    column = 0;
                    row++;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    private VBox createCard(int id,String itemName, String type, String phone, String email, String status, String note, LocalDate selectedDate) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        // Order ID and Status
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(10);

        Label orderId = new Label("#ORD-"+id);
        orderId.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        Label statusLabel = new Label(status);
        statusLabel.setStyle("-fx-background-color: #FFF3CD; -fx-text-fill: #856404; -fx-padding: 5 15; -fx-background-radius: 15;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(orderId, spacer, statusLabel);

        // Client Name
        Label name = new Label("Customer name:-"+itemName);
        name.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        // Service Type
        Label serviceType = new Label("Order type:-"+type);
        serviceType.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        // Date
        Label date = new Label("Date:-"+selectedDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy")));
        date.setStyle("-fx-font-size: 14; -fx-text-fill: #666666; -fx-font-weight: bold;");

        // Price and View Button
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setSpacing(10);

        Button viewBtn = new Button("View");
        viewBtn.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 5;");

        viewBtn.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 5;");

        viewBtn.setOnAction(e -> showDetails(id, itemName, type, phone, email, status, note, selectedDate));

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);
        footer.getChildren().addAll( footerSpacer, viewBtn);

        card.getChildren().addAll(header, name, serviceType, date, footer);

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 0);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);"));

        return card;


    }

    private void showDetails(int id, String itemName, String type, String phone, String email, String status, String note, LocalDate selectedDate) {
        // Create a new window or dialog for details
        Stage detailStage = new Stage();
        VBox detailBox = new VBox(15);
        detailBox.setPadding(new Insets(15));

        // Create labels for all the details
        Label idLabel = new Label("Order ID: #ORD-" + id);
        Label nameLabel = new Label("Customer Name: " + itemName);
        Label typeLabel = new Label("Order Type: " + type);
        Label phoneLabel = new Label("Phone: " + phone);
        Label emailLabel = new Label("Email: " + email);
        Label statusLabel = new Label("Status: " + status);
        Label noteLabel = new Label("Note: " + note);
        Label dateLabel = new Label("Date: " + selectedDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy")));

        // Style the labels
        idLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        nameLabel.setStyle("-fx-font-size: 14;");
        typeLabel.setStyle("-fx-font-size: 14;");
        phoneLabel.setStyle("-fx-font-size: 14;");
        emailLabel.setStyle("-fx-font-size: 14;");
        statusLabel.setStyle("-fx-font-size: 14;");
        noteLabel.setStyle("-fx-font-size: 14;");
        dateLabel.setStyle("-fx-font-size: 14;");

        // Add all labels to the VBox
        detailBox.getChildren().addAll(idLabel, nameLabel, typeLabel, phoneLabel, emailLabel, statusLabel, noteLabel, dateLabel);

        // Set up the scene and stage
        Scene detailScene = new Scene(detailBox, 400, 400);
        detailStage.setTitle("Order Details");
        detailStage.setScene(detailScene);
        detailStage.show();
    }


}
