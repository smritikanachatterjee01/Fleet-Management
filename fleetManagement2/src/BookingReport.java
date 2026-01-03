import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BookingReport extends JDialog {
    public BookingReport(JFrame parent) {
        super(parent, "Booking Statistics Report", true);
        setSize(600, 400);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(230, 240, 255));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT COUNT(*) AS total_bookings, " +
                    "SUM(CASE WHEN status = 'Confirmed' THEN 1 ELSE 0 END) AS confirmed, " +
                    "SUM(CASE WHEN status = 'Pending' THEN 1 ELSE 0 END) AS pending, " +
                    "SUM(CASE WHEN status = 'Cancelled' THEN 1 ELSE 0 END) AS cancelled, " +
                    "SUM(CASE WHEN status = 'Completed' THEN 1 ELSE 0 END) AS completed " +
                    "FROM booking";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);  // Fixed: query string passed to executeQuery()

            if (rs.next()) {
                String report = "<html><div style='font-family:Segoe UI;font-size:14px;color:#333;" +
                        "background-color:#e6f2ff;padding:20px;border-radius:10px;'>" +
                        "<h2 style='color:#4682B4;text-align:center;'>Booking Statistics Report</h2>" +
                        "<div style='background-color:#f0f8ff;padding:15px;border-radius:8px;margin:10px 0;'>" +
                        "<p style='font-weight:bold;color:#2e5a88;'><span style='display:inline-block;width:150px;'>Total Bookings:</span> " +
                        "<span style='color:#0066cc;'>" + rs.getInt("total_bookings") + "</span></p>" +
                        "<p style='font-weight:bold;color:#2e5a88;'><span style='display:inline-block;width:150px;'>Confirmed:</span> " +
                        "<span style='color:#009933;'>" + rs.getInt("confirmed") + "</span></p>" +
                        "<p style='font-weight:bold;color:#2e5a88;'><span style='display:inline-block;width:150px;'>Pending:</span> " +
                        "<span style='color:#ff9900;'>" + rs.getInt("pending") + "</span></p>" +
                        "<p style='font-weight:bold;color:#2e5a88;'><span style='display:inline-block;width:150px;'>Cancelled:</span> " +
                        "<span style='color:#cc3300;'>" + rs.getInt("cancelled") + "</span></p>" +
                        "<p style='font-weight:bold;color:#2e5a88;'><span style='display:inline-block;width:150px;'>Completed:</span> " +
                        "<span style='color:#663399;'>" + rs.getInt("completed") + "</span></p>" +
                        "</div></div></html>";

                JLabel label = new JLabel(report);
                mainPanel.add(label, BorderLayout.CENTER);
            }
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        add(mainPanel);
    }
}