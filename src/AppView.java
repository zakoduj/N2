import Algorithm.DataSource;
import Algorithm.ImageLoader;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

class AppView {
    private final JPanel rootPane = new JPanel();
    private final AppModel appModel;

    // todo: tu jest bug. Algorithm.Plots powinien byc per datasource
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

        // Wszystkie ploty oczywiscie wylatuja
        this.plots.clear();

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

        JPanel panelTimePeriod = new JPanel();
        panelInfo.add(panelTimePeriod);
        panelTimePeriod.add(new JLabel("Cap"));
        SpinnerNumberModel spinnerNumberModelCapacity = new SpinnerNumberModel(10, 10, 100, 1);
        JSpinner spinnerCapacity = new JSpinner(spinnerNumberModelCapacity);
        spinnerCapacity.addChangeListener(e -> {
            JSpinner caller = (JSpinner) e.getSource();
            int value = (int) caller.getValue();
            dataSource.updateCapacity(value);
        });
        panelTimePeriod.add(spinnerCapacity);

        panelTimePeriod.add(new JLabel("Step"));
        SpinnerNumberModel spinnerNumberModelStep = new SpinnerNumberModel(0, 0, 100, 1);
        JSpinner spinnerStep = new JSpinner(spinnerNumberModelStep);
        spinnerStep.addChangeListener(e -> {
            JSpinner caller = (JSpinner) e.getSource();
            int value = (int) caller.getValue();
            dataSource.updateStep(value);
        });
        panelTimePeriod.add(spinnerStep);

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
                    plot = this.createStackedCategoryPlot(categoryDataset);

                    // Find existing plot or create a new one
                    CombinedDomainCategoryPlot combined = this.plots.find(dataSource, CombinedDomainCategoryPlot.class, p -> {
                        p.setOrientation(PlotOrientation.VERTICAL);
                        p.setGap(10);
                        CategoryAxis axis = p.getDomainAxis();
                        axis.setCategoryMargin(0);
                        axis.setTickLabelsVisible(false);
                    });
                    combined.add(plot);
                } else if (dataset instanceof XYDataset) {
                    XYDataset xyDataset = ((XYDataset) dataset);
                    XYPlot xyPlot = this.createXYPlot(xyDataset);

                    // Find existing plot or create a new one
                    CombinedDomainXYPlot combined = this.plots.find(dataSource, CombinedDomainXYPlot.class, p -> {
                        p.setOrientation(PlotOrientation.VERTICAL);
                        p.setGap(10);
                        p.setDomainAxis(this.createDateAxis());

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
        this.plots.iterateFor(dataSource, plot -> panel.add(new ChartPanel(new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, false))));
        return panel;
    }

    private XYPlot createXYPlot(XYDataset dataset) {
        XYPlot xyPlot = new XYPlot(dataset, null, this.createNumberAxis(), new StandardXYItemRenderer());
        xyPlot.setBackgroundPaint(AppColors.background);
        xyPlot.setDomainGridlinePaint(AppColors.grid);
        xyPlot.setRangeGridlinePaint(AppColors.grid);
        xyPlot.setNoDataMessage("No data to display");
        xyPlot.setNoDataMessagePaint(AppColors.text);
        xyPlot.addAnnotation(this.createPlotLegend(xyPlot));
        return xyPlot;
    }

    private DateAxis createDateAxis() {
        DateAxis dateAxis = new DateAxis();
        dateAxis.setLabelPaint(AppColors.text);
        dateAxis.setDateFormatOverride(new SimpleDateFormat("kk:mm:ss"));
        dateAxis.setTickLabelPaint(AppColors.text);
        return dateAxis;
    }

    private AreaTitleAnnotation createPlotLegend(LegendItemSource source) {
        LegendTitle lt = new LegendTitle(source);
        lt.setItemPaint(AppColors.text);
        lt.setBackgroundPaint(AppColors.window);
        lt.setFrame(new BlockBorder(new RectangleInsets(1,1,1,1), AppColors.grid));
        AreaTitleAnnotation ata = new AreaTitleAnnotation(lt);
        ata.setAnnotationInsets(new RectangleInsets(10, 10, 10, 10));
        ata.setAnnotationAnchor(RectangleAnchor.TOP_LEFT);
        return ata;
    }

    private CategoryPlot createStackedCategoryPlot(CategoryDataset dataset) {
        CategoryPlot categoryPlot = new CategoryPlot(dataset, null, createNumberAxis(), new StackedAreaRenderer());
        categoryPlot.setForegroundAlpha(0.5f);
        categoryPlot.setNoDataMessage("No data to display");
        categoryPlot.setNoDataMessagePaint(AppColors.text);
        categoryPlot.setBackgroundPaint(AppColors.background);
        categoryPlot.setDomainGridlinePaint(AppColors.grid);
        categoryPlot.setRangeGridlinePaint(AppColors.grid);
        categoryPlot.addAnnotation(this.createPlotLegend(categoryPlot));
        return categoryPlot;
    }

    private ValueAxis createNumberAxis() {
        NumberAxis axis = new NumberAxis();
        axis.setLabelPaint(AppColors.text);
        axis.setAutoRangeIncludesZero(false);
        axis.setTickLabelPaint(AppColors.text);
        axis.setNumberFormatOverride(new DecimalFormat(".####"));
        return axis;
    }

    JPanel getRootView(){
        return this.rootPane;
    }
}
