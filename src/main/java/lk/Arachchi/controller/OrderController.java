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
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lk.Arachchi.db.DBConnection;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class OrderController {

    @FXML
    private BorderPane rootOrderPage;

    @FXML
    private GridPane cardGrid;

    @FXML
    private DatePicker pickDate;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    void btnRefresh(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Order-page.fxml"));
            Parent newOrderPage = loader.load();

            rootOrderPage.getChildren().clear();
            rootOrderPage.setCenter(newOrderPage);

            if (newOrderPage instanceof Region) {
                ((Region) newOrderPage).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            }
        } catch (Exception e) {
            System.out.println("Error refreshing the order page: " + e.getMessage());
        }
    }


    @FXML
    void btnButton(ActionEvent event) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AddOrder-page.fxml"));
            Parent newPage = fxmlLoader.load();

            Stage newStage = new Stage();
            Scene newScene = new Scene(newPage);
            newStage.setScene(newScene);

            newStage.setTitle("Add Order");
            newStage.initModality(Modality.WINDOW_MODAL);
            newStage.initOwner(rootOrderPage.getScene().getWindow());
            newStage.centerOnScreen();
            newStage.showAndWait();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void initialize() throws Exception {
        cardGrid.getChildren().clear();
        cardGrid.setHgap(10);
        cardGrid.setVgap(10);

        comboBox.setItems(FXCollections.observableArrayList("\uD83D\uDCC8 Date","\uD83D\uDCC9 Date","\uD83C\uDD94 Order ID"));

        int column = 0;
        int row = 0;

        Connection connection = DBConnection.getDBConnection().getConnection();
        String query = "SELECT * FROM addorder WHERE Date >= ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setDate(1, Date.valueOf(LocalDate.now()));  // Use current date as the parameter

        ResultSet resultSet = statement.executeQuery();

        // Process the result set
        while (resultSet.next()) {
            LocalDate orderDate = resultSet.getDate("Date") != null ? resultSet.getDate("Date").toLocalDate() : null;

            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String type = resultSet.getString("type");
            int phone = resultSet.getInt("phone");
            String email = resultSet.getString("email");
            String status = resultSet.getString("statu");
            String note = resultSet.getString("note");

            VBox card = createCard(id, name, type, String.valueOf(phone), email, status, note, orderDate);
            cardGrid.add(card, column, row);

            column++;
            if (column == 4) { // Start a new row after 4 columns
                column = 0;
                row++;
            }
        }
        pickDate.setOnAction(event -> {
            try {
                loadOrdersByDate(pickDate.getValue());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        comboBox.setOnAction(event -> {
            String selectedItem = comboBox.getValue();
            updateCard(selectedItem);
        });
    }

    private void updateCard(String selectedItem) {
        cardGrid.getChildren().clear();  // Clear existing cards
        int column = 0;
        int row = 0;

        try {
            Connection connection = DBConnection.getDBConnection().getConnection();
            String query;

            // Filter cards based on ComboBox selection
            if (selectedItem.contains("Date")) {
                query = "SELECT * FROM addorder ORDER BY Date";
            } else if (selectedItem.contains("Order ID")) {
                query = "SELECT * FROM addorder ORDER BY id";
            } else {
                query = "SELECT * FROM addorder";
            }

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                VBox card = createCardFromResultSet(resultSet);
                cardGrid.add(card, column++, row);

                // Create a new row after every 4 columns
                if (column == 4) {
                    column = 0;
                    row++;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private VBox createCardFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String type = resultSet.getString("type");
        int phone = resultSet.getInt("phone");
        String email = resultSet.getString("email");
        String status = resultSet.getString("statu");
        String note = resultSet.getString("note");
        LocalDate date = resultSet.getDate("Date").toLocalDate();

        return createCard(id, name, type, String.valueOf(phone), email, status, note, date);
    }

    private void loadOrdersByDate(LocalDate selectedDate) throws SQLException, ClassNotFoundException {
        // Clear the grid before adding new data
        cardGrid.getChildren().clear();
        int column = 0;
        int row = 0;

        try {
            Connection connection = DBConnection.getDBConnection().getConnection();
            String query;
                // Query for orders on the selected date
                query = "SELECT * FROM addorder WHERE Date = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setDate(1, Date.valueOf(selectedDate));  // Use selected date as the parameter
                ResultSet resultSet = statement.executeQuery();
                // Process the result set
                while (resultSet.next()) {
                    LocalDate orderDate = resultSet.getDate("Date") != null ? resultSet.getDate("Date").toLocalDate() : null;

                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String type = resultSet.getString("type");
                    int phone = resultSet.getInt("phone");
                    String email = resultSet.getString("email");
                    String status = resultSet.getString("statu");
                    String note = resultSet.getString("note");

                    VBox card = createCard(id, name, type, String.valueOf(phone), email, status, note, orderDate);
                    cardGrid.add(card, column, row);

                    column++;
                    if (column == 4) { // Start a new row after 4 columns
                        column = 0;
                        row++;
                    }
                }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private VBox createCard(int id, String itemName, String type, String phone, String email, String status, String note, LocalDate selectedDate) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setPrefWidth(400);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(10);

        Label orderId = new Label("#ORD-" + id);
        orderId.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        Label statusLabel = new Label(status);
        statusLabel.setStyle("-fx-background-color: #FFF3CD; -fx-text-fill: #856404; -fx-padding: 5 15; -fx-background-radius: 15;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(orderId, spacer, statusLabel);

        Label name = new Label("Customer name: " + itemName);
        name.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        Label serviceType = new Label("Order type: " + type);
        serviceType.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        Label date = new Label("Date: " + (selectedDate != null ? selectedDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy")) : ""));
        date.setStyle("-fx-font-size: 14; -fx-text-fill: #666666; -fx-font-weight: bold;");

        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setSpacing(10);

        Button viewBtn = new Button("View");
        viewBtn.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 5;");

        viewBtn.setOnAction(e -> showDetails(id, itemName, type, phone, email, status, note, selectedDate));

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);
        footer.getChildren().addAll(footerSpacer, viewBtn);

        card.getChildren().addAll(header, name, serviceType, date, footer);

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 0);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);"));

        return card;
    }

    private void showDetails(int id, String itemName, String type, String phone, String email, String status, String note, LocalDate selectedDate) {
        Stage detailStage = new Stage();
        VBox detailBox = new VBox(15);
        detailBox.setPadding(new Insets(15));

        Label idLabel = new Label("Order ID: #ORD-" + id);
        Label nameLabel = new Label("Customer Name: " + itemName);
        Label typeLabel = new Label("Order Type: " + type);
        Label phoneLabel = new Label("Phone: " + phone);
        Label emailLabel = new Label("Email: " + email);
        Label statusLabel = new Label("Payment Status: " + status);
        Label noteLabel = new Label("Note: " + note);
        Label dateLabel = new Label("Date: " + selectedDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy")));

        detailBox.getChildren().addAll(idLabel, nameLabel, typeLabel, phoneLabel, emailLabel, statusLabel, noteLabel, dateLabel);

        Scene detailScene = new Scene(detailBox, 400, 400);
        detailStage.setTitle("Order Details");
        detailStage.setScene(detailScene);
        detailStage.show();
    }


}




