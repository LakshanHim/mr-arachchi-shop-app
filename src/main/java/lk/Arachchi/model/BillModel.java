package lk.Arachchi.model;

import lk.Arachchi.db.DBConnection;
import lk.Arachchi.dto.BillDetailDto;
import lk.Arachchi.dto.BillDto;

import java.sql.*;
import java.util.ArrayList;

public class BillModel {
    public static boolean placeOrder(BillDto billDto)  {
        try {
            Connection connection = DBConnection.getDBConnection().getConnection();
            connection.setAutoCommit(false);

            // Insert the bill record
            PreparedStatement preparedStatement = connection.prepareStatement("insert into bill(orderDate,totalAmount) values (?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setObject(1, billDto.getOrderDate());
            preparedStatement.setObject(2, billDto.getBillAmount());
            int i = preparedStatement.executeUpdate();

            if (i > 0) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    int id = resultSet.getInt(1); // Get generated bill ID

                    for (BillDetailDto bDto : billDto.getBillDetails()) {
                        // Insert bill details
                        PreparedStatement preparedStatement1 = connection.prepareStatement("insert into bill_details(Bid,ItemName,Qty,price) values (?,?,?,?)");
                        preparedStatement1.setObject(1, id);
                        preparedStatement1.setObject(2, bDto.getItemName());
                        preparedStatement1.setObject(3, bDto.getQty());
                        preparedStatement1.setObject(4, bDto.gettPrice());
                        int i1 = preparedStatement1.executeUpdate();

                        if (i1 > 0) {
                            // Check if item has a valid ID before updating inventory
                            if (bDto.getItemId() != 0) { // Ensure itemId is not 0 (invalid ID)
                                // Update inventory only if itemId is valid
                                PreparedStatement preparedStatement2 = connection.prepareStatement("update inventory set qty = qty - ? where id = ?");
                                preparedStatement2.setObject(1, bDto.getQty());
                                preparedStatement2.setObject(2, bDto.getItemId());
                                int i2 = preparedStatement2.executeUpdate();

                                if (i2 <= 0) {
                                    connection.rollback();
                                    connection.setAutoCommit(true);
                                    return false; // Rollback if inventory update fails
                                }
                            }
                        } else {
                            connection.rollback();
                            connection.setAutoCommit(true);
                            return false; // Rollback if bill details insertion fails
                        }
                    }
                }

                connection.commit(); // Commit the transaction
                connection.setAutoCommit(true); // Reset auto commit
                return true; // Order placed successfully
            } else {
                connection.rollback();
                connection.setAutoCommit(true);
                return false; // Rollback if bill insertion fails
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false; // Handle any exception and rollback
        }
    }

    public static int recipt() {
        int lastOrderNumber = 0;  // Default value if no orders exist

        String query = "SELECT id FROM bill ORDER BY id DESC LIMIT 1"; // SQL to get the last order number

        try {
            Connection conn = DBConnection.getDBConnection().getConnection(); // Get connection to the DB
            Statement stmt = conn.createStatement(); // Create a statement
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) { // If the result set has a record
                lastOrderNumber = rs.getInt("id");// Fetch the last order number
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());  // Print any SQL exception that occurs
        }
        return lastOrderNumber;
    }

    public static BillDto billSearch(int id) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getDBConnection().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("select * from bill where id = ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        BillDto odr = new BillDto();
        if (resultSet.next()) {
            odr.setOrderDate(resultSet.getString(2));
            odr.setBillAmount(resultSet.getDouble(3));
        }
        return odr;

    }

    public static ArrayList<BillDetailDto> billDetailSearch(int id) throws SQLException, ClassNotFoundException {
        ArrayList<BillDetailDto> billDetailDtos = new ArrayList<>();

        // Establishing database connection
        Connection connection = DBConnection.getDBConnection().getConnection();

        // SQL query to fetch item names for the given order ID
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bill_details WHERE Bid = ?");
        preparedStatement.setInt(1, id);

        ResultSet resultSet = preparedStatement.executeQuery();

        // Fetching item names from the ResultSet
        while (resultSet.next()) {
            // Using column name for clarity
            String itemName = resultSet.getString("itemname");
            int qty = resultSet.getInt("Qty");
            double price = resultSet.getDouble("price");
            billDetailDtos.add(new BillDetailDto(itemName,qty,price));
        }
        return billDetailDtos;
    }
}
