package Algorithm;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.time.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class DatasetPrices {
    private final int capacity;
    private final TimeSeriesCollection dataset = new TimeSeriesCollection(new TimeSeries("Price"));

    /**
     * @param capacity - ile ostatnich wartosci trzymamy.
     */
    DatasetPrices(int capacity) {
        this.capacity = capacity;
    }

    void update(Market market) {
        if (market == null) {
            return;
        }
        Double price = market.price();
        if (price == null) {
            return;
        }
        System.out.println("Price: " + price);
        RegularTimePeriod regularTimePeriod = new FixedMillisecond();
        TimeSeries timeSeries = this.dataset.getSeries(0);
        while (timeSeries.getItemCount() > this.capacity) {
            timeSeries.delete(0, 1);
        }
        TimeSeriesDataItem timeSeriesDataItem = new TimeSeriesDataItem(regularTimePeriod, price);
        timeSeries.addOrUpdate(timeSeriesDataItem);
    }

    Dataset dataset() {
        return this.dataset;
    }
}

class DatasetBooks {
    private final int capacity;
    private final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    DatasetBooks(int capacity) {
        this.capacity = capacity;
    }

    void update(Market market) {
        if (market == null || market.asks == null || market.bids == null) {
            return;
        }
        while (this.dataset.getColumnCount() > this.capacity) {
            this.dataset.removeColumn(0);
        }
        Integer columnCount = this.dataset.getColumnCount();
        dataset.addValue(market.asks.size, "asks", columnCount);
        dataset.addValue(market.bids.size, "bids", columnCount);
    }

    Dataset dataset() {
        return this.dataset;
    }
}

class DatasetTrades {
    private final int capacity;
    private final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    DatasetTrades(int capacity) {
        this.capacity = capacity;
    }

    void update(Market market) {
        if (market == null || market.buys == null || market.sells == null) {
            return;
        }
        while (this.dataset.getColumnCount() > this.capacity) {
            this.dataset.removeColumn(0);
        }
        Integer columnCount = this.dataset.getColumnCount();
        dataset.addValue(market.buys.volume, "buys", columnCount);
        dataset.addValue(market.sells.volume, "sells", columnCount);
    }

    Dataset dataset() {
        return this.dataset;
    }
}

/**
 * W przypadku takiego datasource'a, jakie datasety powinny sie tu znalezc:
 */
public class DataSourceExchange extends DataSource implements IFeedObserver {
    private final Feed feed;

    // Datasets - capacity
    private final DatasetPrices datasetPrices = new DatasetPrices(10);
    private final DatasetBooks datasetOrders = new DatasetBooks(10);
    private final DatasetTrades datasetTrades = new DatasetTrades(10);

    public DataSourceExchange(Feed feed) {
        this.feed = feed;
        this.feed.addObserver(this);
    }

    private DataSourceExchange(DataSourceExchange other) {
        this.feed = other.feed;
        this.feed.addObserver(this);
    }

    @Override
    public void close() {
        this.feed.removeObserver(this);
    }

    @Override
    public DataSource clone() {
        return new DataSourceExchange(this);
    }

    @Override
    public void onMarketUpdate(Market market) {
        if (market == null) {
            // Market jak najbardziej moze byc nullem.
            return;
        }
        datasetPrices.update(market);
        datasetOrders.update(market);
        datasetTrades.update(market);
    }

    @Override
    public List<Dataset> datasets() {
//        TimeSeriesCollection lol = new TimeSeriesCollection(new TimeSeries("Price"));
//        lol.getSeries(0).addOrUpdate(new TimeSeriesDataItem(new FixedMillisecond(new Date(2019, 3, 17, 1, 1, 1)), 44));
//        lol.getSeries(0).addOrUpdate(new TimeSeriesDataItem(new FixedMillisecond(new Date(2019, 3, 17, 1, 1, 2)), 50));
//        lol.getSeries(0).addOrUpdate(new TimeSeriesDataItem(new FixedMillisecond(new Date(2019, 3, 17, 1, 1, 3)), 47));
//        lol.getSeries(0).addOrUpdate(new TimeSeriesDataItem(new FixedMillisecond(new Date(2019, 3, 17, 1, 1, 4)), 41));
//
//        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//        dataset.addValue(1.0, Category.LONGS, new Integer(1));
//        dataset.addValue(2.0, Category.LONGS, new Integer(2));
//        dataset.addValue(3.0, Category.LONGS, new Integer(3));
//        dataset.addValue(5.0, Category.LONGS, new Integer(4));
//
//        dataset.addValue(3.0, Category.SHORTS, new Integer(1));
//        dataset.addValue(1.0, Category.SHORTS, new Integer(2));
//        dataset.addValue(2.0, Category.SHORTS, new Integer(3));
//        dataset.addValue(1.0, Category.SHORTS, new Integer(4));
//
//        DefaultCategoryDataset executions = new DefaultCategoryDataset();
//        executions.addValue(11.0, Category.BUYS, new Integer(1));
//        executions.addValue(12.0, Category.BUYS, new Integer(2));
//        executions.addValue(31.0, Category.BUYS, new Integer(3));
//        executions.addValue(25.0, Category.BUYS, new Integer(4));
//
//        executions.addValue(13.0, Category.SELLS, new Integer(1));
//        executions.addValue(9.0, Category.SELLS, new Integer(2));
//        executions.addValue(2.0, Category.SELLS, new Integer(3));
//        executions.addValue(1.0, Category.SELLS, new Integer(4));

        return new ArrayList<>(Arrays.asList(this.datasetPrices.dataset(), this.datasetOrders.dataset(), this.datasetTrades.dataset()));
    }
}
