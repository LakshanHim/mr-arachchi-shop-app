package lk.Arachchi.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import lk.Arachchi.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class HomeController {
    @FXML
    private Label appointment;

    @FXML
    private Label getOrders;

    @FXML
    private Label getRevenue;

    @FXML
    private Label dailSale;

    @FXML
    private NumberAxis graph;

    @FXML
    private Label lowStockItem;

    @FXML
    private Label totalOrders;

    @FXML
    public void initialize() throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getDBConnection().getConnection();
        String lowStockQuery = "SELECT name FROM inventory WHERE qty <= 5";
        PreparedStatement lowStockStatement = connection.prepareStatement(lowStockQuery);
        ResultSet lowStockResult = lowStockStatement.executeQuery();

        // Collect low-stock item names
        StringBuilder lowStockNames = new StringBuilder();
        int count = 0;
        while (lowStockResult.next()) {
            String itemName = lowStockResult.getString("name");
            lowStockNames.append(itemName).append(" / ");
            count++;
        }

        // Remove the trailing " / " if there are any items
        if (lowStockNames.length() > 0) {
            lowStockNames.setLength(lowStockNames.length() - 3);
        }

        // Create formatted text with red, bold font
        Text styledText = new Text(lowStockNames.toString());
        styledText.setFill(Color.RED); // Set font color to red
        styledText.setStyle("-fx-font-weight: bold;"); // Set font to bold

        // Add the styled text to the TextFlow
//        txtLowBox.getChildren().clear(); // Clear any existing content
//        txtLowBox.getChildren().add(styledText);

        // Update the low stock count
        lowStockItem.setText(String.valueOf(count));
        setFromBill();

    }

    public void setFromBill() {
        double totalRevenue=0;
        int totalOrders=0;
        String revenueQuery = "SELECT SUM(totalAmount) AS totalRevenue FROM bill WHERE DATE(orderDate) = ?";
        String ordersQuery = "SELECT COUNT(*) AS totalOrders FROM bill WHERE DATE(orderDate) = ?";

        LocalDate today = LocalDate.now();

        // Single try-catch block for all DB operations
        try {
            Connection connection = DBConnection.getDBConnection().getConnection();
            PreparedStatement revenueStatement = connection.prepareStatement(revenueQuery);
            revenueStatement.setString(1, today.toString());  // Set today's date
            ResultSet revenueResult = revenueStatement.executeQuery();
            if (revenueResult.next()) {
                totalRevenue = revenueResult.getDouble("totalRevenue");
            }
            revenueStatement.close();  // Close the statement after use

            // Prepare the statement for total orders
            PreparedStatement ordersStatement = connection.prepareStatement(ordersQuery);
            ordersStatement.setString(1, today.toString());  // Set today's date
            ResultSet ordersResult = ordersStatement.executeQuery();
            if (ordersResult.next()) {
                totalOrders = ordersResult.getInt("totalOrders");
            }
            ordersStatement.close();  // Close the statement after use
            dailSale.setText(String.valueOf(totalRevenue));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
