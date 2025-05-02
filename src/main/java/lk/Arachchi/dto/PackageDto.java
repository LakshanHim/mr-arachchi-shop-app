package lk.Arachchi.dto;

public class PackageDto {
    private String packageName;
    private String additionalItems;
    private String coverageTime;
    private double price;

    public PackageDto(String packageName, String additionalItems, String coverageTime, double price) {
        this.packageName = packageName;
        this.additionalItems = additionalItems;
        this.coverageTime = coverageTime;
        this.price = price;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAdditionalItems() {
        return additionalItems;
    }

    public void setAdditionalItems(String additionalItems) {
        this.additionalItems = additionalItems;
    }

    public String getCoverageTime() {
        return coverageTime;
    }

    public void setCoverageTime(String coverageTime) {
        this.coverageTime = coverageTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
