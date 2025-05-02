package lk.Arachchi.model;

import javafx.scene.control.Alert;
import lk.Arachchi.db.DBConnection;
import lk.Arachchi.dto.PackageDto;
import lk.Arachchi.tm.PackageTM;

import java.sql.*;
import java.util.ArrayList;

public class PackageModel {


    public static boolean addPackage(PackageDto packageDto) throws SQLException, ClassNotFoundException {
        Connection connection1 = DBConnection.getDBConnection().getConnection();

        String query = "INSERT INTO package (package_name, additional_items, coverage_time, price) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connection1.prepareStatement(query);
            // Set parameters for the query
            preparedStatement.setString(1, packageDto.getPackageName());
            preparedStatement.setString(2, packageDto.getAdditionalItems());
            preparedStatement.setString(3, packageDto.getCoverageTime());
            preparedStatement.setDouble(4, packageDto.getPrice());

            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected>0){
                return true;
            }
            else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<PackageTM> LoadForm() {
        ArrayList<PackageTM> tms = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Establish connection
            connection = DBConnection.getDBConnection().getConnection();

            // Prepare the SQL statement
            preparedStatement = connection.prepareStatement("SELECT * FROM package");

            // Execute the query
            resultSet = preparedStatement.executeQuery();

            // Loop through result set and add to the list
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String packageName = resultSet.getString(2);
                String additionalItems = resultSet.getString(3);
                String coverageTime = resultSet.getString(4);
                double price = resultSet.getDouble(5);

                PackageTM packageTM = new PackageTM(id, packageName, additionalItems, coverageTime, price);

                tms.add(packageTM);

            }

        } catch (Exception e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("An error occurred");
            errorAlert.setContentText("Something went wrong. Please try again.");
            errorAlert.showAndWait();
            e.printStackTrace();
        }
        return tms;
    }


}
