package model;

public class ElectronicProduct extends Product {
    private int warrantyMonths;
    private String brand;

    public ElectronicProduct() {
        super();
    }

    public ElectronicProduct(int id, String name, String description, double price,
                             int quantity, String categoryId, int warrantyMonths, String brand) {
        super(id, name, description, price, quantity, categoryId);
        this.warrantyMonths = warrantyMonths;
        this.brand = brand;
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(int warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getProductInfo() {
        return "Elektronik: " + getName() + " - Marka: " + brand +
                " - Garanti: " + warrantyMonths + " ay - Fiyat: " + getPrice() + " TL";
    }

    public String toString() {
        return super.toString() + ";" + warrantyMonths + ";" + brand + ";ELECTRONIC";
    }
}