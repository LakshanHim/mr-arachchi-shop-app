package lk.Arachchi.dto;

import java.util.ArrayList;

public class BillDto {
    private String orderDate;
    private double billAmount;
    private ArrayList<BillDetailDto> billDetails;

    public BillDto(String orderDate, double billAmount, ArrayList<BillDetailDto> billDetails) {
        this.orderDate = orderDate;
        this.billAmount = billAmount;
        this.billDetails = billDetails;
    }

    public BillDto(String orderDate, double billAmount) {
        this.orderDate = orderDate;
        this.billAmount = billAmount;
    }

    public BillDto() {

    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(double billAmount) {
        this.billAmount = billAmount;
    }

    public ArrayList<BillDetailDto> getBillDetails() {
        return billDetails;
    }

    public void setBillDetails(ArrayList<BillDetailDto> billDetails) {
        this.billDetails = billDetails;
    }
}
