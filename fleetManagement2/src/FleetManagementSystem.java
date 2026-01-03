// FleetManagementSystem.java
import javax.swing.*;

public class FleetManagementSystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}