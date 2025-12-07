package efms;

import java.sql.*;
import java.util.*;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/efmsd";
    private static final String USER = "root";
    private static final String PASS = "2003";
    
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
    
    public static int login(String username, String password) {
        try (Connection conn = connect()) {
            String sql = "SELECT id FROM users WHERE username=? AND password=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return -1;
    }
    
    public static boolean register(String username, String password) {
        try (Connection conn = connect()) {
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    
    public static boolean addTransaction(int userId, String type, String category, 
                                        double amount, String desc, java.sql.Date date) {
        if (category == null || category.trim().isEmpty()) {
            category = "Others";
        }
        try (Connection conn = connect()) {
            String sql = "INSERT INTO transactions (user_id, type, category, amount, description, date) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, type);
            stmt.setString(3, category);
            stmt.setDouble(4, amount);
            stmt.setString(5, desc);
            stmt.setDate(6, date);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Add error: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean deleteTransaction(int id) {
        try (Connection conn = connect()) {
            String sql = "DELETE FROM transactions WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    
    public static boolean updateTransaction(int id, String type, String category, 
                                           double amount, String desc, java.sql.Date date) {
        if (category == null || category.trim().isEmpty()) {
            category = "Others";
        }
        try (Connection conn = connect()) {
            String sql = "UPDATE transactions SET type=?, category=?, amount=?, description=?, date=? WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, type);
            stmt.setString(2, category);
            stmt.setDouble(3, amount);
            stmt.setString(4, desc);
            stmt.setDate(5, date);
            stmt.setInt(6, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    
    public static List<String> getCategories(int userId) {
        List<String> categories = new ArrayList<>();
        try (Connection conn = connect()) {
            String sql = "SELECT DISTINCT category FROM transactions WHERE user_id=? ORDER BY category";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            System.out.println("Get categories error: " + e.getMessage());
        }
        return categories;
    }
}