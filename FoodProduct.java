package model;

import java.time.LocalDate;

public class FoodProduct extends Product {
    private LocalDate expirationDate;
    private boolean isOrganic;

    public FoodProduct() {
        super();
    }

    public FoodProduct(int id, String name, String description, double price,
                       int quantity, String categoryId, LocalDate expirationDate, boolean isOrganic) {
        super(id, name, description, price, quantity, categoryId);
        this.expirationDate = expirationDate;
        this.isOrganic = isOrganic;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isOrganic() {
        return isOrganic;
    }

    public void setOrganic(boolean organic) {
        isOrganic = organic;
    }

    public String getProductInfo() {
        String organikMi = isOrganic ? "Organik" : "Normal";
        return "Gida: " + getName() + " - " + organikMi +
                " - SKT: " + expirationDate + " - Fiyat: " + getPrice() + " TL";
    }

    public String toString() {
        return super.toString() + ";" + expirationDate + ";" + isOrganic + ";FOOD";
    }
}