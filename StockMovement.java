package model;

import java.time.LocalDateTime;

public class StockMovement {
    private int id;
    private int productId;
    private int quantity;
    private String movementType;
    private LocalDateTime date;
    private String description;

    public StockMovement() {
    }

    public StockMovement(int id, int productId, int quantity, String movementType,
                         LocalDateTime date, String description) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.movementType = movementType;
        this.date = date;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return id + ";" + productId + ";" + quantity + ";" + movementType + ";" + date + ";" + description;
    }
}