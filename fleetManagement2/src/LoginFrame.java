import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> userTypeCombo;

    public LoginFrame() {
        setTitle("Fleet Management System - Login");
        setSize(750, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(173, 216, 230));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Fleet Management Login", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 0, 139));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel userTypeLabel = new JLabel("Login as:");
        userTypeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(userTypeLabel, gbc);

        gbc.gridx = 1;
        userTypeCombo = new JComboBox<>(new String[]{"Admin", "Customer"});
        userTypeCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        userTypeCombo.setBackground(Color.WHITE);
        panel.add(userTypeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(200, 35));
        loginButton.setFocusPainted(false);
        panel.add(loginButton, gbc);

        gbc.gridy = 5;
        JButton registerButton = new JButton("Register (Customers Only)");
        registerButton.setFont(new Font("Arial", Font.PLAIN, 12));
        registerButton.setBackground(new Color(100, 149, 237));
        registerButton.setForeground(Color.WHITE);
        registerButton.setPreferredSize(new Dimension(200, 30));
        registerButton.setFocusPainted(false);
        panel.add(registerButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String userType = (String) userTypeCombo.getSelectedItem();

                try {
                    if (authenticateUser(username, password, userType)) {
                        JPanel successPanel = new JPanel(new BorderLayout());
                        successPanel.setBackground(new Color(220, 255, 220));

                        JLabel successLabel = new JLabel("Login Successful!", JLabel.CENTER);
                        successLabel.setFont(new Font("Arial", Font.BOLD, 16));
                        successLabel.setForeground(new Color(0, 100, 0));

                        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", JLabel.CENTER);
                        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));

                        JLabel roleLabel = new JLabel("You are logged in as: " + userType, JLabel.CENTER);
                        roleLabel.setFont(new Font("Arial", Font.ITALIC, 12));

                        JPanel messagePanel = new JPanel(new GridLayout(3, 1, 5, 5));
                        messagePanel.setBackground(new Color(220, 255, 220));
                        messagePanel.add(successLabel);
                        messagePanel.add(welcomeLabel);
                        messagePanel.add(roleLabel);

                        successPanel.add(messagePanel, BorderLayout.CENTER);

                        JOptionPane.showMessageDialog(LoginFrame.this, successPanel, "Success", JOptionPane.PLAIN_MESSAGE, new ImageIcon());

                        dispose();

                        if (userType.equals("Admin")) {
                            new AdminDashboard().setVisible(true);
                        } else {
                            new CustomerDashboard(username).setVisible(true);
                        }
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CustomerRegistrationFrame().setVisible(true);
            }
        });

        add(panel);
    }

    private boolean authenticateUser(String username, String password, String userType) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String table = userType.equals("Admin") ? "admin" : "customer";
        String query = "SELECT * FROM " + table + " WHERE username = ? AND password = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } finally {
            conn.close();
        }
    }
}