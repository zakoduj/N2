package Algorithm;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;

import java.util.*;

/**
 * Tu, on juz by musial otrzymywac jakies sensowne dane. Datasource moze zwracac N-datasets.
 * Datasource zatem zarzadza datasetami.
 */
public abstract class DataSource implements IFeedObserver, AutoCloseable {

    /**
     * Kazdy datasource powinien zwracac dataset.
     * @return
     */
    public List<Dataset> datasets() {
        TimeSeriesCollection lol = new TimeSeriesCollection();
        TimeSeries timeSeries = new TimeSeries("Price");
        lol.addSeries(timeSeries);
        timeSeries.addOrUpdate(new TimeSeriesDataItem(new FixedMillisecond(new Date(2019, 3, 17, 1, 1, 1)), 44));
        timeSeries.addOrUpdate(new TimeSeriesDataItem(new FixedMillisecond(new Date(2019, 3, 17, 1, 1, 2)), 50));
        timeSeries.addOrUpdate(new TimeSeriesDataItem(new FixedMillisecond(new Date(2019, 3, 17, 1, 1, 3)), 47));
        timeSeries.addOrUpdate(new TimeSeriesDataItem(new FixedMillisecond(new Date(2019, 3, 17, 1, 1, 4)), 41));

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1.0, Category.LONGS, new Integer(1));
        dataset.addValue(2.0, Category.LONGS, new Integer(2));
        dataset.addValue(3.0, Category.LONGS, new Integer(3));
        dataset.addValue(5.0, Category.LONGS, new Integer(4));

        dataset.addValue(3.0, Category.SHORTS, new Integer(1));
        dataset.addValue(1.0, Category.SHORTS, new Integer(2));
        dataset.addValue(2.0, Category.SHORTS, new Integer(3));
        dataset.addValue(1.0, Category.SHORTS, new Integer(4));

        DefaultCategoryDataset executions = new DefaultCategoryDataset();
        executions.addValue(11.0, Category.BUYS, new Integer(1));
        executions.addValue(12.0, Category.BUYS, new Integer(2));
        executions.addValue(31.0, Category.BUYS, new Integer(3));
        executions.addValue(25.0, Category.BUYS, new Integer(4));

        executions.addValue(13.0, Category.SELLS, new Integer(1));
        executions.addValue(9.0, Category.SELLS, new Integer(2));
        executions.addValue(2.0, Category.SELLS, new Integer(3));
        executions.addValue(1.0, Category.SELLS, new Integer(4));

        return new ArrayList<>(Arrays.asList(lol, dataset, executions));
    }
}
