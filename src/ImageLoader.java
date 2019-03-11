import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ImageLoader {
    private final ClassLoader classLoader;

    ImageLoader() {
        this.classLoader = ClassLoader.getSystemClassLoader();
    }

    ImageIcon loadAsImageIcon(String resName) {
        URL url = this.classLoader.getResource(resName);
        return new ImageIcon(url);
    }

    Image loadAsImage(String resName) {
        return this.loadAsImageIcon(resName).getImage();
    }
}
