import Algorithm.Logger;

import javax.swing.*;
import java.awt.*;

public class AppController extends JFrame {
    private final AppView appView;
    private final Logger logger = new Logger();

    public AppController() {
        this.appView = new AppView();
        setContentPane(appView.getRootView());
        setDefaultCloseOperation(AppController.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1000, 600));
        setMinimumSize(new Dimension(600, 400));
        pack();
        center(this);

        try {
            Bitmex.Client client = new Bitmex.Client(this.logger);
            client.connect();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /*
    Center frame logic.
     */
    private void center(Window window){
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int)(dimension.getWidth() - window.getWidth()) / 2;
        int y = (int)(dimension.getHeight() - window.getHeight()) / 2;
        window.setLocation(x, y);
    }
}
