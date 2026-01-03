import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class VehicleView extends JFrame {
    public VehicleView(JFrame parent, String username) {
        super("Available Vehicles");
        setSize(850, 450);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(173, 216, 230));

        try (Connection conn = DBConnection.getConnection()) {
            // Fixed: Proper SQL query string passed to executeQuery()
            String query = "SELECT vehicle_id, registration_number, make, model, year, capacity, status " +
                    "FROM vehicle WHERE status = 'Available'";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);  // Now correctly passing the query string

            // Create table model
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("Reg No");
            model.addColumn("Make");
            model.addColumn("Model");
            model.addColumn("Year");
            model.addColumn("Capacity");
            model.addColumn("Status");

            // Populate table model
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("vehicle_id"),
                        rs.getString("registration_number"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getInt("capacity"),
                        rs.getString("status")
                });
            }

            // Create table with model
            JTable table = new JTable(model);
            table.setFont(new Font("Arial", Font.PLAIN, 12));
            table.setRowHeight(25);
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            table.setFillsViewportHeight(true);

            // Add table to scroll pane
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(800, 400));
            add(scrollPane);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}