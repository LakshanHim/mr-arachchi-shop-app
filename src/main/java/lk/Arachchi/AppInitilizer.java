package lk.Arachchi;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class AppInitilizer extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the main dashboard page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Dash-page.fxml"));
            BorderPane root = loader.load();
            Scene scene = new Scene(root);
            // Configure the primary stage
            primaryStage.setTitle("Mr. Arachchi Photography Dashboard");
            primaryStage.setMaximized(true);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}







//            BorderPane load = FXMLLoader.load(getClass().getResource("/view/Dash-page.fxml"));
//            Scene scene = new Scene(load);
//            primaryStage.setScene(scene);
//            primaryStage.setMaximized(true);
//            primaryStage.setTitle("Dashboard");
//            primaryStage.centerOnScreen();
//            primaryStage.show();