import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class CancelBookingDialog extends JDialog {
    private JTable bookingsTable;
    private String username;

    public CancelBookingDialog(JFrame parent, String username) {
        super(parent, "Cancel Booking", true);
        this.username = username;
        setSize(800, 500);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(230, 240, 255));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table model
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
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

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel Booking");
        cancelBtn.setBackground(new Color(220, 80, 80));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.addActionListener(this::cancelBooking);
        buttonPanel.add(cancelBtn);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        buttonPanel.add(closeBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        loadBookings();
    }

    private void loadBookings() {
        DefaultTableModel model = (DefaultTableModel) bookingsTable.getModel();
        model.setRowCount(0);

        try {
            int customerId = getCustomerId(username);
            if (customerId == -1) {
                JOptionPane.showMessageDialog(this, "Customer not found", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String query = "SELECT b.booking_id, v.registration_number, " +
                        "b.start_date, b.end_date, b.status " +
                        "FROM booking b JOIN vehicle v ON b.vehicle_id = v.vehicle_id " +
                        "WHERE b.customer_id = ? AND b.status IN ('Pending', 'Confirmed')";

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

    private void cancelBooking(ActionEvent e) {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingId = (int) bookingsTable.getValueAt(selectedRow, 0);
        String status = (String) bookingsTable.getValueAt(selectedRow, 4);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel booking #" + bookingId + "?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                conn.setAutoCommit(false);

                try {
                    // Update booking status
                    String updateBooking = "UPDATE booking SET status = 'Cancelled' WHERE booking_id = ?";
                    PreparedStatement stmt1 = conn.prepareStatement(updateBooking);
                    stmt1.setInt(1, bookingId);
                    stmt1.executeUpdate();

                    // Get vehicle ID
                    String getVehicle = "SELECT vehicle_id FROM booking WHERE booking_id = ?";
                    PreparedStatement stmt2 = conn.prepareStatement(getVehicle);
                    stmt2.setInt(1, bookingId);
                    ResultSet rs = stmt2.executeQuery();

                    if (rs.next()) {
                        int vehicleId = rs.getInt("vehicle_id");

                        // Update vehicle status
                        String updateVehicle = "UPDATE vehicle SET status = 'Available' WHERE vehicle_id = ?";
                        PreparedStatement stmt3 = conn.prepareStatement(updateVehicle);
                        stmt3.setInt(1, vehicleId);
                        stmt3.executeUpdate();
                    }

                    conn.commit();
                    JOptionPane.showMessageDialog(this, "Booking cancelled successfully!");
                    loadBookings(); // Refresh the list
                } catch (SQLException ex) {
                    conn.rollback();
                    throw ex;
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error cancelling booking: " + ex.getMessage(),
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