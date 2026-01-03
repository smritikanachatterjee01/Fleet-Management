import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewBookings extends JDialog {
    public ViewBookings(JFrame parent) {
        super(parent, "Booking List", true);
        setSize(800, 500);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(230, 240, 255));

        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT b.booking_id, c.name AS customer_name, " +
                    "v.registration_number, b.start_date, b.end_date, b.status " +
                    "FROM booking b " +
                    "JOIN customer c ON b.customer_id = c.customer_id " +
                    "JOIN vehicle v ON b.vehicle_id = v.vehicle_id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);  // Fixed: query string passed to executeQuery()

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("Customer");
            model.addColumn("Vehicle");
            model.addColumn("Start Date");
            model.addColumn("End Date");
            model.addColumn("Status");

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("booking_id"),
                        rs.getString("customer_name"),
                        rs.getString("registration_number"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getString("status")
                });
            }

            JTable table = new JTable(model);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(table);
            add(scrollPane);

            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}