package efms;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class Dashboard extends JFrame {
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
        
        setTitle("EFMS - Dashboard");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        JPanel topBar = new JPanel();
        topBar.setLayout(new BorderLayout());
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
        
        JButton dashBtn = new JButton("Dashboard");
        dashBtn.setBackground(new Color(0, 150, 136));
        dashBtn.setForeground(Color.WHITE);
        dashBtn.setFocusPainted(false);
        dashBtn.setPreferredSize(new Dimension(120, 32));
        
        JButton histBtn = new JButton("History");
        histBtn.setBackground(new Color(60, 60, 60));
        histBtn.setForeground(Color.WHITE);
        histBtn.setFocusPainted(false);
        histBtn.setPreferredSize(new Dimension(110, 32));
        
        dashBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "dashboard");
            dashBtn.setBackground(new Color(0, 150, 136));
            histBtn.setBackground(new Color(60, 60, 60));
            setTitle("EFMS - Dashboard");
        });
        rightSection.add(dashBtn);
        
        rightSection.add(Box.createRigidArea(new Dimension(10, 0)));
        
        histBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "history");
            histBtn.setBackground(new Color(0, 150, 136));
            dashBtn.setBackground(new Color(60, 60, 60));
            setTitle("EFMS - History");
        });
        rightSection.add(histBtn);
        
        rightSection.add(Box.createRigidArea(new Dimension(10, 0)));
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(244, 67, 54));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setPreferredSize(new Dimension(90, 32));
        logoutBtn.addActionListener(e -> {
            dispose();
            new Auth().setVisible(true);
        });
        rightSection.add(logoutBtn);
        
        topBar.add(leftSection, BorderLayout.WEST);
        topBar.add(rightSection, BorderLayout.EAST);
        
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
        
        loadData();
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
        
        JLabel descLabel = new JLabel("Description:");
        descLabel.setBounds(50, y, 120, 25);
        descLabel.setForeground(Color.WHITE);
        dialog.add(descLabel);
        
        JTextField descField = new JTextField();
        descField.setBounds(180, y, 220, 28);
        dialog.add(descField);
        y += 45;
        
        JLabel amtLabel = new JLabel("Amount:");
        amtLabel.setBounds(50, y, 120, 25);
        amtLabel.setForeground(Color.WHITE);
        dialog.add(amtLabel);
        
        JTextField amountField = new JTextField();
        amountField.setBounds(180, y, 220, 28);
        dialog.add(amountField);
        y += 45;
        
        JLabel catLabel = new JLabel("Category:");
        catLabel.setBounds(50, y, 120, 25);
        catLabel.setForeground(Color.WHITE);
        dialog.add(catLabel);
        
        List<String> cats = Database.getCategories(userId);
        JComboBox<String> categoryBox = new JComboBox<>();
        categoryBox.setEditable(true);
        for (String cat : cats) {
            categoryBox.addItem(cat);
        }
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
        
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
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
                Object catObj = categoryBox.getSelectedItem();
                String category = (catObj != null) ? catObj.toString().trim() : "";
                String type = (String) typeBox.getSelectedItem();
                java.util.Date date = (java.util.Date) dateSpinner.getValue();
                java.sql.Date sqlDate = new java.sql.Date(date.getTime());
                
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
    
    public void loadData() {
        try (Connection conn = Database.connect()) {
            String sql = "SELECT type, SUM(amount) as total FROM transactions WHERE user_id=? GROUP BY type";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            double income = 0, expense = 0;
            while (rs.next()) {
                double amt = rs.getDouble("total");
                if (rs.getString("type").equals("Income")) income = amt;
                else expense = amt;
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
            String sql = "SELECT DAYOFWEEK(date) as dow, DATE_FORMAT(date, '%a') as day, SUM(amount) as total " +
                        "FROM transactions WHERE user_id=? AND type='Expense' " +
                        "AND date >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
                        "GROUP BY date ORDER BY date";
            PreparedStatement stmt = conn.prepareStatement(sql);
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
                g2d.setFont(new Font("Arial", Font.PLAIN, 14));
                g2d.drawString("No expenses in past 7 days", weeklyChartPanel.getWidth()/2 - 80, weeklyChartPanel.getHeight()/2);
                return;
            }
            
            int width = weeklyChartPanel.getWidth();
            int height = weeklyChartPanel.getHeight();
            int padding = 60;
            int chartHeight = height - 2 * padding;
            int barWidth = (width - 2 * padding) / 7;
            
            double maxAmount = amounts.stream().max(Double::compare).orElse(100.0);
            
            g2d.setColor(Color.WHITE);
            g2d.drawLine(padding, height - padding, width - padding, height - padding);
            g2d.drawLine(padding, padding, padding, height - padding);
            
            for (int i = 0; i < amounts.size(); i++) {
                int barHeight = (int) ((amounts.get(i) / maxAmount) * (chartHeight - 30));
                int x = padding + (i * barWidth) + (barWidth / 3);
                int y = height - padding - barHeight;
                
                g2d.setColor(new Color(0, 150, 136));
                g2d.fillRect(x, y, barWidth / 3, barHeight);
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                g2d.drawString(days.get(i), x, height - padding + 15);
                g2d.drawString("$" + (int)amounts.get(i).doubleValue(), x - 5, y - 5);
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
            String sql = "SELECT category, SUM(amount) as total FROM transactions " +
                        "WHERE user_id=? AND type='Expense' AND date=CURDATE() GROUP BY category";
            PreparedStatement stmt = conn.prepareStatement(sql);
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
                g2d.drawString("No expenses today", dailyChartPanel.getWidth()/2 - 50, dailyChartPanel.getHeight()/2);
                return;
            }
            
            // Sort data by amount in descending order, but keep "Others" at the end
            List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>(data.entrySet());
            sortedEntries.sort((e1, e2) -> {
                // If either is "Others", it goes to the end
                if (e1.getKey().equalsIgnoreCase("Others")) return 1;
                if (e2.getKey().equalsIgnoreCase("Others")) return -1;
                // Otherwise sort by amount descending
                return Double.compare(e2.getValue(), e1.getValue());
            });
            
            Color[] colors = {
                new Color(255, 99, 132), new Color(54, 162, 235),
                new Color(255, 206, 86), new Color(75, 192, 192),
                new Color(153, 102, 255), new Color(255, 159, 64)
            };
            
            int width = dailyChartPanel.getWidth();
            int height = dailyChartPanel.getHeight();
            int centerX = width / 2 + 50;
            int centerY = height / 2;
            int radius = Math.min(width, height) / 3;
            
            int startAngle = 0;
            int colorIndex = 0;
            int legendX = 20;
            int legendY = 40;
            
            for (Map.Entry<String, Double> entry : sortedEntries) {
                double percentage = (entry.getValue() / total);
                int arcAngle = (int) (percentage * 360);
                
                g2d.setColor(colors[colorIndex % colors.length]);
                g2d.fillArc(centerX - radius, centerY - radius, radius * 2, radius * 2, startAngle, arcAngle);
                
                g2d.fillRect(legendX, legendY, 15, 15);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.PLAIN, 11));
                g2d.drawString(entry.getKey() + " $" + (int)entry.getValue().doubleValue(), legendX + 20, legendY + 12);
                
                startAngle += arcAngle;
                colorIndex++;
                legendY += 22;
            }
        } catch (SQLException ex) {
            System.out.println("Pie error: " + ex.getMessage());
        }
    }
}