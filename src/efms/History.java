package efms;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class History {
    private int userId;
    private Dashboard dashboard;
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField, yearField;
    private JComboBox<String> monthBox;
    private JLabel monthlyIncomeLabel, monthlyExpenseLabel;
    private String currentSort = "date DESC";
    private Map<Integer, Integer> rowToIdMap = new HashMap<>();
    
    public JPanel createHistoryPanel(int userId, Dashboard dashboard) {
        this.userId = userId;
        this.dashboard = dashboard;
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(40, 40, 40));
        topPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(new Color(40, 40, 40));
        
        monthBox = new JComboBox<>(new String[]{
            "Month", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        });
        monthBox.setPreferredSize(new Dimension(110, 28));
        leftPanel.add(monthBox);
        
        yearField = new JTextField("Year", 8);
        yearField.setPreferredSize(new Dimension(80, 28));
        yearField.setForeground(Color.GRAY);
        yearField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (yearField.getText().equals("Year")) {
                    yearField.setText("");
                    yearField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (yearField.getText().isEmpty()) {
                    yearField.setText("Year");
                    yearField.setForeground(Color.GRAY);
                }
            }
        });
        leftPanel.add(yearField);
        
        JButton filterBtn = new JButton("Filter");
        filterBtn.setBackground(new Color(0, 150, 136));
        filterBtn.setForeground(Color.WHITE);
        filterBtn.setFocusPainted(false);
        filterBtn.setPreferredSize(new Dimension(80, 28));
        filterBtn.addActionListener(e -> loadTransactions());
        leftPanel.add(filterBtn);
        
        monthlyIncomeLabel = new JLabel("");
        monthlyIncomeLabel.setForeground(new Color(76, 175, 80));
        monthlyIncomeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        leftPanel.add(monthlyIncomeLabel);
        
        monthlyExpenseLabel = new JLabel("");
        monthlyExpenseLabel.setForeground(new Color(244, 67, 54));
        monthlyExpenseLabel.setFont(new Font("Arial", Font.BOLD, 12));
        leftPanel.add(monthlyExpenseLabel);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(new Color(40, 40, 40));
        
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 28));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loadTransactions();
                }
            }
        });
        rightPanel.add(searchField);
        
        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(0, 150, 136));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setPreferredSize(new Dimension(90, 28));
        searchBtn.addActionListener(e -> loadTransactions());
        rightPanel.add(searchBtn);
        
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);
        
        model = new DefaultTableModel(
            new String[]{"Description", "Type", "Category", "Amount", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(model);
        table.setBackground(new Color(45, 45, 45));
        table.setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(0, 150, 136));
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setShowVerticalLines(true);
        table.setGridColor(new Color(60, 60, 60));
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.LEFT);
                setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 10));
                
                if (isSelected) {
                    setBackground(new Color(0, 150, 136));
                    setForeground(Color.WHITE);
                } else {
                    setBackground(new Color(45, 45, 45));
                    setForeground(Color.WHITE);
                }
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(35, 35, 35));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel panel = new JPanel(new BorderLayout());
                panel.setBackground(new Color(35, 35, 35));
                panel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 10));
                
                JLabel textLabel = new JLabel(value.toString());
                textLabel.setForeground(Color.WHITE);
                textLabel.setFont(new Font("Arial", Font.BOLD, 13));
                
                JLabel arrowLabel = new JLabel("↕");
                arrowLabel.setForeground(Color.WHITE);
                arrowLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                
                panel.add(textLabel, BorderLayout.WEST);
                panel.add(arrowLabel, BorderLayout.EAST);
                
                return panel;
            }
        };
        
        for (int i = 0; i < header.getColumnModel().getColumnCount(); i++) {
            header.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
        
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = header.columnAtPoint(e.getPoint());
                String columnName = model.getColumnName(col);
                
                if (columnName.equals("Description")) {
                    currentSort = currentSort.equals("description ASC") ? "description DESC" : "description ASC";
                } else if (columnName.equals("Type")) {
                    currentSort = currentSort.equals("type ASC") ? "type DESC" : "type ASC";
                } else if (columnName.equals("Category")) {
                    currentSort = currentSort.equals("category ASC") ? "category DESC" : "category ASC";
                } else if (columnName.equals("Amount")) {
                    currentSort = currentSort.equals("amount ASC") ? "amount DESC" : "amount ASC";
                } else if (columnName.equals("Date")) {
                    currentSort = currentSort.equals("date ASC") ? "date DESC" : "date ASC";
                }
                
                loadTransactions();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(45, 45, 45));
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 10, 0, 10),
            BorderFactory.createEmptyBorder()
        ));
        scrollPane.setBackground(new Color(30, 30, 30));
        scrollPane.getViewport().setOpaque(true);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(40, 40, 40));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftButtons.setBackground(new Color(40, 40, 40));
        
        JButton updateBtn = new JButton("Update");
        updateBtn.setBackground(new Color(33, 150, 243));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFocusPainted(false);
        updateBtn.setPreferredSize(new Dimension(90, 32));
        updateBtn.addActionListener(e -> updateTransaction());
        leftButtons.add(updateBtn);
        
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBackground(new Color(244, 67, 54));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setPreferredSize(new Dimension(90, 32));
        deleteBtn.addActionListener(e -> deleteTransaction());
        leftButtons.add(deleteBtn);
        
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightButtons.setBackground(new Color(40, 40, 40));
        
        JButton importBtn = new JButton("Import");
        importBtn.setBackground(new Color(100, 100, 100));
        importBtn.setForeground(Color.WHITE);
        importBtn.setFocusPainted(false);
        importBtn.setPreferredSize(new Dimension(90, 32));
        importBtn.addActionListener(e -> importCSV());
        rightButtons.add(importBtn);
        
        JButton exportBtn = new JButton("Export");
        exportBtn.setBackground(new Color(100, 100, 100));
        exportBtn.setForeground(Color.WHITE);
        exportBtn.setFocusPainted(false);
        exportBtn.setPreferredSize(new Dimension(90, 32));
        exportBtn.addActionListener(e -> exportCSV());
        rightButtons.add(exportBtn);
        
        bottomPanel.add(leftButtons, BorderLayout.WEST);
        bottomPanel.add(rightButtons, BorderLayout.EAST);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        loadTransactions();
        return mainPanel;
    }
    
    private void loadTransactions() {
        model.setRowCount(0);
        rowToIdMap.clear();
        
        StringBuilder sql = new StringBuilder("SELECT id, description, type, category, amount, date FROM transactions WHERE user_id=?");
        List<Object> params = new ArrayList<>();
        params.add(userId);
        
        String search = searchField.getText().trim();
        if (!search.isEmpty()) {
            sql.append(" AND description LIKE ?");
            params.add("%" + search + "%");
        }
        
        String month = (String) monthBox.getSelectedItem();
        if (!month.equals("Month")) {
            int monthNum = monthBox.getSelectedIndex();
            sql.append(" AND MONTH(date)=?");
            params.add(monthNum);
        }
        
        String year = yearField.getText().trim();
        if (!year.isEmpty() && !year.equals("Year")) {
            try {
                sql.append(" AND YEAR(date)=?");
                params.add(Integer.parseInt(year));
            } catch (NumberFormatException e) {}
        }
        
        sql.append(" ORDER BY ").append(currentSort);
        
        try (Connection conn = Database.connect()) {
            PreparedStatement stmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            double totalIncome = 0, totalExpense = 0;
            int rowIndex = 0;
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");
                
                if (type.equals("Income")) totalIncome += amount;
                else totalExpense += amount;
                
                model.addRow(new Object[]{
                    rs.getString("description"),
                    type,
                    rs.getString("category"),
                    String.format("$%.2f", amount),
                    rs.getDate("date")
                });
                
                rowToIdMap.put(rowIndex, id);
                rowIndex++;
            }
            
            if (!month.equals("Month") || (!year.isEmpty() && !year.equals("Year"))) {
                monthlyIncomeLabel.setText(String.format("Income: $%.2f", totalIncome));
                monthlyExpenseLabel.setText(String.format("Expense: $%.2f", totalExpense));
            } else {
                monthlyIncomeLabel.setText("");
                monthlyExpenseLabel.setText("");
            }
        } catch (SQLException ex) {
            System.out.println("Load error: " + ex.getMessage());
        }
    }
    
    private int getSelectedId() {
        int row = table.getSelectedRow();
        if (row == -1) return -1;
        return rowToIdMap.getOrDefault(row, -1);
    }
    
    private void updateTransaction() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Select a transaction!");
            return;
        }
        
        int id = getSelectedId();
        if (id == -1) return;
        
        JDialog dialog = new JDialog((Frame) null, "Update Transaction", true);
        dialog.setSize(420, 400);
        dialog.setLocationRelativeTo(null);
        dialog.getContentPane().setBackground(new Color(30, 30, 30));
        dialog.setLayout(null);
        
        int y = 30;
        
        JLabel descLabel = new JLabel("Description:");
        descLabel.setBounds(40, y, 100, 25);
        descLabel.setForeground(Color.WHITE);
        dialog.add(descLabel);
        
        JTextField descField = new JTextField((String) model.getValueAt(row, 0));
        descField.setBounds(150, y, 220, 28);
        dialog.add(descField);
        y += 45;
        
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setBounds(40, y, 100, 25);
        typeLabel.setForeground(Color.WHITE);
        dialog.add(typeLabel);
        
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Expense", "Income"});
        typeBox.setSelectedItem(model.getValueAt(row, 1));
        typeBox.setBounds(150, y, 220, 28);
        dialog.add(typeBox);
        y += 45;
        
        JLabel catLabel = new JLabel("Category:");
        catLabel.setBounds(40, y, 100, 25);
        catLabel.setForeground(Color.WHITE);
        dialog.add(catLabel);
        
        JTextField catField = new JTextField((String) model.getValueAt(row, 2));
        catField.setBounds(150, y, 220, 28);
        dialog.add(catField);
        y += 45;
        
        JLabel amtLabel = new JLabel("Amount:");
        amtLabel.setBounds(40, y, 100, 25);
        amtLabel.setForeground(Color.WHITE);
        dialog.add(amtLabel);
        
        String amtStr = ((String) model.getValueAt(row, 3)).replace("$", "");
        JTextField amtField = new JTextField(amtStr);
        amtField.setBounds(150, y, 220, 28);
        dialog.add(amtField);
        y += 45;
        
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setBounds(40, y, 100, 25);
        dateLabel.setForeground(Color.WHITE);
        dialog.add(dateLabel);
        
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        dateSpinner.setValue((java.sql.Date) model.getValueAt(row, 4));
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setBounds(150, y, 220, 28);
        dialog.add(dateSpinner);
        y += 55;
        
        JButton saveBtn = new JButton("Update");
        saveBtn.setBounds(160, y, 100, 35);
        saveBtn.setBackground(new Color(33, 150, 243));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> {
            try {
                String desc = descField.getText();
                String type = (String) typeBox.getSelectedItem();
                String cat = catField.getText().trim();
                double amt = Double.parseDouble(amtField.getText());
                java.util.Date date = (java.util.Date) dateSpinner.getValue();
                java.sql.Date sqlDate = new java.sql.Date(date.getTime());
                
                if (Database.updateTransaction(id, type, cat, amt, desc, sqlDate)) {
                    JOptionPane.showMessageDialog(dialog, "Updated!");
                    loadTransactions();
                    dashboard.loadData();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        dialog.add(saveBtn);
        
        dialog.setVisible(true);
    }
    
    private void deleteTransaction() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Select a transaction!");
            return;
        }
        
        int id = getSelectedId();
        if (id == -1) return;
        
        int confirm = JOptionPane.showConfirmDialog(null, "Delete this transaction?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (Database.deleteTransaction(id)) {
                JOptionPane.showMessageDialog(null, "Deleted!");
                loadTransactions();
                dashboard.loadData();
            } else {
                JOptionPane.showMessageDialog(null, "Failed!");
            }
        }
    }
    
    private void exportCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export CSV");
        
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(new File(fileChooser.getSelectedFile() + ".csv"))) {
                writer.println("Description,Type,Category,Amount,Date");
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        writer.print(model.getValueAt(i, j));
                        if (j < model.getColumnCount() - 1) writer.print(",");
                    }
                    writer.println();
                }
                JOptionPane.showMessageDialog(null, "Exported!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Export error: " + ex.getMessage());
            }
        }
    }
    
    private void importCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import CSV");
        
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                reader.readLine();
                String line;
                int count = 0;
                
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 5) {
                        String desc = parts[0];
                        String type = parts[1];
                        String category = parts[2];
                        double amount = Double.parseDouble(parts[3].replace("$", ""));
                        java.sql.Date date = java.sql.Date.valueOf(parts[4]);
                        
                        if (Database.addTransaction(userId, type, category, amount, desc, date)) {
                            count++;
                        }
                    }
                }
                
                JOptionPane.showMessageDialog(null, "Imported " + count + " transactions!");
                loadTransactions();
                dashboard.loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Import error: " + ex.getMessage());
            }
        }
    }
}