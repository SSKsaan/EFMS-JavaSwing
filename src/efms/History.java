/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package efms;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 *
 * @author ssksan
 */
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
        
        mainPanel.add(createTopPanel(), BorderLayout.NORTH);
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        mainPanel.add(createBottomPanel(), BorderLayout.SOUTH);
        
        loadTransactions();
        return mainPanel;
    }
    
    private JPanel createTopPanel() {
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
        yearField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (yearField.getText().equals("Year")) {
                    yearField.setText("");
                    yearField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (yearField.getText().isEmpty()) {
                    yearField.setText("Year");
                    yearField.setForeground(Color.GRAY);
                }
            }
        });
        leftPanel.add(yearField);
        
        JButton filterBtn = createButton("Filter", 80, new Color(0, 150, 136));
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
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) loadTransactions();
            }
        });
        rightPanel.add(searchField);
        
        JButton searchBtn = createButton("Search", 90, new Color(0, 150, 136));
        searchBtn.addActionListener(e -> loadTransactions());
        rightPanel.add(searchBtn);
        
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);
        return topPanel;
    }
    
    private JScrollPane createTablePanel() {
        model = new DefaultTableModel(
            new String[]{"Description", "Type", "Category", "Amount", "Date"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
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
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
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
            public void mouseClicked(MouseEvent e) {
                int col = header.columnAtPoint(e.getPoint());
                String name = model.getColumnName(col).toLowerCase();
                currentSort = currentSort.equals(name + " ASC") ? name + " DESC" : name + " ASC";
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
        return scrollPane;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(40, 40, 40));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftButtons.setBackground(new Color(40, 40, 40));
        
        JButton updateBtn = createButton("Update", 90, new Color(33, 150, 243));
        updateBtn.addActionListener(e -> updateTransaction());
        leftButtons.add(updateBtn);
        
        JButton deleteBtn = createButton("Delete", 90, new Color(244, 67, 54));
        deleteBtn.addActionListener(e -> deleteTransaction());
        leftButtons.add(deleteBtn);
        
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightButtons.setBackground(new Color(40, 40, 40));
        
        JButton importBtn = createButton("Import", 90, new Color(100, 100, 100));
        importBtn.addActionListener(e -> importCSV());
        rightButtons.add(importBtn);
        
        JButton exportBtn = createButton("Export", 90, new Color(100, 100, 100));
        exportBtn.addActionListener(e -> exportCSV());
        rightButtons.add(exportBtn);
        
        bottomPanel.add(leftButtons, BorderLayout.WEST);
        bottomPanel.add(rightButtons, BorderLayout.EAST);
        return bottomPanel;
    }
    
    private JButton createButton(String text, int width, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(width, text.equals("Filter") || text.equals("Search") ? 28 : 32));
        return btn;
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
            sql.append(" AND MONTH(date)=?");
            params.add(monthBox.getSelectedIndex());
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
                    rs.getString("description"), type, rs.getString("category"),
                    String.format("$%.2f", amount), rs.getDate("date")
                });
                
                rowToIdMap.put(rowIndex++, id);
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
        return row == -1 ? -1 : rowToIdMap.getOrDefault(row, -1);
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
        y = addField(dialog, "Description:", new JTextField((String) model.getValueAt(row, 0)), y);
        JTextField descField = (JTextField) dialog.getContentPane().getComponent(1);
        
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setBounds(40, y, 100, 25);
        typeLabel.setForeground(Color.WHITE);
        dialog.add(typeLabel);
        
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Expense", "Income"});
        typeBox.setSelectedItem(model.getValueAt(row, 1));
        typeBox.setBounds(150, y, 220, 28);
        dialog.add(typeBox);
        y += 45;
        
        y = addField(dialog, "Category:", new JTextField((String) model.getValueAt(row, 2)), y);
        JTextField catField = (JTextField) dialog.getContentPane().getComponent(5);
        
        String amtStr = ((String) model.getValueAt(row, 3)).replace("$", "");
        y = addField(dialog, "Amount:", new JTextField(amtStr), y);
        JTextField amtField = (JTextField) dialog.getContentPane().getComponent(7);
        
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setBounds(40, y, 100, 25);
        dateLabel.setForeground(Color.WHITE);
        dialog.add(dateLabel);
        
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setValue((java.sql.Date) model.getValueAt(row, 4));
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
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
                java.sql.Date sqlDate = new java.sql.Date(((java.util.Date) dateSpinner.getValue()).getTime());
                
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
    
    private int addField(JDialog dialog, String label, JTextField field, int y) {
        JLabel lbl = new JLabel(label);
        lbl.setBounds(40, y, 100, 25);
        lbl.setForeground(Color.WHITE);
        dialog.add(lbl);
        field.setBounds(150, y, 220, 28);
        dialog.add(field);
        return y + 45;
    }
    
    private void deleteTransaction() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Select a transaction!");
            return;
        }
        
        int id = getSelectedId();
        if (id == -1) return;
        
        int confirm = JOptionPane.showConfirmDialog(null, "Delete this transaction?", 
            "Confirm", JOptionPane.YES_NO_OPTION);
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
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Export CSV");
        
        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter w = new PrintWriter(new File(fc.getSelectedFile() + ".csv"))) {
                w.println("Description,Type,Category,Amount,Date");
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        w.print(model.getValueAt(i, j));
                        if (j < model.getColumnCount() - 1) w.print(",");
                    }
                    w.println();
                }
                JOptionPane.showMessageDialog(null, "Exported!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Export error: " + ex.getMessage());
            }
        }
    }
    
    private void importCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Import CSV");
        
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader r = new BufferedReader(new FileReader(fc.getSelectedFile()))) {
                r.readLine();
                String line;
                int count = 0;
                
                while ((line = r.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 5) {
                        double amount = Double.parseDouble(parts[3].replace("$", ""));
                        java.sql.Date date = java.sql.Date.valueOf(parts[4]);
                        
                        if (Database.addTransaction(userId, parts[1], parts[2], amount, parts[0], date)) {
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