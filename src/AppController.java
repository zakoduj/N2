import Algorithm.DataSourceExchange;
import Algorithm.Feed;
import Algorithm.ILogger;
import Algorithm.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

class AppController extends JFrame {
    private final AppView appView;
    private final AppModel appModel = new AppModel();
    private final Feeds feeds = new Feeds();

    AppController(ILogger logger) {
        this.appView = new AppView(appModel);
        this.setContentPane(appView.getRootView());
        this.setDefaultCloseOperation(AppController.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(1000, 600));
        this.setMinimumSize(new Dimension(600, 400));

        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        JMenu menuFile = new JMenu("File");
        menuBar.add(menuFile);

        JMenu menuDataSource = new JMenu("Data source");
        this.feeds.forEach(aClass -> {
            JMenuItem menuItem = this.createMenuItem(aClass.getSimpleName(), "stock.png", e -> {
                try {
                    Feed feed = appModel.feedManager.find(aClass);
                    if (feed == null) {
                        feed = feeds.create(aClass, logger);
                        appModel.feedManager.add(feed);
                    }
                    appModel.dataSources.add(new DataSourceExchange(feed));
                    appView.rebuild();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            });
            menuDataSource.add(menuItem);
        });
        menuFile.add(menuDataSource);

        menuFile.addSeparator();
        menuFile.add(this.createMenuItem("Quit", "", e -> System.exit(0)));

        this.pack();
        this.center(this);
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

    private JMenuItem createMenuItem(String name, String iconPath, ActionListener actionListener) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Menu item name cannot be empty!");
        }
        JMenuItem menuItem;
        if (!iconPath.isEmpty()) {
            menuItem = new JMenuItem(name, ImageLoader.loadAsImageIcon(iconPath));
        } else {
            menuItem = new JMenuItem(name);
        }
        menuItem.addActionListener(actionListener);
        return menuItem;
    }
}
