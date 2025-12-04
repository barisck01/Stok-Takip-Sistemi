package business;

import data.FileDataService;
import data.IDataService;
import model.*;

import java.util.*;
import java.util.stream.Collectors;

public class ProductService {
    private IDataService<Product> productDataService;
    private IDataService<StockMovement> stockMovementDataService;
    private Map<String, Category> categoryMap;

    public ProductService() {
        this.productDataService = new FileDataService<>("data/products.txt", Product.class);
        this.stockMovementDataService = new FileDataService<>("data/stock_movements.txt", StockMovement.class);
        this.categoryMap = new HashMap<>();
        initializeCategories();
    }

    private void initializeCategories() {
        categoryMap.put("CAT001", new Category("CAT001", "Elektronik", "Elektronik urunler"));
        categoryMap.put("CAT002", new Category("CAT002", "Gida", "Gida urunleri"));
        categoryMap.put("CAT003", new Category("CAT003", "Giyim", "Giyim urunleri"));
        categoryMap.put("CAT004", new Category("CAT004", "Ev ve Yasam", "Ev ve yasam urunleri"));
    }

    public void addProduct(Product product) {
        productDataService.save(product);
    }

    public void updateProduct(Product product) {
        productDataService.update(product);
    }

    public void deleteProduct(int id) {
        productDataService.delete(id);
    }

    public Product getProductById(int id) {
        return productDataService.getById(id);
    }

    public List<Product> getAllProducts() {
        return productDataService.getAll();
    }

    public List<Product> searchByName(String name) {
        return getAllProducts().stream()
                .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Product> filterByCategory(String categoryId) {
        return getAllProducts().stream()
                .filter(p -> p.getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
    }

    public List<Product> getLowStockProducts(int minQuantity) {
        return getAllProducts().stream()
                .filter(p -> p.getQuantity() < minQuantity)
                .collect(Collectors.toList());
    }

    public void addStockMovement(StockMovement movement) {
        stockMovementDataService.save(movement);
        Product product = getProductById(movement.getProductId());
        if (product != null) {
            if (movement.getMovementType().equals("IN")) {
                product.setQuantity(product.getQuantity() + movement.getQuantity());
            } else if (movement.getMovementType().equals("OUT")) {
                product.setQuantity(product.getQuantity() - movement.getQuantity());
            }
            updateProduct(product);
        }
    }

    public List<StockMovement> getAllStockMovements() {
        return stockMovementDataService.getAll();
    }

    public Map<String, Category> getAllCategories() {
        return categoryMap;
    }

    public Category getCategoryById(String id) {
        return categoryMap.get(id);
    }

    public int generateNewProductId() {
        List<Product> products = getAllProducts();
        if (products.isEmpty()) {
            return 1;
        }
        return products.stream()
                .mapToInt(Product::getId)
                .max()
                .orElse(0) + 1;
    }

    public int generateNewMovementId() {
        List<StockMovement> movements = getAllStockMovements();
        if (movements.isEmpty()) {
            return 1;
        }
        return movements.stream()
                .mapToInt(StockMovement::getId)
                .max()
                .orElse(0) + 1;
    }
}