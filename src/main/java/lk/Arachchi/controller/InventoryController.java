package lk.Arachchi.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lk.Arachchi.db.DBConnection;
import javafx.fxml.FXMLLoader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InventoryController {

    @FXML
    private GridPane cardGride;

    @FXML
    private Label lItem;

    @FXML
    private Label oItem;

    @FXML
    private Label tItem;

    @FXML
    private Label totalvalue;

    @FXML
    private TextField txtinput;

    @FXML
    private BorderPane rootInvent;

    public void initialize() {
        try {
            loadInventoryCards();
            updateInventoryStats();
        } catch (Exception e) {
            System.out.println("Error loading inventory cards: " + e.getMessage());
        }
    }

    private void updateInventoryStats() {
        try {
            // Create database connection
            Connection connection = DBConnection.getDBConnection().getConnection();

            // Query to count total distinct items in inventory (total number of products)
            String totalQuery = "SELECT COUNT(*) FROM inventory";
            PreparedStatement totalStatement = connection.prepareStatement(totalQuery);
            ResultSet totalResult = totalStatement.executeQuery();
            if (totalResult.next()) {
                int totalItems = totalResult.getInt(1); // Total distinct items
                tItem.setText(String.valueOf(totalItems));  // Set total items (distinct items)
            }

            // Query to count low stock items (quantity <= 5)
            String lowStockQuery = "SELECT COUNT(*) FROM inventory WHERE qty <= 5";
            PreparedStatement lowStockStatement = connection.prepareStatement(lowStockQuery);
            ResultSet lowStockResult = lowStockStatement.executeQuery();
            if (lowStockResult.next()) {
                int lowStockItems = lowStockResult.getInt(1);
                lItem.setText(String.valueOf(lowStockItems));  // Set low stock items
            }

            // Query to count out of stock items (quantity == 0)
            String outOfStockQuery = "SELECT COUNT(*) FROM inventory WHERE qty = 0";
            PreparedStatement outOfStockStatement = connection.prepareStatement(outOfStockQuery);
            ResultSet outOfStockResult = outOfStockStatement.executeQuery();
            if (outOfStockResult.next()) {
                int outOfStockItems = outOfStockResult.getInt(1);
                oItem.setText(String.valueOf(outOfStockItems));  // Set out of stock items
            }

        } catch (Exception e) {
            System.out.println("Error updating inventory stats: " + e.getMessage());
        }
    }


    private void loadInventoryCards() throws Exception {
        cardGride.getChildren().clear();
        cardGride.setHgap(20);  // Increased spacing between cards
        cardGride.setVgap(20);
        cardGride.setPadding(new Insets(20));  // Add padding around the grid

        Connection connection = DBConnection.getDBConnection().getConnection();
        String query = "SELECT * FROM inventory";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        int column = 0;
        int row = 0;

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            double price = resultSet.getDouble("price");
            int qty = resultSet.getInt("qty");
            String sku = resultSet.getString("sku");

            VBox card = createInventoryCard(id, name, price, qty, sku);
            cardGride.add(card, column, row);

            column++;
            if (column == 3) {  // Changed to 3 cards per row for better spacing
                column = 0;
                row++;
            }
        }
        updateInventoryStats();
    }

    private VBox createInventoryCard(int id, String name, double price, int qty, String sku) {
        // Main card container
        VBox card = new VBox();
        card.setPadding(new Insets(15));
        card.setSpacing(8);
        card.setPrefWidth(300);  // Fixed width for cards
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +  // More rounded corners
                "-fx-border-width: 0 0 0 8; -fx-border-color: #2196F3; " + // Blue border with more curve on left
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 0);");

        // Title and Status
        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        Label statusLabel = new Label(qty > 5 ? "In Stock" : "Low Stock");
        statusLabel.setFont(Font.font("System", 12));
        statusLabel.setStyle(qty > 5
                ? "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 4;"
                : "-fx-background-color: #FFA726; -fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 4;");

        HBox headerBox = new HBox();
        headerBox.setSpacing(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().addAll(nameLabel, statusLabel);

        // Separator line
        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setStyle("-fx-background-color: #e0e0e0;");  // Light gray separator

        // Info Grid
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(10);
        infoGrid.setVgap(5);

        // Info Labels - Left Column
        addInfoLabel(infoGrid, "SKU:", sku, 0);
        addInfoLabel(infoGrid, "Quantity:", qty + " units", 1);
        addInfoLabel(infoGrid, "Price:", String.format("$%,.2f", price), 2);

        // Additional Separator between Price and Buttons
        Region separator2 = new Region();
        separator2.setPrefHeight(1);
        separator2.setStyle("-fx-background-color: #e0e0e0;");  // Light gray separator

        // Buttons
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(10);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Label editButton = createButton("Edit", "#2196F3", id);
        Label deleteButton = createButton("Delete", "#F44336", id);

        buttonBox.getChildren().addAll(editButton, deleteButton);

        // Add elements to card
        card.getChildren().addAll(headerBox, separator, infoGrid, separator2, buttonBox);

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 12; " +
                "-fx-border-width: 0 0 0 8; -fx-border-color: #2196F3; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 0);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-border-width: 0 0 0 8; -fx-border-color: #2196F3; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 0);"));

        return card;
    }

    private void addInfoLabel(GridPane grid, String label, String value, int row) {
        Label labelNode = new Label(label);
        labelNode.setFont(Font.font("System", 12));
        labelNode.setStyle("-fx-text-fill: #666666;");

        Label valueNode = new Label(value);
        valueNode.setFont(Font.font("System", 12));
        valueNode.setStyle("-fx-text-fill: #333333;");

        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }

    private Label createButton(String text, String color, int id) {
        Label button = new Label(text);
        button.setFont(Font.font("System", FontWeight.BOLD, 12));
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                "-fx-padding: 5 15; -fx-background-radius: 4; -fx-cursor: hand;");
        button.setOnMouseClicked(e -> {
            if (text.equals("Edit")) {
                editItem(id);
            } else if (text.equals("Delete")) {
                deleteItem(id);
            }
        });

        // Hover effect for buttons
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: derive(" + color + ", 10%); " +
                "-fx-text-fill: white; -fx-padding: 5 15; -fx-background-radius: 4; -fx-cursor: hand;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; -fx-padding: 5 15; -fx-background-radius: 4; -fx-cursor: hand;"));

        return button;
    }

    private void editItem(int id) {
        try {
            // Create a new Stage (edit window)
            Stage editStage = new Stage();
            editStage.setTitle("Edit Item");

            // Create a VBox layout for the edit form
            VBox editLayout = new VBox(10);  // Maintain default spacing between controls
            editLayout.setPadding(new Insets(20));

            // Create TextField controls to edit the item details
            TextField txtName = new TextField();
            TextField txtPrice = new TextField();
            TextField txtQty = new TextField();
            TextField txtSku = new TextField();

            // Load the existing item details from the database
            Connection connection = DBConnection.getDBConnection().getConnection();
            String query = "SELECT * FROM inventory WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                txtName.setText(resultSet.getString("name"));
                txtPrice.setText(String.valueOf(resultSet.getDouble("price")));
                txtQty.setText(String.valueOf(resultSet.getInt("qty")));
                txtSku.setText(resultSet.getString("sku"));
            }

            // Create Save button to update the item
            Label saveButton = new Label("Save");
            saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                    "-fx-padding: 10 20; -fx-background-radius: 4; -fx-cursor: hand;");
            saveButton.setOnMouseClicked(e -> {
                try {
                    // Update the database with the new values
                    String updateQuery = "UPDATE inventory SET name = ?, price = ?, qty = ?, sku = ? WHERE id = ?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                    updateStatement.setString(1, txtName.getText());
                    updateStatement.setDouble(2, Double.parseDouble(txtPrice.getText()));
                    updateStatement.setInt(3, Integer.parseInt(txtQty.getText()));
                    updateStatement.setString(4, txtSku.getText());
                    updateStatement.setInt(5, id);

                    int rowsAffected = updateStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Item updated successfully!");
                        loadInventoryCards(); // Refresh the inventory list
                    } else {
                        System.out.println("Error updating item.");
                    }
                    editStage.close(); // Close the edit window
                } catch (Exception ex) {
                    System.out.println("Error saving updated item: " + ex.getMessage());
                }
            });

            // Add controls to the layout
            editLayout.getChildren().addAll(
                    new Label("Item Name:"), txtName,
                    new Label("Price:"), txtPrice,
                    new Label("Quantity:"), txtQty,
                    new Label("SKU:"), txtSku,
                    saveButton
            );

            // Create and show the scene with increased height
            Scene editScene = new Scene(editLayout, 300, 400);  // Increased height to 400 (width stays the same)
            editStage.setScene(editScene);
            editStage.initModality(Modality.WINDOW_MODAL);
            editStage.initOwner(rootInvent.getScene().getWindow());
            editStage.centerOnScreen();
            editStage.showAndWait();

        } catch (Exception e) {
            System.out.println("Error opening edit window: " + e.getMessage());
        }
    }



    private void deleteItem(int id) {
        try {
            Connection connection = DBConnection.getDBConnection().getConnection();
            String query = "DELETE FROM inventory WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate();

            loadInventoryCards();  // Refresh the inventory after deletion
        } catch (Exception e) {
            System.out.println("Error deleting item: " + e.getMessage());
        }
    }

    @FXML
    void btnAddItems(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AddItem-page.fxml"));
            Parent newPage = fxmlLoader.load();

            Stage newStage = new Stage();
            Scene newScene = new Scene(newPage);
            newStage.setScene(newScene);

            newStage.setTitle("Add Items");
            newStage.initModality(Modality.WINDOW_MODAL);
            newStage.initOwner(rootInvent.getScene().getWindow());
            newStage.centerOnScreen();
            newStage.showAndWait();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void btnSearch(ActionEvent event) {
        String searchQuery = txtinput.getText().trim();  // Capture the text from the TextField

        if (!searchQuery.isEmpty()) {
            try {
                // Clear existing cards in the grid
                cardGride.getChildren().clear();
                cardGride.setHgap(20);  // Increased spacing between cards
                cardGride.setVgap(20);
                cardGride.setPadding(new Insets(20));  // Add padding around the grid

                // Connect to the database
                Connection connection = DBConnection.getDBConnection().getConnection();

                // Modify query to find items where name begins with the input string
                String query = "SELECT * FROM inventory WHERE name LIKE ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, searchQuery + "%");  // Use LIKE for names starting with the input text

                ResultSet resultSet = statement.executeQuery();

                // Initialize row and column positions for the grid
                int column = 0;
                int row = 0;

                // Loop through the result set and create cards
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    double price = resultSet.getDouble("price");
                    int qty = resultSet.getInt("qty");
                    String sku = resultSet.getString("sku");

                    VBox card = createInventoryCard(id, name, price, qty, sku);
                    cardGride.add(card, column, row);

                    column++;
                    if (column == 3) {  // 3 cards per row
                        column = 0;
                        row++;
                    }
                }
            } catch (Exception e) {
                System.out.println("Error searching inventory: " + e.getMessage());
            }
        } else {
            // If no search term, reload all inventory cards
            try {
                loadInventoryCards();
            } catch (Exception e) {
                System.out.println("Error loading inventory cards: " + e.getMessage());
            }
        }
    }


}
