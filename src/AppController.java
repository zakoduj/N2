import com.fasterxml.jackson.annotation.JsonProperty;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class Info {
    private String info;
    private String version;
    private String timestamp;
    private String docs;
    private Limit limit;

    @JsonProperty("info")
    public String getInfo() { return info; }
    @JsonProperty("info")
    public void setInfo(String value) { this.info = value; }

    @JsonProperty("version")
    public String getVersion() { return version; }
    @JsonProperty("version")
    public void setVersion(String value) { this.version = value; }

    @JsonProperty("timestamp")
    public String getTimestamp() { return timestamp; }
    @JsonProperty("timestamp")
    public void setTimestamp(String value) { this.timestamp = value; }

    @JsonProperty("docs")
    public String getDocs() { return docs; }
    @JsonProperty("docs")
    public void setDocs(String value) { this.docs = value; }

    @JsonProperty("limit")
    public Limit getLimit() { return limit; }
    @JsonProperty("limit")
    public void setLimit(Limit value) { this.limit = value; }
}

class Limit {
    private long remaining;

    @JsonProperty("remaining")
    public long getRemaining() { return remaining; }
    @JsonProperty("remaining")
    public void setRemaining(long value) { this.remaining = value; }
}

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
            BitmexSocket bitmexSocket = new BitmexSocket(this.logger);
            bitmexSocket.connect();
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
