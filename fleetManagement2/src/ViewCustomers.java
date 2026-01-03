import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewCustomers extends JDialog {
    public ViewCustomers(JFrame parent) {
        super(parent, "Customer List", true);
        setSize(800, 500);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(230, 240, 255));

        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM customer";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);  // Fixed: query string passed to executeQuery()

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("Username");
            model.addColumn("Name");
            model.addColumn("Email");
            model.addColumn("Phone");

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("customer_id"),
                        rs.getString("username"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone")
                });
            }

            JTable table = new JTable(model);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            table.setRowHeight(25);
            JScrollPane scrollPane = new JScrollPane(table);
            add(scrollPane);

            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}