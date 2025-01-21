package lk.Arachchi.dto;

public class BillDetailDto {
    private int itemId;
    private int qty;
    private double tPrice;
    private String itemName;

    public BillDetailDto(int itemId, int qty, double tPrice) {
        this.itemId = itemId;
        this.qty = qty;
        this.tPrice = tPrice;
    }

    public BillDetailDto(int itemId, String itemName, int qty, double tPrice) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.qty = qty;
        this.tPrice = tPrice;
    }

    public BillDetailDto(String itemName, int qty, double tPrice) {
        this.itemName = itemName;
        this.qty = qty;
        this.tPrice = tPrice;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double gettPrice() {
        return tPrice;
    }

    public void settPrice(double tPrice) {
        this.tPrice = tPrice;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
