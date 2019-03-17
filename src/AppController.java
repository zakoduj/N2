import Algorithm.DataSourceExchange;
import Algorithm.Feed;
import Algorithm.ImageLoader;
import Bitmex.Bitmex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AppController extends JFrame {
    private final AppView appView;
    private final AppModel appModel = new AppModel();

    public AppController() {

        this.appView = new AppView(appModel);
        setContentPane(appView.getRootView());
        setDefaultCloseOperation(AppController.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1000, 600));
        setMinimumSize(new Dimension(600, 400));

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menuFile = new JMenu("File");
        menuBar.add(menuFile);

        JMenu menuDataSource = new JMenu("Data source");
        menuDataSource.add(this.createMenuItem("Bitmex", "Live data from bitmex exchange", "stock.png", e -> {
            try {
                // nazwe feedu zawsze bierzemy z menu
                String feedName = e.getActionCommand();
                Feed feed = appModel.feedManager.find(feedName);
                if (feed == null) {
                    // Tu powinien byc feedFactory.create(feedName);
                    feed = new Bitmex(appModel.logger);
                    appModel.feedManager.add(feed);
                }
                appModel.dataSources.add(new DataSourceExchange(feed));
                appView.rebuild();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }));
        menuFile.add(menuDataSource);

        menuFile.addSeparator();
        menuFile.add(this.createMenuItem("Quit", "Quit application", "", e -> System.exit(0)));


        pack();
        center(this);

//        try {
//            Bitmex client = new Bitmex(this.logger);
//            client.connect();
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR", JOptionPane.INFORMATION_MESSAGE);
//        }
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

    private JMenuItem createMenuItem(String name, String tooltip, String iconPath, ActionListener actionListener) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Menu item name cannot be empty!");
        }
        JMenuItem menuItem;
        if (!iconPath.isEmpty()) {
            ImageIcon imageIcon = ImageLoader.loadAsImageIcon(iconPath);
            if (imageIcon != null)
                menuItem = new JMenuItem(name, imageIcon);
            else
                menuItem = new JMenuItem(name);
        } else {
            menuItem = new JMenuItem(name);
        }
        if (!tooltip.isEmpty())
            menuItem.setToolTipText(tooltip);
        menuItem.addActionListener(actionListener);
        return menuItem;
    }
}
