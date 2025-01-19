package lk.Arachchi.dto;

public class InventoryDto {
    private String name;
    private double price;
    private int qty;
    private String sku;

    public InventoryDto(String name, double price, int qty, String sku) {
        this.name = name;
        this.price = price;
        this.qty = qty;
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
