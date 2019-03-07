import javax.swing.*;

public class N2 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppController appController = new AppController();
            appController.setVisible(true);
        });
    }
}
