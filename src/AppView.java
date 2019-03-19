import Algorithm.Category;
import Algorithm.DataSource;
import Algorithm.ImageLoader;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.DefaultCategoryItemRenderer;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

class AppView {
    private final JPanel rootPane = new JPanel();
    private final AppModel appModel;
    private final Plots plots = new Plots();

    AppView(AppModel appModel) {
        this.appModel = appModel;
        this.rootPane.setLayout(new BoxLayout(this.rootPane, BoxLayout.X_AXIS));
        this.rootPane.setBorder(new EmptyBorder(10, 0, 10, 10));

        this.rebuild();
    }

    /**
     * Tu tworzymy caly widok.
     */
    void rebuild() {
        // Usuwamy wszystkie childy naszego root componentu
        this.rootPane.removeAll();

        // Nastepnie dla kazdego data source trzeba zbudowac widok - dlatego tez potrzebujemy tutaj datasources.
        // Jedziemy oczywiscie ze split panem. Trzeba bedzie tutaj wymyslic algorytm budowania siatki.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBorder(new EmptyBorder(0,0,0,0));
        splitPane.setResizeWeight(.5);

        for (int index = 0; index < appModel.dataSources.size(); index++) {
            JPanel panel = this.create(appModel.dataSources.get(index));
            if (index <= 1) {
                splitPane.add(panel);
            } else {
                // Index jest wiekszy od 1, czyli mamy 2+ elementow
                JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPane, panel);
                pane.setBorder(new EmptyBorder(0,0,0,0));
                pane.setResizeWeight(.5);
                splitPane = pane;
            }
        }

