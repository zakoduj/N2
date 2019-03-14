import Algorithm.ImageLoader;
import com.bulenkov.darcula.DarculaLaf;
import org.apache.commons.lang3.SystemUtils;

import javax.swing.*;

public class N2 {
    public static void main(String[] args) {
        ImageLoader imageLoader = new ImageLoader();
        try {
            UIManager.setLookAndFeel(new DarculaLaf());
            if (SystemUtils.IS_OS_MAC) {
                com.apple.eawt.Application application = com.apple.eawt.Application.getApplication();
                if (application != null) {
                    application.setDockIconImage(imageLoader.loadAsImage("N2.png"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            AppController appController = new AppController(imageLoader);
            appController.setVisible(true);
        });
    }
}
