package ui;

import business.ProductService;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MainFrame extends JFrame {
    private ProductService productService;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel totalProductLabel;
    private JLabel lowStockLabel;

    private Color primaryColor = new Color(41, 128, 185);
    private Color successColor = new Color(39, 174, 96);
    private Color dangerColor = new Color(192, 57, 43);
    private Color warningColor = new Color(243, 156, 18);
    private Color bgColor = new Color(236, 240, 241);
    private Color cardColor = Color.WHITE;

    public MainFrame() {
        productService = new ProductService();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Stok Kontrol Sistemi - 20220305065");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgColor);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(bgColor);

        JPanel statsPanel = createStatsPanel();
        centerPanel.add(statsPanel, BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel();
        centerPanel.add(tablePanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);
        refreshTable();
        updateStats();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("STOK KONTROL SISTEMI");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(primaryColor);

        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        searchPanel.add(searchField);

        JButton searchButton = createStyledButton("Ara", Color.WHITE, primaryColor);
        searchButton.addActionListener(e -> searchProducts());
        searchPanel.add(searchButton);

        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(bgColor);

        totalProductLabel = new JLabel("0");
        JPanel totalCard = createStatCard("Toplam Urun", totalProductLabel, primaryColor);
        statsPanel.add(totalCard);

        lowStockLabel = new JLabel("0");
        JPanel lowStockCard = createStatCard("Dusuk Stok", lowStockLabel, dangerColor);
        statsPanel.add(lowStockCard);

        JPanel categoryCard = createStatCard("Kategori", new JLabel("4"), successColor);
        statsPanel.add(categoryCard);

        JPanel activeCard = createStatCard("Durum", new JLabel("Aktif"), warningColor);
        statsPanel.add(activeCard);

        return statsPanel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setPreferredSize(new Dimension(5, 0));
        card.add(colorBar, BorderLayout.WEST);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setBackground(cardColor);
        textPanel.setBorder(new EmptyBorder(0, 15, 0, 0));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLbl.setForeground(Color.GRAY);
        textPanel.add(titleLbl);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 22));
        valueLabel.setForeground(color);
        textPanel.add(valueLabel);

        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(0, 15));
        tablePanel.setBackground(bgColor);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(bgColor);

        JButton addButton = createStyledButton("+ Urun Ekle", successColor, Color.WHITE);
        addButton.addActionListener(e -> showAddProductDialog());
        buttonPanel.add(addButton);

        JButton editButton = createStyledButton("Duzenle", primaryColor, Color.WHITE);
        editButton.addActionListener(e -> showEditProductDialog());
        buttonPanel.add(editButton);

        JButton deleteButton = createStyledButton("Sil", dangerColor, Color.WHITE);
        deleteButton.addActionListener(e -> deleteSelectedProduct());
        buttonPanel.add(deleteButton);

        JButton stockInButton = createStyledButton("Stok Girisi", successColor, Color.WHITE);
        stockInButton.addActionListener(e -> showStockMovementDialog("IN"));
        buttonPanel.add(stockInButton);

        JButton stockOutButton = createStyledButton("Stok Cikisi", warningColor, Color.WHITE);
        stockOutButton.addActionListener(e -> showStockMovementDialog("OUT"));
        buttonPanel.add(stockOutButton);

        JButton refreshButton = createStyledButton("Yenile", new Color(149, 165, 166), Color.WHITE);
        refreshButton.addActionListener(e -> {
            refreshTable();
            updateStats();
        });
        buttonPanel.add(refreshButton);

        tablePanel.add(buttonPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Urun Adi", "Aciklama", "Fiyat", "Stok", "Kategori", "Tur"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(tableModel);
        productTable.setFont(new Font("Arial", Font.PLAIN, 13));
        productTable.setRowHeight(35);
        productTable.setSelectionBackground(new Color(52, 152, 219));
        productTable.setSelectionForeground(Color.WHITE);
        productTable.setGridColor(new Color(230, 230, 230));
        productTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        productTable.getTableHeader().setBackground(primaryColor);
        productTable.getTableHeader().setForeground(Color.WHITE);
        productTable.getTableHeader().setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < productTable.getColumnCount(); i++) {
            productTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        return button;
    }

    private void updateStats() {
        List<Product> products = productService.getAllProducts();
        totalProductLabel.setText(String.valueOf(products.size()));

        long lowStock = products.stream().filter(p -> p.getQuantity() < 10).count();
        lowStockLabel.setText(String.valueOf(lowStock));
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Product> products = productService.getAllProducts();

        for (Product product : products) {
            String categoryName = "Bilinmiyor";
            Category cat = productService.getCategoryById(product.getCategoryId());
            if (cat != null) {
                categoryName = cat.getName();
            }

            String type = "Normal";
            if (product instanceof ElectronicProduct) {
                type = "Elektronik";
            } else if (product instanceof FoodProduct) {
                type = "Gida";
            }

            Object[] row = {
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice() + " TL",
                    product.getQuantity(),
                    categoryName,
                    type
            };
            tableModel.addRow(row);
        }
    }

    private void searchProducts() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            refreshTable();
            return;
        }

        tableModel.setRowCount(0);
        List<Product> results = productService.searchByName(searchText);

        for (Product product : results) {
            String categoryName = "Bilinmiyor";
            Category cat = productService.getCategoryById(product.getCategoryId());
            if (cat != null) {
                categoryName = cat.getName();
            }

            String type = "Normal";
            if (product instanceof ElectronicProduct) {
                type = "Elektronik";
            } else if (product instanceof FoodProduct) {
                type = "Gida";
            }

            Object[] row = {
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice() + " TL",
                    product.getQuantity(),
                    categoryName,
                    type
            };
            tableModel.addRow(row);
        }
    }

    private void showAddProductDialog() {
        JDialog dialog = new JDialog(this, "Yeni Urun Ekle", true);
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(bgColor);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createLabel("Urun Tipi:"), gbc);

        gbc.gridx = 1;
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Normal", "Elektronik", "Gida"});
        styleComboBox(typeCombo);
        panel.add(typeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(createLabel("Urun Adi:"), gbc);

        gbc.gridx = 1;
        JTextField nameField = createTextField();
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(createLabel("Aciklama:"), gbc);

        gbc.gridx = 1;
        JTextField descField = createTextField();
        panel.add(descField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(createLabel("Fiyat:"), gbc);

        gbc.gridx = 1;
        JTextField priceField = createTextField();
        panel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(createLabel("Stok Miktari:"), gbc);

        gbc.gridx = 1;
        JTextField quantityField = createTextField();
        panel.add(quantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(createLabel("Kategori:"), gbc);

        gbc.gridx = 1;
        JComboBox<String> categoryCombo = new JComboBox<>();
        productService.getAllCategories().forEach((id, cat) ->
                categoryCombo.addItem(id + " - " + cat.getName())
        );
        styleComboBox(categoryCombo);
        panel.add(categoryCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel extra1Label = createLabel("Ekstra 1:");
        panel.add(extra1Label, gbc);

        gbc.gridx = 1;
        JTextField extra1Field = createTextField();
        panel.add(extra1Field, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        JLabel extra2Label = createLabel("Ekstra 2:");
        panel.add(extra2Label, gbc);

        gbc.gridx = 1;
        JTextField extra2Field = createTextField();
        panel.add(extra2Field, gbc);

        typeCombo.addActionListener(e -> {
            String selected = typeCombo.getSelectedItem().toString();
            if (selected.equals("Elektronik")) {
                extra1Label.setText("Garanti (ay):");
                extra2Label.setText("Marka:");
            } else if (selected.equals("Gida")) {
                extra1Label.setText("SKT (yyyy-MM-dd):");
                extra2Label.setText("Organik (true/false):");
            } else {
                extra1Label.setText("Ekstra 1:");
                extra2Label.setText("Ekstra 2:");
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 8, 5);
        JButton saveButton = createStyledButton("Kaydet", successColor, Color.WHITE);
        saveButton.addActionListener(e -> {
            try {
                int id = productService.generateNewProductId();
                String name = nameField.getText();
                String desc = descField.getText();
                double price = Double.parseDouble(priceField.getText());
                int quantity = Integer.parseInt(quantityField.getText());
                String categoryId = categoryCombo.getSelectedItem().toString().split(" - ")[0];

                Product product;
                String type = typeCombo.getSelectedItem().toString();

                if (type.equals("Elektronik")) {
                    int warranty = Integer.parseInt(extra1Field.getText());
                    String brand = extra2Field.getText();
                    product = new ElectronicProduct(id, name, desc, price, quantity, categoryId, warranty, brand);
                } else if (type.equals("Gida")) {
                    LocalDate expDate = LocalDate.parse(extra1Field.getText());
                    boolean isOrganic = Boolean.parseBoolean(extra2Field.getText());
                    product = new FoodProduct(id, name, desc, price, quantity, categoryId, expDate, isOrganic);
                } else {
                    product = new Product(id, name, desc, price, quantity, categoryId);
                }

                productService.addProduct(product);
                refreshTable();
                updateStats();
                dialog.dispose();
                showSuccessMessage("Urun basariyla eklendi!");
            } catch (Exception ex) {
                showErrorMessage("Hata: " + ex.getMessage());
            }
        });
        panel.add(saveButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditProductDialog() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            showErrorMessage("Lutfen duzenlenecek urunu secin!");
            return;
        }

        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        Product product = productService.getProductById(productId);

        if (product == null) {
            showErrorMessage("Urun bulunamadi!");
            return;
        }

        JDialog dialog = new JDialog(this, "Urun Duzenle", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(bgColor);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createLabel("Urun Adi:"), gbc);

        gbc.gridx = 1;
        JTextField nameField = createTextField();
        nameField.setText(product.getName());
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(createLabel("Aciklama:"), gbc);

        gbc.gridx = 1;
        JTextField descField = createTextField();
        descField.setText(product.getDescription());
        panel.add(descField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(createLabel("Fiyat:"), gbc);

        gbc.gridx = 1;
        JTextField priceField = createTextField();
        priceField.setText(String.valueOf(product.getPrice()));
        panel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(createLabel("Stok Miktari:"), gbc);

        gbc.gridx = 1;
        JTextField quantityField = createTextField();
        quantityField.setText(String.valueOf(product.getQuantity()));
        panel.add(quantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(createLabel("Kategori:"), gbc);

        gbc.gridx = 1;
        JComboBox<String> categoryCombo = new JComboBox<>();
        productService.getAllCategories().forEach((id, cat) ->
                categoryCombo.addItem(id + " - " + cat.getName())
        );
        styleComboBox(categoryCombo);
        panel.add(categoryCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 8, 5);
        JButton saveButton = createStyledButton("Guncelle", primaryColor, Color.WHITE);
        saveButton.addActionListener(e -> {
            try {
                product.setName(nameField.getText());
                product.setDescription(descField.getText());
                product.setPrice(Double.parseDouble(priceField.getText()));
                product.setQuantity(Integer.parseInt(quantityField.getText()));
                product.setCategoryId(categoryCombo.getSelectedItem().toString().split(" - ")[0]);

                productService.updateProduct(product);
                refreshTable();
                updateStats();
                dialog.dispose();
                showSuccessMessage("Urun basariyla guncellendi!");
            } catch (Exception ex) {
                showErrorMessage("Hata: " + ex.getMessage());
            }
        });
        panel.add(saveButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            showErrorMessage("Lutfen silinecek urunu secin!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bu urunu silmek istediginize emin misiniz?",
                "Silme Onayi",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int productId = (int) tableModel.getValueAt(selectedRow, 0);
            productService.deleteProduct(productId);
            refreshTable();
            updateStats();
            showSuccessMessage("Urun silindi!");
        }
    }

    private void showStockMovementDialog(String type) {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            showErrorMessage("Lutfen urun secin!");
            return;
        }

        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        String productName = (String) tableModel.getValueAt(selectedRow, 1);

        String title = type.equals("IN") ? "Stok Girisi" : "Stok Cikisi";
        Color titleColor = type.equals("IN") ? successColor : warningColor;

        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(400, 280);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(bgColor);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createLabel("Urun:"), gbc);

        gbc.gridx = 1;
        JLabel productLabel = new JLabel(productName);
        productLabel.setFont(new Font("Arial", Font.BOLD, 14));
        productLabel.setForeground(titleColor);
        panel.add(productLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(createLabel("Miktar:"), gbc);

        gbc.gridx = 1;
        JTextField quantityField = createTextField();
        panel.add(quantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(createLabel("Aciklama:"), gbc);

        gbc.gridx = 1;
        JTextField descField = createTextField();
        panel.add(descField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 8, 5);
        JButton saveButton = createStyledButton("Kaydet", titleColor, Color.WHITE);
        saveButton.addActionListener(e -> {
            try {
                int movementId = productService.generateNewMovementId();
                int quantity = Integer.parseInt(quantityField.getText());
                String desc = descField.getText();

                StockMovement movement = new StockMovement(
                        movementId, productId, quantity, type, LocalDateTime.now(), desc
                );

                productService.addStockMovement(movement);
                refreshTable();
                updateStats();
                dialog.dispose();

                String msg = type.equals("IN") ? "Stok girisi yapildi!" : "Stok cikisi yapildi!";
                showSuccessMessage(msg);
            } catch (Exception ex) {
                showErrorMessage("Hata: " + ex.getMessage());
            }
        });
        panel.add(saveButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(new Color(52, 73, 94));
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private void styleComboBox(JComboBox comboBox) {
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Basarili", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Hata", JOptionPane.ERROR_MESSAGE);
    }
}