        this.rootPane.add(splitPane);
        this.rootPane.revalidate();
        this.rootPane.repaint();
    }

    /**
     * Dobra, teraz co trzeba z datasource?
     * @param dataSource
     * @return
     */
    private JPanel create(DataSource dataSource) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.X_AXIS));
        panel.add(panelInfo);

        JButton buttonClose = new JButton("", ImageLoader.loadAsImageIcon("cancel.png"));
        buttonClose.setToolTipText("Close current data source");
        buttonClose.addActionListener(e -> {
            try {
                dataSource.close();
                appModel.dataSources.remove(dataSource);
                plots.clear();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this.rootPane, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }
            // Usuniecie data sourcea rowniez powoduje przebudowanie okna.
            this.rebuild();
        });
        panelInfo.add(buttonClose);

        /**
         * Dla kazdego datasetu tworzymy teraz UI.
         */
        dataSource.datasets().forEach(dataset -> {
            try {
                if (dataset instanceof CategoryDataset) {
                    CategoryDataset categoryDataset = ((CategoryDataset) dataset);
                    CategoryPlot plot;
                    if (categoryDataset.getRowCount() > 1) {
                        plot = this.createStackedCategoryPlot(categoryDataset);
                    } else {
                        plot = this.createCategoryPlot(categoryDataset);
                    }
                    LegendTitle lt = new LegendTitle(plot);
                    lt.setItemPaint(AppColors.text);
                    lt.setBackgroundPaint(AppColors.window);
                    lt.setFrame(new BlockBorder(new RectangleInsets(1,1,1,1), AppColors.grid));

                    TextTitle tt = new TextTitle("This is\na multiline text\nto demonstrate the wrapping");
                    tt.setTextAlignment(HorizontalAlignment.LEFT);

                    AreaTitleAnnotation ata = new AreaTitleAnnotation(lt);
                    ata.setAnnotationInsets(new RectangleInsets(10, 10, 10, 10));
                    ata.setAnnotationAnchor(RectangleAnchor.TOP_LEFT);
                    plot.addAnnotation(ata);

                    CombinedDomainCategoryPlot combined = plots.find(CombinedDomainCategoryPlot.class, p -> {
                        p.setOrientation(PlotOrientation.VERTICAL);
                        p.setGap(10);
                        CategoryAxis axis = p.getDomainAxis();
                        axis.setCategoryMargin(0);
                        axis.setTickLabelsVisible(false);
                    });
                    combined.add(plot);
                } else if (dataset instanceof XYDataset) {
                    XYDataset xyDataset = ((XYDataset) dataset);

                    NumberAxis priceAxis = new NumberAxis();
                    priceAxis.setLabelPaint(AppColors.text);
                    priceAxis.setAutoRangeIncludesZero(false);
                    priceAxis.setTickLabelPaint(AppColors.text);

                    XYPlot xyPlot = new XYPlot(xyDataset, null, priceAxis, new StandardXYItemRenderer());
                    xyPlot.setBackgroundPaint(AppColors.background);
                    xyPlot.setDomainGridlinePaint(AppColors.grid);
                    xyPlot.setRangeGridlinePaint(AppColors.grid);
                    xyPlot.setNoDataMessage("No data to display");
                    xyPlot.setNoDataMessagePaint(AppColors.text);

                    LegendTitle lt = new LegendTitle(xyPlot);
                    lt.setItemPaint(AppColors.text);
                    lt.setBackgroundPaint(AppColors.window);
                    lt.setFrame(new BlockBorder(new RectangleInsets(1,1,1,1), AppColors.grid));

                    AreaTitleAnnotation ata = new AreaTitleAnnotation(lt);
                    ata.setAnnotationInsets(new RectangleInsets(10, 10, 10, 10));
                    ata.setAnnotationAnchor(RectangleAnchor.TOP_LEFT);
                    xyPlot.addAnnotation(ata);

                    CombinedDomainXYPlot combined = plots.find(CombinedDomainXYPlot.class, p -> {
                        p.setOrientation(PlotOrientation.VERTICAL);
                        p.setGap(10);

                        DateFormat dateFormat = new SimpleDateFormat("kk:mm:ss");

                        DateAxis dateAxis = new DateAxis();
                        dateAxis.setLabelPaint(AppColors.text);
                        dateAxis.setDateFormatOverride(dateFormat);
                        dateAxis.setLowerMargin(0.02);
                        dateAxis.setUpperMargin(0.02);
                        dateAxis.setTickLabelPaint(AppColors.text);
                        p.setDomainAxis(dateAxis);

                    });


                    combined.add(xyPlot);
                } else {
                    throw new Exception("Plot creation for " + dataset.getClass().getSimpleName() + " was not implemented.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this.rootPane, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        });

        /**
         * A teraz budujemy tyle chartów, ile mamy kombinowanych plotów.
         */
        this.plots.forEach(plot -> panel.add(new ChartPanel(new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, false))));
        return panel;
    }

    private CategoryPlot createStackedCategoryPlot(CategoryDataset dataset) {
        CategoryPlot categoryPlot = new CategoryPlot(dataset, null, createRangeAxis(), new StackedAreaRenderer());
        categoryPlot.setForegroundAlpha(0.5f);
        categoryPlot.setNoDataMessage("No data to display");
        categoryPlot.setBackgroundPaint(AppColors.background);
        categoryPlot.setDomainGridlinePaint(AppColors.grid);
        categoryPlot.setRangeGridlinePaint(AppColors.grid);
        return categoryPlot;
    }

    private CategoryPlot createCategoryPlot(CategoryDataset dataset) {
        CategoryPlot categoryPlot = new CategoryPlot(dataset, null, createRangeAxis(), new DefaultCategoryItemRenderer());
        categoryPlot.setForegroundAlpha(0.5f);
        categoryPlot.setNoDataMessage("No data to display");
        categoryPlot.setBackgroundPaint(AppColors.background);
        categoryPlot.setDomainGridlinePaint(AppColors.grid);
        categoryPlot.setRangeGridlinePaint(AppColors.grid);

        return categoryPlot;
    }

    private ValueAxis createRangeAxis() {
        NumberAxis axis = new NumberAxis();
        axis.setLabelPaint(AppColors.text);
        axis.setAutoRangeIncludesZero(false);
        axis.setTickLabelPaint(AppColors.text);
        return axis;
    }

    JPanel getRootView(){
        return this.rootPane;
    }
}
