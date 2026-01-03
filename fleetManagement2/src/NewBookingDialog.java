import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;

public class NewBookingDialog extends JDialog {
    private JComboBox<String> vehicleCombo;
    private JTextField startDateField, endDateField;
    private String username;

    public NewBookingDialog(JFrame parent, String username) {
        super(parent, "New Booking", true);
        this.username = username;
        setSize(450, 350);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(173, 216, 230));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(173, 216, 230));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Vehicle selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Vehicle:"), gbc);
        gbc.gridx = 1;
        vehicleCombo = new JComboBox<>();
        vehicleCombo.setBackground(Color.WHITE);
        loadAvailableVehicles();
        panel.add(vehicleCombo, gbc);

        // Start date
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Start Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        startDateField = new JTextField(LocalDate.now().toString(), 15);
        panel.add(startDateField, gbc);

        // End date
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("End Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        endDateField = new JTextField(LocalDate.now().plusDays(1).toString(), 15);
        panel.add(endDateField, gbc);

        // Book button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton bookButton = new JButton("Book Vehicle");
        bookButton.setBackground(new Color(70, 130, 180));
        bookButton.setForeground(Color.WHITE);
        bookButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bookButton.addActionListener(e -> createBooking());
        panel.add(bookButton, gbc);

        add(panel);
    }

    private void loadAvailableVehicles() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT vehicle_id, registration_number, make, model " +
                    "FROM vehicle WHERE status = 'Available'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);  // Fixed: Added query parameter

            while (rs.next()) {
                String display = rs.getString("registration_number") + " - " +
                        rs.getString("make") + " " + rs.getString("model");
                vehicleCombo.addItem(rs.getInt("vehicle_id") + ":" + display);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading vehicles: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createBooking() {
        String selectedVehicle = (String) vehicleCombo.getSelectedItem();
        if (selectedVehicle == null || selectedVehicle.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No vehicle selected",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int vehicleId = Integer.parseInt(selectedVehicle.split(":")[0]);
        String startDate = startDateField.getText();
        String endDate = endDateField.getText();

        try {
            int customerId = getCustomerId(username);
            if (customerId == -1) {
                JOptionPane.showMessageDialog(this, "Customer not found",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String query = "INSERT INTO booking (customer_id, vehicle_id, " +
                        "booking_date, start_date, end_date, status) " +
                        "VALUES (?, ?, CURDATE(), ?, ?, 'Pending')";

                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, customerId);
                    stmt.setInt(2, vehicleId);
                    stmt.setString(3, startDate);
                    stmt.setString(4, endDate);

                    int rowsAffected = stmt.executeUpdate();  // Fixed: executeUpdate() for INSERT
                    if (rowsAffected > 0) {
                        // Update vehicle status
                        String updateQuery = "UPDATE vehicle SET status = 'On Route' WHERE vehicle_id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                            updateStmt.setInt(1, vehicleId);
                            updateStmt.executeUpdate();
                        }

                        JOptionPane.showMessageDialog(this, "Booking created successfully",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getCustomerId(String username) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT customer_id FROM customer WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {  // Fixed: Added query execution
                    if (rs.next()) {
                        return rs.getInt("customer_id");
                    }
                }
            }
            return -1;
        }
    }
}