import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomerDashboard extends JFrame {
    private String username;

    public CustomerDashboard(String username) {
        this.username = username;
        setTitle("Fleet Management System - Customer Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(173, 216, 230));

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(100, 149, 237));
        menuBar.setBorder(BorderFactory.createRaisedBevelBorder());

        // Create menus
        JMenu vehicleMenu = createStyledMenu("Vehicles");
        JMenu bookingMenu = createStyledMenu("Bookings");
        JMenu accountMenu = createStyledMenu("Account");

        // Create menu items
        JMenuItem viewVehiclesItem = createStyledMenuItem("View Available Vehicles");
        JMenuItem newBookingItem = createStyledMenuItem("New Booking");
        JMenuItem viewBookingsItem = createStyledMenuItem("View My Bookings");
        JMenuItem cancelBookingItem = createStyledMenuItem("Cancel Booking");
        JMenuItem updateProfileItem = createStyledMenuItem("Update Profile");
        JMenuItem logoutItem = createStyledMenuItem("Logout");

        // Add action listeners
        viewVehiclesItem.addActionListener(e -> new VehicleView(this, username).setVisible(true));
        newBookingItem.addActionListener(e -> new NewBookingDialog(this, username).setVisible(true));
        viewBookingsItem.addActionListener(e -> new BookingsView(username).setVisible(true));
        cancelBookingItem.addActionListener(e -> new CancelBookingDialog(this, username).setVisible(true));
        updateProfileItem.addActionListener(e -> new AccountDialog(this, username).setVisible(true));
        logoutItem.addActionListener(e -> logout());

        // Add items to menus
        vehicleMenu.add(viewVehiclesItem);
        bookingMenu.add(newBookingItem);
        bookingMenu.add(viewBookingsItem);
        bookingMenu.add(cancelBookingItem);
        accountMenu.add(updateProfileItem);
        accountMenu.add(logoutItem);

        // Add menus to menu bar
        menuBar.add(vehicleMenu);
        menuBar.add(bookingMenu);
        menuBar.add(accountMenu);

        setJMenuBar(menuBar);

        // Main panel with welcome message
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(173, 216, 230));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Welcome panel with gradient background
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
        welcomePanel.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(Color.WHITE);
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

        mainPanel.add(welcomePanel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        buttonPanel.setBackground(new Color(173, 216, 230));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Create buttons
        JButton viewVehiclesBtn = createStyledButton("View Available Vehicles");
        JButton newBookingBtn = createStyledButton("New Booking");
        JButton viewBookingsBtn = createStyledButton("View My Bookings");
        JButton cancelBookingBtn = createCancelButton("Cancel Booking");

        // Add button actions
        viewVehiclesBtn.addActionListener(e -> new VehicleView(this, username).setVisible(true));
        newBookingBtn.addActionListener(e -> new NewBookingDialog(this, username).setVisible(true));
        viewBookingsBtn.addActionListener(e -> new BookingsView(username).setVisible(true));
        cancelBookingBtn.addActionListener(e -> new CancelBookingDialog(this, username).setVisible(true));

        buttonPanel.add(viewVehiclesBtn);
        buttonPanel.add(newBookingBtn);
        buttonPanel.add(viewBookingsBtn);
        buttonPanel.add(cancelBookingBtn);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Footer panel with logout button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(new Color(173, 216, 230));
        JButton logoutBtn = createStyledButton("Logout");
        logoutBtn.addActionListener(e -> logout());
        footerPanel.add(logoutBtn);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private JMenu createStyledMenu(String title) {
        JMenu menu = new JMenu(title);
        menu.setForeground(Color.WHITE);
        menu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return menu;
    }

    private JMenuItem createStyledMenuItem(String title) {
        JMenuItem item = new JMenuItem(title);
        item.setBackground(new Color(240, 248, 255));
        item.setForeground(new Color(0, 0, 139));
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return item;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(70, 130, 180));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setPreferredSize(new Dimension(200, 50));
        button.setFocusPainted(false);
        return button;
    }

    private JButton createCancelButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(220, 80, 80));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setPreferredSize(new Dimension(200, 50));
        button.setFocusPainted(false);
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CustomerDashboard dashboard = new CustomerDashboard("testuser");
            dashboard.setVisible(true);
        });
    }
}