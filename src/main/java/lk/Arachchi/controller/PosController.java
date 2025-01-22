package lk.Arachchi.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import lk.Arachchi.db.DBConnection;
import lk.Arachchi.dto.BillDetailDto;
import lk.Arachchi.dto.BillDto;
import lk.Arachchi.model.BillModel;
import lk.Arachchi.tm.BillTM;

import javax.print.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PosController {

    @FXML
    private GridPane cardGrid;

    @FXML
    private Label subTotal;

    @FXML
    private TextField txtBalancce;
    @FXML
    private Label subBalance;

    @FXML
    private TextField txtSearch;

    @FXML
    private BorderPane rootPos;

    @FXML
    private TableView<BillTM> tblBill;

    private List<BillTM> billList;

    private double balance=0;

    private double Total=0;


    private ArrayList<BillDetailDto> billDetailDtos;

    @FXML
    void btnBalance(ActionEvent event) {
        double amount = Double.parseDouble(txtBalancce.getText());
        balance = amount-Total;
        subBalance.setText("Balance:"+balance);
    }


    @FXML
    private void initialize() {
        billList = new ArrayList<>();
        billDetailDtos = new ArrayList<>();
        loadCards();

        tblBill.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblBill.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("qty"));
        tblBill.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("price"));

        // Add a Delete column
        TableColumn<BillTM, Void> colBtn = new TableColumn<>("Action");

        Callback<TableColumn<BillTM, Void>, TableCell<BillTM, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btn = new Button("Delete");

            {
                btn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 5;");

                btn.setOnAction(event -> {
                    BillTM billItem = getTableView().getItems().get(getIndex());
                    deleteItem(billItem);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        };

        colBtn.setCellFactory(cellFactory);
        tblBill.getColumns().add(colBtn);
    }


    @FXML
    void btnPay(ActionEvent event) throws SQLException, ClassNotFoundException {
        if (balance >= 0) {
            // Clear order details
            tblBill.getItems().clear();
            subTotal.setText("");
            subBalance.setText("");
            txtBalancce.clear();

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String format = formatter.format(date);


            try {
                boolean isOrderPlaced = BillModel.placeOrder(new BillDto(format, Total, billDetailDtos));

                if (isOrderPlaced) {
                    // Display success alert to the user
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Payment Successful");
                    alert.setHeaderText("Payment Completed");
                    alert.setContentText("The bill has been paid successfully and saved to the database.");
                    alert.showAndWait();
                } else {
                    // Display error alert to the user
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Payment Error");
                    alert.setHeaderText("Database Error");
                    alert.setContentText("There was an error saving the bill details to the database. Please try again.");
                    alert.showAndWait();
                }
            } catch (Exception e) {
                // Handle unexpected exceptions
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Unexpected Error");
                alert.setHeaderText("An error occurred");
                alert.setContentText("An unexpected error occurred while processing the payment: " + e.getMessage());
                alert.showAndWait();

                // Optionally log the error to console or a logging framework
                e.printStackTrace();
                System.out.println(e.getMessage());
            }


        } else {
            // Show error alert if balance is insufficient
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Payment Error");
            alert.setHeaderText("Insufficient Amount");
            alert.setContentText("Your amount is less than the total bill. Please check and try again.");
            alert.showAndWait();
        }

    }

    private void deleteItem(BillTM billItem) {
        // Subtract the item's total price from the subtotal
        Total -= billItem.getPrice(); // Assuming `billItem.getPrice()` gives the total price for the item
        if (Total < 0) {
            Total = 0; // Prevent negative totals
        }

        // Update the subtotal label
        subTotal.setText("Subtotal: " + String.format("%.2f", Total));

        // Remove the item from the list
        billList.remove(billItem);

        // Update the TableView
        tblBill.setItems(FXCollections.observableList(billList));
    }

    @FXML
    void addCustomCharge(ActionEvent event) {
        // Create a new dialog window
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add Custom Charge");

        // Create the layout for the dialog
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-background-color: #f4f4f4;");

        // Description field
        Label lblDescription = new Label("Description");
        TextField txtDescription = new TextField();
        txtDescription.setPromptText("Enter description");

        // Quantity field
        Label lblQty = new Label("Qty");
        TextField txtQty = new TextField();
        txtQty.setPromptText("Enter quantity");

        // Price field
        Label lblPrice = new Label("Price");
        TextField txtPrice = new TextField();
        txtPrice.setPromptText("Enter price");

        // Add button
        Button btnAdd = new Button("Add to Cart");
        btnAdd.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 10;");

        // Add button action
        btnAdd.setOnAction(e -> {
            String description = txtDescription.getText();
            int quantity;
            double price;

            // Validate input
            try {
                quantity = Integer.parseInt(txtQty.getText());
                price = Double.parseDouble(txtPrice.getText());
            } catch (NumberFormatException ex) {
                showAlert("Invalid Input", "Please enter valid numeric values for quantity and price.");
                return;
            }

            // Add to the bill list and update the table
            double totalPrice = quantity * price;
            billList.add(new BillTM(description, quantity, totalPrice));
            tblBill.setItems(FXCollections.observableList(billList));

            // Update the subtotal
            Total += totalPrice;
            subTotal.setText("Subtotal: " + String.format("%.2f", Total));

            // Close the dialog
            dialog.close();
            billDetailDtos.add(new BillDetailDto(description,quantity,totalPrice));
        });

        // Arrange the elements in the layout
        layout.getChildren().addAll(lblDescription, txtDescription, lblQty, txtQty, lblPrice, txtPrice, btnAdd);

        // Set the scene and show the dialog
        Scene scene = new Scene(layout, 300, 300);
        dialog.setScene(scene);
        dialog.show();


    }

    @FXML
    void search(ActionEvent event) {
        // Get the search term from the text field
        String searchTerm = txtSearch.getText().toLowerCase();

        // Clear the existing items in the grid
        cardGrid.getChildren().clear();

        // Reload and filter the items based on the search term
        try {
            Connection connection = DBConnection.getDBConnection().getConnection();

            // Modify query to search for names or SKUs starting with the search term
            String query = "SELECT * FROM inventory WHERE LOWER(name) LIKE ? OR LOWER(sku) LIKE ?";
            PreparedStatement statement = connection.prepareStatement(query);

            // Search for items starting with the search term
            statement.setString(1, searchTerm + "%"); // Match items starting with the term
            statement.setString(2, searchTerm + "%");
            ResultSet resultSet = statement.executeQuery();

            int column = 0;
            int row = 0;

            // Loop through the filtered results and create cards
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                String sku = resultSet.getString("sku");

                // Create a card
                VBox card = createCard(id, name, sku, price);

                // Add the card to the grid
                cardGrid.add(card, column, row);

                column++;
                if (column == 4) { // Adjust for the number of columns
                    column = 0;
                    row++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCards() {
        try {
            Connection connection = DBConnection.getDBConnection().getConnection();
            String query = "SELECT * FROM inventory";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            int column = 0;
            int row = 0;

            // Loop through the results and create cards
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String sku = resultSet.getString("sku");
                double price = resultSet.getDouble("price");


                // Create a card
                VBox card = createCard(id, name, sku, price);

                // Add the card to the grid
                cardGrid.add(card, column, row);

                column++;
                if (column == 4) { // Change to the desired number of columns
                    column = 0;
                    row++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createCard(int id, String name, String sku, double price) {
        // Create a VBox for the card
        VBox card = new VBox();
        card.setSpacing(10);
        card.setPrefWidth(140); // Set the card width
        card.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-color: #f9f9f9; -fx-background-radius: 5;");

        // Add item details to the card
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 5; -fx-background-radius: 3;");
        nameLabel.setPrefWidth(150); // Match the card width
        nameLabel.setAlignment(javafx.geometry.Pos.CENTER); // Center align the text
        nameLabel.setStyle(nameLabel.getStyle() + " -fx-text-alignment: center; -fx-alignment: center;");

        Label skuLabel = new Label(sku);
        skuLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #000000; -fx-font-weight: bold;");

        Label priceLabel = new Label("$" + String.format("%.2f", price));
        priceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #f31313;");



        // Add a spinner for quantity
        Spinner<Integer> quantitySpinner = new Spinner<>();
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1)); // Min: 1, Max: 100, Default: 1
        quantitySpinner.setStyle("-fx-pref-width: 60;");

        // Add button
        Button addButton = new Button("Add");
        addButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 5 10; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Set button action to call handleAddButton
        addButton.setOnAction(event -> {
            int quantity = quantitySpinner.getValue();
            // Assume getAvailableStock returns the available stock for the given item
            int availableStock = getAvailableStock(id);

            // Check if the quantity is greater than available stock
            if (quantity > availableStock) {
                // Show alert if not enough stock
                showAlert("Insufficient Stock", "Not enough items available. Only " + availableStock + " item(s) are in stock.");
            } else {
                handleAddButton(id, name, price, quantity);  // Trigger handleAddButton method
            }
        });

        // Place spinner and button in an HBox for alignment
        javafx.scene.layout.HBox controlBox = new javafx.scene.layout.HBox(10);
        controlBox.setAlignment(javafx.geometry.Pos.CENTER); // Center-align the elements
        controlBox.getChildren().addAll(quantitySpinner, addButton);

        // Add elements to the VBox
        card.getChildren().addAll(nameLabel, priceLabel, skuLabel, controlBox);

        return card;
    }
    private int getAvailableStock(int id) {
        int availableStock = 0;  // Default to 0 if there is any error or no stock found
        String query = "SELECT qty FROM inventory WHERE id = ?";  // SQL query to get the stock quantity

        try{
            Connection connection = DBConnection.getDBConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            // Set the itemId parameter in the query
            preparedStatement.setInt(1, id);

            // Execute the query and get the result
            ResultSet resultSet = preparedStatement.executeQuery();

            // If the result exists, get the available quantity
            if (resultSet.next()) {
                availableStock = resultSet.getInt("qty");  // Fetch quantity from the result set
            }

        } catch (Exception e) {
            System.out.println("Error fetching available stock: " + e.getMessage());
        }

        return availableStock;  // Return the available stock quantity
    }

    private void handleAddButton(int id, String name, double price, int quantity) {
        double tPrice = price * quantity;

        Total+=tPrice;
        subTotal.setText("Subtotal: " + Total);
        billList.add(new BillTM(name,quantity,tPrice));
        tblBill.setItems(FXCollections.observableList(billList));
        billDetailDtos.add(new BillDetailDto(id,name,quantity,tPrice));
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    void btnRecipet(ActionEvent event) throws SQLException, ClassNotFoundException {
        try {
            // Retrieve the last order number
            int lastOrderNum = BillModel.recipt();
            if (lastOrderNum <= 0) {
                showAlert("Error", "Invalid order number found.");
                return;
            }

            // Retrieve bill details
            BillDto billDto = BillModel.billSearch(lastOrderNum);
            if (billDto == null) {
                showAlert("Error", "Bill details not found.");
                return;
            }

            // Format date nicely
            String orderDate = formatDate(billDto.getOrderDate());
            double amount = billDto.getBillAmount();

            // Retrieve item details
            ArrayList<BillDetailDto> items = BillModel.billDetailSearch(lastOrderNum);
            if (items == null || items.isEmpty()) {
                showAlert("Error", "No items found for this bill.");
                return;
            }

            // Calculate totals
            double subtotal = calculateSubtotal(items);
            double taxRate = 0.00; // 10% tax rate
            double taxAmount = subtotal*taxRate;
            double total = subtotal;

            // Build the receipt string with enhanced formatting
            StringBuilder receipt = new StringBuilder();
            receipt.append("═══════════════════════════════════════\n");
            receipt.append("      MrArachchi Photography Shop      \n");
            receipt.append("═══════════════════════════════════════\n");
            receipt.append(centerText("123 Main St, City, ST") + "\n");
            receipt.append(centerText("Phone: 123-456-7890") + "\n");
            receipt.append(centerText("Email: contact@mrarachchi.com") + "\n");
            receipt.append("───────────────────────────────────────\n");
            receipt.append(centerText("CASH RECEIPT") + "\n");
            receipt.append("───────────────────────────────────────\n");
            receipt.append(String.format("Order No: #%06d\n", lastOrderNum));
            receipt.append(String.format("Date: %s\n", orderDate));
            receipt.append(String.format("Time: %s\n", getCurrentTime()));
            receipt.append("───────────────────────────────────────\n");
            receipt.append(String.format("%-20s %8s %10s\n", "Item", "Qty", "Amount"));
            receipt.append("───────────────────────────────────────\n");

            // Add item details with proper alignment
            for (BillDetailDto item : items) {
                // Truncate long item names
                String itemName = truncateString(item.getItemName(), 20);
                receipt.append(String.format("%-20s %8d %10.2f\n",
                        itemName,
                        item.getQty(),
                        item.gettPrice()));
            }

            receipt.append("───────────────────────────────────────\n");
            receipt.append(String.format("%-29s %10.2f\n", "Subtotal:", subtotal));
            receipt.append(String.format("%-29s %10.2f\n", "Tax (" + (taxRate * 100) + "%):", taxAmount));
            receipt.append("───────────────────────────────────────\n");
            receipt.append(String.format("%-29s %10.2f\n", "Total:", total));
            receipt.append("═══════════════════════════════════════\n");
            receipt.append(centerText("THANK YOU FOR YOUR BUSINESS!") + "\n");
            receipt.append(centerText("Follow us @mrarachchi") + "\n");
            receipt.append("═══════════════════════════════════════\n");

            // Create and style the receipt label
            Label receiptLabel = new Label(receipt.toString());
            receiptLabel.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 14px; -fx-font-weight: bold;");
            receiptLabel.setMaxWidth(Double.MAX_VALUE);
            receiptLabel.setAlignment(Pos.CENTER_LEFT);

            // Create buttons for actions
            Button printButton = createStyledButton("Print Receipt", "printer");

            // Add icons to buttons using your image path
            String imagePath = "E:\\ACPT\\Project java\\PhotographyShopApp\\src\\main\\resources\\images\\mr3.jpg";
            try {
                printButton.setGraphic(new ImageView(new Image(new File(imagePath + "printer.png").toURI().toString(), 16, 16, true, true)));
            } catch (Exception e) {
                System.out.println("Warning: Could not load button icons - " + e.getMessage());
            }

            // Create button container
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.getChildren().addAll(printButton);

            // Create main container
            VBox vbox = new VBox(15);
            vbox.setPadding(new Insets(20));
            vbox.setStyle("-fx-background-color: white;");
            vbox.getChildren().addAll(receiptLabel, buttonBox);

            // Set up the stage
            Stage receiptStage = new Stage();
            receiptStage.setTitle("Receipt #" + lastOrderNum);

            // Add shop logo/icon
            try {
                receiptStage.getIcons().add(new Image(new File(imagePath + "logo.png").toURI().toString()));
            } catch (Exception e) {
                System.out.println("Warning: Could not load shop logo - " + e.getMessage());
            }

            Scene scene = new Scene(vbox);

            // Add your CSS file
            scene.getStylesheets().add(new File("E:/ACPT/Project java/PhotographyShopApp/src/main/resources/view/style.css").toURI().toString());

            receiptStage.setScene(scene);
            receiptStage.setResizable(false);
            receiptStage.show();

            // Add button event handlers
            printButton.setOnAction(e -> printReceipt(receipt.toString()));

        } catch (Exception e) {
            showAlert("Error", "Failed to generate receipt: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper methods remain the same
    private String centerText(String text) {
        int padding = (47 - text.length()) / 2;
        return String.format("%" + padding + "s%s%" + padding + "s", "", text, "");
    }

    private String getCurrentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    private String formatDate(String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            return localDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        } catch (Exception e) {
            return date;
        }
    }

    private String truncateString(String str, int length) {
        if (str.length() <= length) return str;
        return str.substring(0, length - 3) + "...";
    }

    private double calculateSubtotal(ArrayList<BillDetailDto> items) {
        return items.stream()
                .mapToDouble(item -> item.gettPrice())
                .sum();
    }

    private Button createStyledButton(String text, String iconName) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 10 20;");
        return button;
    }

    // Implement these methods based on your requirements
    private void printReceipt(String receiptText) {
        Printer printer = Printer.getDefaultPrinter();
        if (printer == null) {
            showAlert("Error", "No printer found!");
            return;
        }

        PrinterJob job = PrinterJob.createPrinterJob(printer);
        if (job != null && job.showPrintDialog(null)) {
            TextArea printArea = new TextArea(receiptText);
            printArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px;");
            boolean success = job.printPage(printArea);
            if (success) {
                job.endJob();
                System.out.println("Receipt printed successfully.");
            } else {
                System.out.println("Failed to print the receipt.");
            }
        }
    }


}
