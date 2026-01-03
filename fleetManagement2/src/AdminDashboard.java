import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminDashboard extends JFrame {
    public AdminDashboard() {
        setTitle("Fleet Management System - Admin Dashboard");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(230, 240, 255));

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(173, 216, 230));
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Create menus
        JMenu createMenu = createStyledMenu("Add New");
        JMenu viewMenu = createStyledMenu("View");
        JMenu reportMenu = createStyledMenu("Reports");

        // Create menu items
        JMenuItem addVehicleItem = createStyledMenuItem("Add Vehicle");
        JMenuItem viewVehiclesItem = createStyledMenuItem("View Vehicles");
        JMenuItem viewCustomersItem = createStyledMenuItem("View Customers");
        JMenuItem viewBookingsItem = createStyledMenuItem("View Bookings");
        JMenuItem bookingReportItem = createStyledMenuItem("Booking Report");

        // Add action listeners to menu items
        addVehicleItem.addActionListener(e -> showAddVehicleDialog());
        viewVehiclesItem.addActionListener(e -> showViewVehiclesDialog());
        viewCustomersItem.addActionListener(e -> showViewCustomersDialog());
        viewBookingsItem.addActionListener(e -> showViewBookingsDialog());
        bookingReportItem.addActionListener(e -> showBookingReportDialog());

        // Add items to menus
        createMenu.add(addVehicleItem);
        viewMenu.add(viewVehiclesItem);
        viewMenu.add(viewCustomersItem);
        viewMenu.add(viewBookingsItem);
        reportMenu.add(bookingReportItem);

        // Add menus to menu bar
        menuBar.add(createMenu);
        menuBar.add(viewMenu);
        menuBar.add(reportMenu);

        // User menu with logout
        JMenu userMenu = createStyledMenu("Admin");
        JMenuItem logoutItem = createStyledMenuItem("Logout");
        logoutItem.addActionListener(e -> logout());
        userMenu.add(logoutItem);

        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(userMenu);

        setJMenuBar(menuBar);

        // Main panel with welcome message
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(230, 240, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Welcome panel with gradient
        JPanel welcomePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color color1 = new Color(100, 149, 237);
                Color color2 = new Color(173, 216, 230);
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), 0, color2);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        welcomePanel.setPreferredSize(new Dimension(0, 100));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        welcomePanel.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome, Admin!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(Color.WHITE);
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

        mainPanel.add(welcomePanel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        buttonPanel.setBackground(new Color(230, 240, 255));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Create buttons
        JButton vehiclesButton = createStyledButton("Manage Vehicles", new Color(70, 130, 180));
        JButton customersButton = createStyledButton("Manage Customers", new Color(70, 130, 180));
        JButton bookingsButton = createStyledButton("Manage Bookings", new Color(70, 130, 180));
        JButton reportsButton = createStyledButton("Generate Reports", new Color(70, 130, 180));

        // Add action listeners to buttons
        vehiclesButton.addActionListener(e -> showViewVehiclesDialog());
        customersButton.addActionListener(e -> showViewCustomersDialog());
        bookingsButton.addActionListener(e -> showViewBookingsDialog());
        reportsButton.addActionListener(e -> showBookingReportDialog());

        buttonPanel.add(vehiclesButton);
        buttonPanel.add(customersButton);
        buttonPanel.add(bookingsButton);
        buttonPanel.add(reportsButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void showAddVehicleDialog() {
        AddVehicle dialog = new AddVehicle(this);
        dialog.setVisible(true);
    }

    private void showViewVehiclesDialog() {
        ViewVehicles dialog = new ViewVehicles(this);
        dialog.setVisible(true);
    }

    private void showViewCustomersDialog() {
        ViewCustomers dialog = new ViewCustomers(this);
        dialog.setVisible(true);
    }

    private void showViewBookingsDialog() {
        ViewBookings dialog = new ViewBookings(this);
        dialog.setVisible(true);
    }

    private void showBookingReportDialog() {
        BookingReport dialog = new BookingReport(this);
        dialog.setVisible(true);
    }

    private JMenu createStyledMenu(String title) {
        JMenu menu = new JMenu(title);
        menu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        menu.setForeground(new Color(0, 0, 139));
        menu.setBackground(new Color(173, 216, 230));
        return menu;
    }

    private JMenuItem createStyledMenuItem(String title) {
        JMenuItem item = new JMenuItem(title);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        item.setBackground(new Color(230, 240, 255));
        item.setForeground(Color.BLACK);
        item.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return item;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        button.setPreferredSize(new Dimension(200, 80));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(new Color(230, 230, 250));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(Color.WHITE);
            }
        });
        return button;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminDashboard dashboard = new AdminDashboard();
            dashboard.setVisible(true);
        });
    }
}