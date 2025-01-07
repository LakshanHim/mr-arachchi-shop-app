package lk.Arachchi.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DashPageController {

    @FXML
    private VBox rootDash;

    @FXML
    private ScrollPane scrollPanePage;

    @FXML
    private Label setDate;

    @FXML
    private Label setTime;

    @FXML
    public void initialize() {
        // Fast scrolling logic
        scrollPanePage.addEventFilter(ScrollEvent.SCROLL, event -> {
            double deltaY = event.getDeltaY() * 6;  // Adjust speed multiplier
            scrollPanePage.setVvalue(scrollPanePage.getVvalue() - deltaY / scrollPanePage.getContent().getBoundsInLocal().getHeight());
            event.consume();
        });
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String format = formatter.format(date);
        setDate.setText("\uD83D\uDCC5"+format);

        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss a");
            String currentTime = timeFormatter.format(new Date());
            setTime.setText("⏰"+currentTime);
        }));
        clock.setCycleCount(Timeline.INDEFINITE); // Run indefinitely
        clock.play(); // Start the clock
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
