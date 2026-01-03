import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewVehicles extends JDialog {
    public ViewVehicles(JFrame parent) {
        super(parent, "Vehicle List", true);
        setSize(800, 500);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(230, 240, 255));

        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM vehicle";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);  // Fixed: query string passed to executeQuery()

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("Reg No");
            model.addColumn("Make");
            model.addColumn("Model");
            model.addColumn("Year");
            model.addColumn("Capacity");
            model.addColumn("Status");

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

            JTable table = new JTable(model);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            table.setRowHeight(25);
            table.setSelectionBackground(new Color(173, 216, 230));
            table.setSelectionForeground(Color.BLACK);

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            add(scrollPane);

            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}