import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class AppView {
    private JPanel rootPane = new JPanel();

    public AppView() {
        this.rootPane.setLayout(new BoxLayout(this.rootPane, BoxLayout.X_AXIS));
        this.rootPane.setBorder(new EmptyBorder(10, 0, 10, 10));
    }

    JPanel getRootView(){
        return this.rootPane;
    }
}
