package data;

import model.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class FileDataService<T> implements IDataService<T> {
    private String filePath;
    private Class<T> type;

    public FileDataService(String filePath, Class<T> type) {
        this.filePath = filePath;
        this.type = type;
        createFileIfNotExists();
    }

    private void createFileIfNotExists() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(T item) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
            writer.write(item.toString());
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(T item) {
        List<T> items = getAll();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            for (T existingItem : items) {
                if (getId(existingItem) == getId(item)) {
                    writer.write(item.toString());
                } else {
                    writer.write(existingItem.toString());
                }
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        List<T> items = getAll();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            for (T item : items) {
                if (getId(item) != id) {
                    writer.write(item.toString());
                    writer.newLine();
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public T getById(int id) {
        List<T> allItems = getAll();
        for (T item : allItems) {
            if (getId(item) == id) {
                return item;
            }
        }
        return null;
    }

    public List<T> getAll() {
        List<T> items = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    T item = parseLine(line);
                    if (item != null) {
                        items.add(item);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }

    private int getId(T item) {
        if (item instanceof Product) {
            return ((Product) item).getId();
        } else if (item instanceof StockMovement) {
            return ((StockMovement) item).getId();
        }
        return -1;
    }

    private T parseLine(String line) {
        String[] parts = line.split(";");
        try {
            if (type == Product.class || type == ElectronicProduct.class || type == FoodProduct.class) {
                if (parts.length >= 10 && parts[parts.length - 1].equals("ELECTRONIC")) {
                    ElectronicProduct ep = new ElectronicProduct();
                    ep.setId(Integer.parseInt(parts[0]));
                    ep.setName(parts[1]);
                    ep.setDescription(parts[2]);
                    ep.setPrice(Double.parseDouble(parts[3]));
                    ep.setQuantity(Integer.parseInt(parts[4]));
                    ep.setCategoryId(parts[5]);
                    ep.setWarrantyMonths(Integer.parseInt(parts[6]));
                    ep.setBrand(parts[7]);
                    return (T) ep;
                } else if (parts.length >= 10 && parts[parts.length - 1].equals("FOOD")) {
                    FoodProduct fp = new FoodProduct();
                    fp.setId(Integer.parseInt(parts[0]));
                    fp.setName(parts[1]);
                    fp.setDescription(parts[2]);
                    fp.setPrice(Double.parseDouble(parts[3]));
                    fp.setQuantity(Integer.parseInt(parts[4]));
                    fp.setCategoryId(parts[5]);
                    fp.setExpirationDate(LocalDate.parse(parts[6]));
                    fp.setOrganic(Boolean.parseBoolean(parts[7]));
                    return (T) fp;
                } else {
                    Product p = new Product();
                    p.setId(Integer.parseInt(parts[0]));
                    p.setName(parts[1]);
                    p.setDescription(parts[2]);
                    p.setPrice(Double.parseDouble(parts[3]));
                    p.setQuantity(Integer.parseInt(parts[4]));
                    p.setCategoryId(parts[5]);
                    return (T) p;
                }
            } else if (type == StockMovement.class) {
                StockMovement sm = new StockMovement();
                sm.setId(Integer.parseInt(parts[0]));
                sm.setProductId(Integer.parseInt(parts[1]));
                sm.setQuantity(Integer.parseInt(parts[2]));
                sm.setMovementType(parts[3]);
                sm.setDate(LocalDateTime.parse(parts[4]));
                sm.setDescription(parts[5]);
                return (T) sm;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}