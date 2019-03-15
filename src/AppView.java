import Algorithm.DataSource;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.*;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.DefaultCategoryItemRenderer;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class AppView {
    private final JPanel rootPane = new JPanel();
    private final AppModel appModel;

    public AppView(AppModel appModel) {
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

        for (int index = 0; index < 3; index++) {
            JPanel panel = this.create(null);
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

        DefaultPieDataset dataset = new DefaultPieDataset( );
        dataset.setValue( "Short" , new Double( 11 ) );
        dataset.setValue( "Long" , new Double( 20 ) );


        /**
         * Category data set per wszystko co chcemy pokazac na jednym plocie? No tak. Pozycje
         */
        DefaultCategoryDataset positions = new DefaultCategoryDataset();
        positions.addValue(1.0, Category.LONGS, new Integer(1));
        positions.addValue(2.0, Category.LONGS, new Integer(2));
        positions.addValue(3.0, Category.LONGS, new Integer(3));
        positions.addValue(5.0, Category.LONGS, new Integer(4));

        positions.addValue(3.0, Category.SHORTS, new Integer(1));
        positions.addValue(1.0, Category.SHORTS, new Integer(2));
        positions.addValue(2.0, Category.SHORTS, new Integer(3));
        positions.addValue(1.0, Category.SHORTS, new Integer(4));

        /**
         * Wykonania (pozycji)
         */
        DefaultCategoryDataset executions = new DefaultCategoryDataset();
        executions.addValue(11.0, Category.BUYS, new Integer(1));
        executions.addValue(12.0, Category.BUYS, new Integer(2));
        executions.addValue(31.0, Category.BUYS, new Integer(3));
        executions.addValue(25.0, Category.BUYS, new Integer(4));

        executions.addValue(13.0, Category.SELLS, new Integer(1));
        executions.addValue(9.0, Category.SELLS, new Integer(2));
        executions.addValue(2.0, Category.SELLS, new Integer(3));
        executions.addValue(1.0, Category.SELLS, new Integer(4));

        /**
         * Cena
         */
        DefaultCategoryDataset prices = new DefaultCategoryDataset();
        prices.addValue(3600.0, Category.PRICE, new Integer(1));
        prices.addValue(3605.0, Category.PRICE, new Integer(2));
        prices.addValue(3649.0, Category.PRICE, new Integer(3));
        prices.addValue(3599.0, Category.PRICE, new Integer(4));

        CombinedDomainCategoryPlot combinedDomainCategoryPlot = this.createCombinedDomainCategoryPlot();
        combinedDomainCategoryPlot.add(this.createCategoryPlot(prices));
        combinedDomainCategoryPlot.add(this.createStackedCategoryPlot(positions));
        combinedDomainCategoryPlot.add(this.createStackedCategoryPlot(executions));

        JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, combinedDomainCategoryPlot, true);
        panel.add(new ChartPanel(chart));

        return panel;
    }

    private CombinedDomainCategoryPlot createCombinedDomainCategoryPlot() {
        CombinedDomainCategoryPlot plot = new CombinedDomainCategoryPlot();
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setGap(10);
        CategoryAxis axis = plot.getDomainAxis();
        axis.setCategoryMargin(0);
        axis.setTickLabelsVisible(false);
        return plot;
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
