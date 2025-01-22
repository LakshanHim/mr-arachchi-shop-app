package lk.Arachchi.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.Arachchi.db.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DashPageController {
    @FXML
    private Label getOrders;

    @FXML
    private Label getAppoiment;

    @FXML
    private Label dSales;

    @FXML
    private Label getItem;

    @FXML
    private Label getRevenue;

    @FXML
    private VBox rootDash;

    @FXML
    private ScrollPane scrollPanePage;

    @FXML
    private Label setDate;

    @FXML
    private Label setTime;

    @FXML
    private Label lItem;

    @FXML
    private Label tOrders;

    @FXML
    private TextFlow txtLowBox;

    @FXML
    private TextArea txtRemender;

    @FXML
    private BarChart<String, Number> lineChart;

    @FXML
    public void initialize() throws SQLException, ClassNotFoundException {
        setLineChart();
        loadTodaysOrders();
        // Fast scrolling logic
        scrollPanePage.addEventFilter(ScrollEvent.SCROLL, event -> {
            double deltaY = event.getDeltaY() * 6;  // Adjust speed multiplier
            scrollPanePage.setVvalue(scrollPanePage.getVvalue() - deltaY / scrollPanePage.getContent().getBoundsInLocal().getHeight());
            event.consume();
        });
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String format = formatter.format(date);
        setDate.setText("\uD83D\uDCC5" + format);

        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss a");
            String currentTime = timeFormatter.format(new Date());
            setTime.setText("⏰" + currentTime);
        }));
        clock.setCycleCount(Timeline.INDEFINITE); // Run indefinitely
        clock.play(); // Start the clock

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
        txtLowBox.getChildren().clear(); // Clear any existing content
        txtLowBox.getChildren().add(styledText);

        // Update the low stock count
        lItem.setText(String.valueOf(count));
        setFromBill();
        setTodayTotalOrders();
    }

    public void setLineChart() throws SQLException {
        lineChart.setTitle("Total Amounts for the Last 5 Days");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Amount");

        // Database resources
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        // Calculate the date range (last 5 days)
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(5); // 5 days ago (not including today)

        try {
            // SQL query to fetch totalAmount for the last 5 days (excluding today)
            String query = "SELECT orderDate, SUM(totalAmount) AS total FROM bill " +
                    "WHERE orderDate >= ? AND orderDate < ? " +  // Filter dates within the last 5 days
                    "GROUP BY orderDate ORDER BY orderDate ASC";

            // Database connection and execution
            connection = DBConnection.getDBConnection().getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, startDate.toString());  // Set the start date (5 days ago)
            preparedStatement.setString(2, today.toString());     // Set today's date (exclusive)

            resultSet = preparedStatement.executeQuery();

            // Process the result set and add data to the series
            while (resultSet.next()) {
                // Convert the orderDate to LocalDate
                LocalDate date = resultSet.getDate("orderDate").toLocalDate();
                double totalAmount = resultSet.getDouble("total");

                // Add data to the series
                series.getData().add(new XYChart.Data<>(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), totalAmount));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        // Add the series to the LineChart
        lineChart.getData().clear();  // Clear any previous data
        lineChart.getData().add(series);
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

            // Update the labels with the retrieved data
            getOrders.setText("Total Orders Today: " + totalOrders);
            getRevenue.setText("Total Revenue Today: " + totalRevenue);
            dSales.setText(String.valueOf(totalRevenue));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void setTodayTotalOrders() {
        int totalOrders = 0;  // Initialize the count for total orders
        String ordersQuery = "SELECT COUNT(*) AS totalOrders FROM addorder WHERE Date = ?";


        LocalDate today = LocalDate.now();  // Get today's date

        try{
            Connection connection = DBConnection.getDBConnection().getConnection();
            PreparedStatement ordersStatement = connection.prepareStatement(ordersQuery);

            // Set the parameter for today's date
            ordersStatement.setString(1, today.toString());

            // Execute the query and process the result
            try (ResultSet ordersResult = ordersStatement.executeQuery()) {
                if (ordersResult.next()) {
                    totalOrders = ordersResult.getInt("totalOrders");
                }
            }

            // Update the label with today's total orders
            getAppoiment.setText(String.valueOf(totalOrders));

        } catch (Exception e) {
            System.out.println(e.getMessage());  // Print the exception stack trace for debugging
        }
    }



    public void loadTodaysOrders() throws SQLException, ClassNotFoundException {
        // Get today's date in the required format
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        String todayDate = formatter.format(today);

        // Establish a database connection
        Connection connection = DBConnection.getDBConnection().getConnection();

        // SQL query to get today's orders (use correct casing for `Date`)
        String query = "SELECT name, type FROM addorder WHERE Date = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, todayDate);

        ResultSet resultSet = statement.executeQuery();

        // Collect order names and count
        StringBuilder orderDetails = new StringBuilder();
        int orderCount = 0;
        while (resultSet.next()) {
            String orderName = resultSet.getString("name");
            String orderType = resultSet.getString("type");
            orderDetails.append("Name:- ").append(orderName).append("     ").append(" Type:- ").append(orderType).append("\n");
            orderCount++;
        }

        // Set the count and names
        tOrders.setText(String.valueOf(orderCount)); // Set order count to the Label
        txtRemender.setText(orderDetails.toString()); // Set order details to the TextArea

    }



    @FXML
    void btnStaff(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Staff-page.fxml"));

        Parent inventoryReportView = loader.load();

        rootDash.setFillWidth(true);

        rootDash.getChildren().clear();
        rootDash.getChildren().add(inventoryReportView);
        if (inventoryReportView instanceof Region) {
            ((Region) inventoryReportView).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }
    }
    @FXML
    void btnDash(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Home-page.fxml"));

        Parent inventoryReportView = loader.load();
        rootDash.setFillWidth(true);
        rootDash.getChildren().clear();
        rootDash.getChildren().add(inventoryReportView);
        if (inventoryReportView instanceof Region) {
            ((Region) inventoryReportView).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }
    }
    @FXML
    void btnInvent(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/inventory-page.fxml"));

        Parent inventoryReportView = loader.load();
        rootDash.setFillWidth(true);
        rootDash.getChildren().clear();
        rootDash.getChildren().add(inventoryReportView);
        if (inventoryReportView instanceof Region) {
            ((Region) inventoryReportView).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }
    }

    @FXML
    void btnOrder(ActionEvent event) throws IOException {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Order-page.fxml"));

            Parent inventoryReportView = loader.load();

            rootDash.setFillWidth(true);

            rootDash.getChildren().clear();
            rootDash.getChildren().add(inventoryReportView);
            if (inventoryReportView instanceof Region) {
                ((Region) inventoryReportView).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @FXML
    void btnPos(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Pos-page.fxml"));

        Parent inventoryReportView = loader.load();

        rootDash.setFillWidth(true);

        rootDash.getChildren().clear();
        rootDash.getChildren().add(inventoryReportView);
        if (inventoryReportView instanceof Region) {
            ((Region) inventoryReportView).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }

    }

    @FXML
    void btnSuppliers(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Supplier-page.fxml"));

        Parent inventoryReportView = loader.load();

        rootDash.setFillWidth(true);

        rootDash.getChildren().clear();
        rootDash.getChildren().add(inventoryReportView);
        if (inventoryReportView instanceof Region) {
            ((Region) inventoryReportView).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }

    }


}
