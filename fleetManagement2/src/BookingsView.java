import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class BookingsView extends JFrame {
    private String username;
    private JTable bookingsTable;

    public BookingsView(String username) {
        this.username = username;
        setTitle("My Bookings");
        setSize(900, 500);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(173, 216, 230));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table model
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        model.addColumn("Booking ID");
        model.addColumn("Vehicle");
        model.addColumn("Start Date");
        model.addColumn("End Date");
        model.addColumn("Status");

        bookingsTable = new JTable(model);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        bookingsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        bookingsTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add cancel button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel Booking");
        cancelButton.setBackground(new Color(220, 80, 80));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(e -> cancelBooking());
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadBookings();
    }

    private void loadBookings() {
        DefaultTableModel model = (DefaultTableModel) bookingsTable.getModel();
        model.setRowCount(0); // Clear existing data

        try {
            int customerId = getCustomerId(username);
            if (customerId == -1) {
                JOptionPane.showMessageDialog(this, "Customer not found",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String query = "SELECT b.booking_id, v.registration_number, " +
                        "b.start_date, b.end_date, b.status " +
                        "FROM booking b " +
                        "JOIN vehicle v ON b.vehicle_id = v.vehicle_id " +
                        "WHERE b.customer_id = ?";

                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, customerId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("booking_id"),
                            rs.getString("registration_number"),
                            rs.getDate("start_date"),
                            rs.getDate("end_date"),
                            rs.getString("status")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading bookings: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingId = (int) bookingsTable.getValueAt(selectedRow, 0);
        String status = (String) bookingsTable.getValueAt(selectedRow, 4);

        if ("Cancelled".equals(status)) {
            JOptionPane.showMessageDialog(this, "This booking is already cancelled",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel this booking?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                // Start transaction
                conn.setAutoCommit(false);

                try {
                    // 1. Update booking status to Cancelled
                    String updateBookingQuery = "UPDATE booking SET status = 'Cancelled' WHERE booking_id = ?";
                    PreparedStatement updateBookingStmt = conn.prepareStatement(updateBookingQuery);
                    updateBookingStmt.setInt(1, bookingId);
                    updateBookingStmt.executeUpdate();

                    // 2. Get vehicle ID for this booking
                    String getVehicleQuery = "SELECT vehicle_id FROM booking WHERE booking_id = ?";
                    PreparedStatement getVehicleStmt = conn.prepareStatement(getVehicleQuery);
                    getVehicleStmt.setInt(1, bookingId);
                    ResultSet rs = getVehicleStmt.executeQuery();

                    if (rs.next()) {
                        int vehicleId = rs.getInt("vehicle_id");

                        // 3. Update vehicle status back to Available
                        String updateVehicleQuery = "UPDATE vehicle SET status = 'Available' WHERE vehicle_id = ?";
                        PreparedStatement updateVehicleStmt = conn.prepareStatement(updateVehicleQuery);
                        updateVehicleStmt.setInt(1, vehicleId);
                        updateVehicleStmt.executeUpdate();
                    }

                    conn.commit();
                    JOptionPane.showMessageDialog(this, "Booking cancelled successfully",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadBookings(); // Refresh the table
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error cancelling booking: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int getCustomerId(String username) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT customer_id FROM customer WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("customer_id");
            }
            return -1;
        }
    }
}