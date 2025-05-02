package lk.Arachchi.tm;

public class PackageTM {
    private int id;
    private String package_Name;
    private String additional_Items;
    private String coverage_Time;
    private double price;

    // Constructor
    public PackageTM(int id, String package_Name, String additional_Items, String coverage_Time, double price) {
        this.id = id;
        this.package_Name = package_Name;
        this.additional_Items = additional_Items;
        this.coverage_Time = coverage_Time;
        this.price = price;
    }

    // Getter methods
    public int getId() {
        return id;
    }

    public String getPackage_Name() {
        return package_Name;
    }

    public String getAdditional_Items() {
        return additional_Items;
    }

    public String getCoverage_Time() {
        return coverage_Time;
    }

    public double getPrice() {
        return price;
    }
}

