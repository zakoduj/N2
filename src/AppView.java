import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AppView {
    private JPanel rootPane = new JPanel();

    public AppView() {
        this.rootPane.setLayout(new BoxLayout(this.rootPane, BoxLayout.X_AXIS));
        this.rootPane.setBorder(new EmptyBorder(10, 0, 10, 10));

        DefaultPieDataset dataset = new DefaultPieDataset( );
        dataset.setValue( "Short" , new Double( 11 ) );
        dataset.setValue( "Long" , new Double( 20 ) );

        JFreeChart chart = ChartFactory.createPieChart(
                "Market",   // chart title
                dataset,          // data
                true,             // include legend
                true,
                false);
        chart.setBackgroundPaint(new Color(255, 255, 255, 0));
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setOutlineVisible(false);
        plot.setShadowXOffset(0);
        plot.setShadowYOffset(0);


        ChartPanel chartpanel = new ChartPanel(chart);
        this.rootPane.add(chartpanel);
    }

    JPanel getRootView(){
        return this.rootPane;
    }
}
