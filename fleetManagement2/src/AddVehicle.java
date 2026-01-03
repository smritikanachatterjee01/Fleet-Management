import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddVehicle extends JDialog {
    private JTextField regNoField, makeField, modelField, yearField, capacityField;
    private JComboBox<String> statusCombo;

    public AddVehicle(JFrame parent) {
        super(parent, "Add New Vehicle", true);
        setSize(500, 500);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(230, 240, 255));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(230, 240, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(173, 216, 230));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Add New Vehicle", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 15));
        formPanel.setBackground(new Color(230, 240, 255));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        addStyledLabelFieldPair(formPanel, "Registration Number:", regNoField = new JTextField());
        addStyledLabelFieldPair(formPanel, "Make:", makeField = new JTextField());
        addStyledLabelFieldPair(formPanel, "Model:", modelField = new JTextField());
        addStyledLabelFieldPair(formPanel, "Year:", yearField = new JTextField());
        addStyledLabelFieldPair(formPanel, "Capacity:", capacityField = new JTextField());

        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(statusLabel);

        statusCombo = new JComboBox<>(new String[]{"Available", "In Maintenance", "On Route"});
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(statusCombo);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(230, 240, 255));

        JButton addButton = createStyledButton("Add Vehicle", new Color(70, 130, 180));
        addButton.addActionListener(e -> addVehicle());

        JButton cancelButton = createStyledButton("Cancel", new Color(220, 220, 220));
        cancelButton.setForeground(Color.DARK_GRAY);
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addStyledLabelFieldPair(JPanel panel, String labelText, JTextField textField) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(label);

        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(textField);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setFocusPainted(false);
        return button;
    }

    private void addVehicle() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "INSERT INTO vehicle (registration_number, make, model, year, capacity, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, regNoField.getText());
            stmt.setString(2, makeField.getText());
            stmt.setString(3, modelField.getText());
            stmt.setInt(4, Integer.parseInt(yearField.getText()));
            stmt.setInt(5, Integer.parseInt(capacityField.getText()));
            stmt.setString(6, (String) statusCombo.getSelectedItem());

            int rowsAffected = stmt.executeUpdate();  // Fixed: executeUpdate() for INSERT
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Vehicle added successfully!");
                dispose();
            }

            conn.close();
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}