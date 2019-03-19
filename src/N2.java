import Algorithm.ImageLoader;
import Algorithm.Logger;
import com.bulenkov.darcula.DarculaLaf;
import org.apache.commons.lang3.SystemUtils;

import javax.swing.*;

/**
 * Logger jest per cala aplikacja. Logger jest takim systemem jednostronnej komunikacji z tworca aplikacji.
 */
public class N2 {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new DarculaLaf());
            if (SystemUtils.IS_OS_MAC) {
                com.apple.eawt.Application application = com.apple.eawt.Application.getApplication();
                if (application != null) {
                    application.setDockIconImage(ImageLoader.loadAsImage("N2.png"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            AppController appController = new AppController(new Logger());
            appController.setVisible(true);
        });
    }
}
