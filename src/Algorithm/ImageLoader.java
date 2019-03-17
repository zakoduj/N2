package Algorithm;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ImageLoader {
    public static ImageIcon loadAsImageIcon(String resName) {
        final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        URL url = classLoader.getResource(resName);
        if (url == null) {
            throw new IllegalArgumentException(resName + " is not contained within resources.");
        }
        return new ImageIcon(url);
    }

    public static Image loadAsImage(String resName) {
        return loadAsImageIcon(resName).getImage();
    }
}
