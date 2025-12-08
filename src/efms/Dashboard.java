/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package efms;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;

/**
 *
 * @author ssksan
 */
public class Dashboard extends javax.swing.JFrame {
    
    private int userId;
    private String username;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JLabel balanceLabel;
    private JPanel weeklyChartPanel, dailyChartPanel;
    private History historyManager;
    
    public Dashboard(int userId, String username) {
        this.userId = userId;
        this.username = username;
        initComponents();
        loadData();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {
        setTitle("EFMS - Dashboard");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Top Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(35, 35, 35));
        topBar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 10));
        
        JPanel leftSection = new JPanel();
        leftSection.setLayout(new BoxLayout(leftSection, BoxLayout.X_AXIS));
        leftSection.setBackground(new Color(35, 35, 35));
        
        JLabel userLabel = new JLabel("Welcome, " + username);
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.BOLD, 15));
        leftSection.add(userLabel);
        leftSection.add(Box.createRigidArea(new Dimension(15, 0)));
        
        balanceLabel = new JLabel("Balance: 0.00 $");
        balanceLabel.setForeground(new Color(76, 175, 80));
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 15));
        leftSection.add(balanceLabel);
        
        JPanel rightSection = new JPanel();
        rightSection.setLayout(new BoxLayout(rightSection, BoxLayout.X_AXIS));
        rightSection.setBackground(new Color(35, 35, 35));
        
        JButton dashBtn = createButton("Dashboard", new Color(0, 150, 136), 120);
        JButton histBtn = createButton("History", new Color(60, 60, 60), 110);
        JButton logoutBtn = createButton("Logout", new Color(244, 67, 54), 90);
        
        dashBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "dashboard");
            dashBtn.setBackground(new Color(0, 150, 136));
            histBtn.setBackground(new Color(60, 60, 60));
            setTitle("EFMS - Dashboard");
        });
        
        histBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "history");
            histBtn.setBackground(new Color(0, 150, 136));
            dashBtn.setBackground(new Color(60, 60, 60));
            setTitle("EFMS - History");
        });
        
        logoutBtn.addActionListener(e -> {
            dispose();
            new Auth().setVisible(true);
        });
        
        rightSection.add(dashBtn);
        rightSection.add(Box.createRigidArea(new Dimension(10, 0)));
        rightSection.add(histBtn);
        rightSection.add(Box.createRigidArea(new Dimension(10, 0)));
        rightSection.add(logoutBtn);
        
        topBar.add(leftSection, BorderLayout.WEST);
        topBar.add(rightSection, BorderLayout.EAST);
        
        // Add Button
        JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButtonPanel.setBackground(new Color(30, 30, 30));
        addButtonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        
        JButton addBtn = new JButton("+ ADD NEW ENTRY");
        addBtn.setBackground(new Color(76, 175, 80));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setFont(new Font("Arial", Font.BOLD, 16));
        addBtn.setPreferredSize(new Dimension(250, 50));
        addBtn.addActionListener(e -> showAddDialog());
        addButtonPanel.add(addBtn);
        
        // Content Panel
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(30, 30, 30));
        
        JPanel dashPanel = createDashboardPanel();
        historyManager = new History();
        JPanel histPanel = historyManager.createHistoryPanel(userId, this);
        
        contentPanel.add(dashPanel, "dashboard");
        contentPanel.add(histPanel, "history");
        
        add(topBar, BorderLayout.NORTH);
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(new Color(30, 30, 30));
        mainContainer.add(addButtonPanel, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);
        add(mainContainer, BorderLayout.CENTER);
    }// </editor-fold>                        
    
    private JButton createButton(String text, Color bg, int width) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(width, 32));
        return btn;
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 20));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        weeklyChartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawWeeklyChart(g);
            }
        };
        weeklyChartPanel.setBackground(new Color(40, 40, 40));
        weeklyChartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)), 
            "Weekly Expenses", 0, 0, new Font("Arial", Font.BOLD, 14), Color.WHITE));
        
        dailyChartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawDailyPieChart(g);
            }
        };
        dailyChartPanel.setBackground(new Color(40, 40, 40));
        dailyChartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)), 
            "Today's Expenses by Category", 0, 0, new Font("Arial", Font.BOLD, 14), Color.WHITE));
        
        panel.add(weeklyChartPanel);
        panel.add(dailyChartPanel);
        return panel;
    }
    
    private void showAddDialog() {
        JDialog dialog = new JDialog(this, "Add Entry", true);
        dialog.setSize(450, 380);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(new Color(30, 30, 30));
        dialog.setLayout(null);
        
        int y = 25;
        y = addDialogField(dialog, "Description:", new JTextField(), y);
        JTextField descField = (JTextField) dialog.getContentPane().getComponent(1);
        
        y = addDialogField(dialog, "Amount:", new JTextField(), y);
        JTextField amountField = (JTextField) dialog.getContentPane().getComponent(3);
        
        JLabel catLabel = new JLabel("Category:");
        catLabel.setBounds(50, y, 120, 25);
        catLabel.setForeground(Color.WHITE);
        dialog.add(catLabel);
        
        JComboBox<String> categoryBox = new JComboBox<>();
        categoryBox.setEditable(true);
        Database.getCategories(userId).forEach(categoryBox::addItem);
        categoryBox.setBounds(180, y, 220, 28);
        dialog.add(categoryBox);
        y += 45;
        
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setBounds(50, y, 120, 25);
        typeLabel.setForeground(Color.WHITE);
        dialog.add(typeLabel);
        
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Expense", "Income"});
        typeBox.setBounds(180, y, 220, 28);
        dialog.add(typeBox);
        y += 45;
        
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setBounds(50, y, 120, 25);
        dateLabel.setForeground(Color.WHITE);
        dialog.add(dateLabel);
        
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.setBounds(180, y, 220, 28);
        dialog.add(dateSpinner);
        y += 50;
        
        JButton saveBtn = new JButton("Save");
        saveBtn.setBounds(175, y, 100, 38);
        saveBtn.setBackground(new Color(0, 150, 136));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> {
            try {
                String desc = descField.getText();
                double amount = Double.parseDouble(amountField.getText());
                String category = categoryBox.getSelectedItem().toString().trim();
                String type = (String) typeBox.getSelectedItem();
                java.sql.Date sqlDate = new java.sql.Date(((java.util.Date) dateSpinner.getValue()).getTime());
                
                if (Database.addTransaction(userId, type, category, amount, desc, sqlDate)) {
                    JOptionPane.showMessageDialog(dialog, "Added!");
                    loadData();
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
    
    private int addDialogField(JDialog dialog, String label, JTextField field, int y) {
        JLabel lbl = new JLabel(label);
        lbl.setBounds(50, y, 120, 25);
        lbl.setForeground(Color.WHITE);
        dialog.add(lbl);
        field.setBounds(180, y, 220, 28);
        dialog.add(field);
        return y + 45;
    }
    
    public void loadData() {
        try (Connection conn = Database.connect()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT type, SUM(amount) as total FROM transactions WHERE user_id=? GROUP BY type");
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            double income = 0, expense = 0;
            while (rs.next()) {
                if (rs.getString("type").equals("Income")) 
                    income = rs.getDouble("total");
                else 
                    expense = rs.getDouble("total");
            }
            
            balanceLabel.setText(String.format("Balance: %.2f $", income - expense));
            weeklyChartPanel.repaint();
            dailyChartPanel.repaint();
        } catch (SQLException ex) {
            System.out.println("Load error: " + ex.getMessage());
        }
    }
    
    private void drawWeeklyChart(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        try (Connection conn = Database.connect()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT DATE_FORMAT(date, '%a') as day, SUM(amount) as total " +
                "FROM transactions WHERE user_id=? AND type='Expense' " +
                "AND date >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) GROUP BY date ORDER BY date");
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            List<Double> amounts = new ArrayList<>();
            List<String> days = new ArrayList<>();
            while (rs.next()) {
                amounts.add(rs.getDouble("total"));
                days.add(rs.getString("day"));
            }
            
            if (amounts.isEmpty()) {
                g2d.setColor(Color.WHITE);
                g2d.drawString("No expenses in past 7 days", 
                    weeklyChartPanel.getWidth()/2 - 80, weeklyChartPanel.getHeight()/2);
                return;
            }
            
            int w = weeklyChartPanel.getWidth(), h = weeklyChartPanel.getHeight();
            int pad = 60, chartH = h - 2*pad, barW = (w - 2*pad) / 7;
            double max = amounts.stream().max(Double::compare).orElse(100.0);
            
            g2d.setColor(Color.WHITE);
            g2d.drawLine(pad, h-pad, w-pad, h-pad);
            g2d.drawLine(pad, pad, pad, h-pad);
            
            for (int i = 0; i < amounts.size(); i++) {
                int barH = (int)((amounts.get(i) / max) * (chartH - 30));
                int x = pad + (i * barW) + (barW / 3);
                int y = h - pad - barH;
                
                g2d.setColor(new Color(0, 150, 136));
                g2d.fillRect(x, y, barW/3, barH);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                g2d.drawString(days.get(i), x, h-pad+15);
                g2d.drawString("$" + (int)amounts.get(i).doubleValue(), x-5, y-5);
            }
        } catch (SQLException ex) {
            g2d.setColor(Color.WHITE);
            g2d.drawString("Error: " + ex.getMessage(), 20, weeklyChartPanel.getHeight()/2);
        }
    }
    
    private void drawDailyPieChart(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        try (Connection conn = Database.connect()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT category, SUM(amount) as total FROM transactions " +
                "WHERE user_id=? AND type='Expense' AND date=CURDATE() GROUP BY category");
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            Map<String, Double> data = new LinkedHashMap<>();
            double total = 0;
            while (rs.next()) {
                double amt = rs.getDouble("total");
                data.put(rs.getString("category"), amt);
                total += amt;
            }
            
            if (data.isEmpty()) {
                g2d.setColor(Color.WHITE);
                g2d.drawString("No expenses today", dailyChartPanel.getWidth()/2-50, dailyChartPanel.getHeight()/2);
                return;
            }
            
            List<Map.Entry<String, Double>> sorted = new ArrayList<>(data.entrySet());
            sorted.sort((e1, e2) -> {
                if (e1.getKey().equalsIgnoreCase("Others")) return 1;
                if (e2.getKey().equalsIgnoreCase("Others")) return -1;
                return Double.compare(e2.getValue(), e1.getValue());
            });
            
            Color[] colors = {new Color(255,99,132), new Color(54,162,235), 
                new Color(255,206,86), new Color(75,192,192), 
                new Color(153,102,255), new Color(255,159,64)};
            
            int w = dailyChartPanel.getWidth(), h = dailyChartPanel.getHeight();
            int centerX = w/2+50, centerY = h/2, radius = Math.min(w,h)/3;
            int angle = 0, idx = 0, legendY = 40;
            
            for (Map.Entry<String, Double> e : sorted) {
                int arc = (int)((e.getValue() / total) * 360);
                g2d.setColor(colors[idx % colors.length]);
                g2d.fillArc(centerX-radius, centerY-radius, radius*2, radius*2, angle, arc);
                g2d.fillRect(20, legendY, 15, 15);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.PLAIN, 11));
                g2d.drawString(e.getKey() + " $" + (int)e.getValue().doubleValue(), 40, legendY+12);
                angle += arc;
                idx++;
                legendY += 22;
            }
        } catch (SQLException ex) {
            System.out.println("Pie error: " + ex.getMessage());
        }
    }
    
    // Variables declaration - do not modify                     
    // End of variables declaration                   
}