package lk.Arachchi.model;

import lk.Arachchi.db.DBConnection;
import lk.Arachchi.dto.InventoryDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InventoryModel {
    public static boolean addItem(InventoryDto inventoryDto){
        try {
            Connection connection = DBConnection.getDBConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("insert into inventory(name,price,qty,sku) values(?,?,?,?)");
            preparedStatement.setString(1, inventoryDto.getName());
            preparedStatement.setDouble(2, inventoryDto.getPrice());
            preparedStatement.setInt(3, inventoryDto.getQty());
            preparedStatement.setString(4, inventoryDto.getSku());
            int i = preparedStatement.executeUpdate();
            if(i>0){
                return true;
            }else {
                return false;
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
