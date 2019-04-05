import AI.GRU;
import AI.Link;
import AI.Standardizer;
import Algorithm.ImageLoader;
import Algorithm.Logger;
import com.bulenkov.darcula.DarculaLaf;
import org.apache.commons.lang3.SystemUtils;

import javax.swing.*;
import java.util.*;

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

        Map<double[], double[]> set = new HashMap<double[], double[]>(){{
            put(Standardizer.apply(12, 14, 13, 15, 16), new double[] {0, 0, 1});
            put(Standardizer.apply(15, 13, 11, 9, 5), new double[] {1, 0, 0});
        }};
        GRU gru = new GRU(5, 5 * 5, 3);
        List<Link> chain = new ArrayList<>();
        gru.train(set, chain, error -> error > 0.9);
        double[] cls = gru.classify(chain, 15, 13, 11, 9, 5);

        Map<double[], double[]> set2 = new HashMap<double[], double[]>(){{
            put(Standardizer.apply(1, 2, 1, 2, 1), new double[] {0, 1, 0});
        }};
        gru.train(set2, chain, error -> error > 0.9);
        double[] cls2 = gru.classify(chain, 1, 2, 1, 2, 1);
        double[] cls3 = gru.classify(chain, 15, 13, 11, 9, 5);


        SwingUtilities.invokeLater(() -> {
            AppController appController = new AppController(new Logger());
            appController.setVisible(true);
        });
    }
}
