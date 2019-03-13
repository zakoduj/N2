package Algorithm;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ImageLoader {
    private final ClassLoader classLoader;

    public ImageLoader() {
        this.classLoader = ClassLoader.getSystemClassLoader();
    }

    public ImageIcon loadAsImageIcon(String resName) {
        URL url = this.classLoader.getResource(resName);
        return new ImageIcon(url);
    }

    Image loadAsImage(String resName) {
        return this.loadAsImageIcon(resName).getImage();
    }
}
