package efms;

import javax.swing.*;
import java.awt.*;

public class Auth extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    
    public Auth() {
        setTitle("EFMS - Login");
        setSize(450, 320);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(30, 30, 30));
        setLayout(null);
        
        JLabel titleLabel = new JLabel("Finance Manager");
        titleLabel.setBounds(125, 30, 200, 30);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel);
        
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(70, 100, 100, 25);
        userLabel.setForeground(Color.WHITE);
        add(userLabel);
        
        userField = new JTextField();
        userField.setBounds(170, 100, 200, 30);
        add(userField);
        
        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(70, 150, 100, 25);
        passLabel.setForeground(Color.WHITE);
        add(passLabel);
        
        passField = new JPasswordField();
        passField.setBounds(170, 150, 200, 30);
        add(passField);
        
        JButton loginBtn = new JButton("Sign In");
        loginBtn.setBounds(170, 210, 95, 35);
        loginBtn.setBackground(new Color(0, 150, 136));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 12));
        loginBtn.addActionListener(e -> login());
        add(loginBtn);
        
        JButton registerBtn = new JButton("Sign Up");
        registerBtn.setBounds(275, 210, 95, 35);
        registerBtn.setBackground(new Color(100, 100, 100));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setFont(new Font("Arial", Font.BOLD, 12));
        registerBtn.addActionListener(e -> register());
        add(registerBtn);
    }
    
    private void login() {
        String user = userField.getText();
        String pass = new String(passField.getPassword());
        
        int userId = Database.login(user, pass);
        if (userId != -1) {
            dispose();
            new Dashboard(userId, user).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials!");
        }
    }
    
    private void register() {
        String user = userField.getText();
        String pass = new String(passField.getPassword());
        
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields!");
            return;
        }
        
        if (Database.register(user, pass)) {
            JOptionPane.showMessageDialog(this, "Registration successful!");
        } else {
            JOptionPane.showMessageDialog(this, "Username already exists!");
        }
    }
}