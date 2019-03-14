import Algorithm.DataSource;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.*;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
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

        for (int index = 0; index < 1; index++) {
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

    private JPanel create(DataSource dataSource) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        DefaultPieDataset dataset = new DefaultPieDataset( );
        dataset.setValue( "Short" , new Double( 11 ) );
        dataset.setValue( "Long" , new Double( 20 ) );

//        panel.add(new ChartPanel(createPieChart("Market", dataset)));
//        panel.add(new ChartPanel(createPieChart("Market", dataset)));

        /**
         * Category data set per wszystko co chcemy pokazac na jednym plocie? No tak.
         */
        DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();
        categoryDataset.addValue(1.0, new Integer(1), new Integer(1));
        categoryDataset.addValue(2.0, new Integer(1), new Integer(2));
        categoryDataset.addValue(3.0, new Integer(1), new Integer(3));
        categoryDataset.addValue(5.0, new Integer(1), new Integer(4));

        categoryDataset.addValue(3.0, new Integer(2), new Integer(1));
        categoryDataset.addValue(1.0, new Integer(2), new Integer(2));
        categoryDataset.addValue(2.0, new Integer(2), new Integer(3));
        categoryDataset.addValue(1.0, new Integer(2), new Integer(4));

        DateFormat dateFormat = new SimpleDateFormat("kk:mm");
        DateAxis dateAxis = new DateAxis();
        dateAxis.setLabelPaint(AppColors.text);
        dateAxis.setDateFormatOverride(dateFormat);
        dateAxis.setLowerMargin(0.02);
        dateAxis.setUpperMargin(0.02);
        dateAxis.setTickLabelPaint(AppColors.text);

        NumberAxis priceAxis = new NumberAxis();
        priceAxis.setLabelPaint(AppColors.text);
        priceAxis.setAutoRangeIncludesZero(false);
        priceAxis.setTickLabelPaint(AppColors.text);
        priceAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        CombinedDomainCategoryPlot combinedDomainCategoryPlot = new CombinedDomainCategoryPlot();
        combinedDomainCategoryPlot.setOrientation(PlotOrientation.VERTICAL);
//        combinedDomainCategoryPlot.setGap(10);

        final CategoryAxis domainAxis = combinedDomainCategoryPlot.getDomainAxis();
        domainAxis.setCategoryMargin(0);

        CategoryPlot categoryPlot =
                new CategoryPlot(categoryDataset, domainAxis, priceAxis, new StackedAreaRenderer());
        categoryPlot.setForegroundAlpha(0.5f);
        categoryPlot.setBackgroundPaint(Color.lightGray);
        categoryPlot.setDomainGridlinePaint(Color.white);
        categoryPlot.setRangeGridlinePaint(Color.white);
        categoryPlot.addAnnotation(createCustomAnnotation2("lol"));
        combinedDomainCategoryPlot.add(categoryPlot);
        JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, categoryPlot, true);
        panel.add(new ChartPanel(chart));

        return panel;
    }

    private XYTitleAnnotation createPlotLegend(XYPlot plot) {
        LegendTitle lt = new LegendTitle(plot);
        lt.setItemPaint(AppColors.text);
        lt.setBackgroundPaint(AppColors.window);
        lt.setFrame(new BlockBorder(new RectangleInsets(1,0,0,1), AppColors.grid));
        XYTitleAnnotation ta = new XYTitleAnnotation(0, 0, lt, RectangleAnchor.BOTTOM_LEFT);
        ta.setMaxWidth(0.48);
        return ta;
    }

    private XYTitleAnnotation createCustomAnnotation(String text) {
        TextTitle textTitle = new TextTitle(text);
        textTitle.setPaint(AppColors.text);
        XYTitleAnnotation ta = new XYTitleAnnotation(0.01, 0.98, textTitle, RectangleAnchor.TOP_LEFT);
        ta.setMaxWidth(0.48);
        return ta;
    }

    private CategoryAnnotation createCustomAnnotation2(String text) {
        CategoryAnnotation ta = new CategoryPointerAnnotation(text, new Integer(1), 1, 90);
        return ta;
    }

    /**
     * Tworzy jeden pie chart.
     * @return
     */
    private JFreeChart createPieChart(String title, DefaultPieDataset dataset) {
        PiePlot piePlot = createPiePlot(dataset);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, piePlot, false);
        chart.setBackgroundPaint(new Color(255, 255, 255, 0));
        return chart;
    }

    private PiePlot createPiePlot(DefaultPieDataset dataset) {
        PiePlot piePlot = new PiePlot(dataset);
        piePlot.setBackgroundPaint(AppColors.background);
        piePlot.setOutlineVisible(false);
        piePlot.setOutlineVisible(false);
        piePlot.setShadowXOffset(0);
        piePlot.setShadowYOffset(0);
        return piePlot;
    }

    JPanel getRootView(){
        return this.rootPane;
    }
}
