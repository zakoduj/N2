package Algorithm;

import TA.MACD;
import TA.Tool;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.time.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

interface DatasetUpdatable<T extends Dataset> {
    void update(Market market);
    void capacity(int value);
    T dataset();
}

class Prices extends TimeSeriesCollection implements DatasetUpdatable<TimeSeriesCollection> {
    private int capacity;

    Prices(int capacity) {
        this.capacity = capacity;
        this.addSeries(new TimeSeries("Price"));
    }

    @Override
    public void update(Market market) {
        Double price = market.price();
        if (price == null) {
            return;
        }
        RegularTimePeriod regularTimePeriod = new FixedMillisecond();
        TimeSeries timeSeries = this.getSeries(0);
        while (timeSeries.getItemCount() >= this.capacity) {
            timeSeries.delete(0, 1);
        }
        TimeSeriesDataItem timeSeriesDataItem = new TimeSeriesDataItem(regularTimePeriod, price);
        timeSeries.addOrUpdate(timeSeriesDataItem);
    }

    @Override
    public void capacity(int value) {
        this.capacity = value;
    }

    @Override
    public TimeSeriesCollection dataset() {
        return this;
    }
}

class Indicator extends TimeSeriesCollection implements DatasetUpdatable<TimeSeriesCollection> {
    private int capacity;
    private MACD macd = new MACD(12, 26, 9);

    private enum Type {
        Macd,
        Signal,
        Histogram
    }

    Indicator(int capacity) {
        this.capacity = capacity;
        this.addSeries(new TimeSeries(Type.Macd));
        this.addSeries(new TimeSeries(Type.Signal));
        this.addSeries(new TimeSeries(Type.Histogram));
    }

    @Override
    public void update(Market market) {
        Double price = market.price();
        if (price == null) {
            return;
        }

        // Procesujemy!
        Tool.ComplexResult complexResult = this.macd.next(price);

        // Obecny czas
        RegularTimePeriod timePeriod = new FixedMillisecond();

        // I aktualizujemy wszystko
        this.updateSeries(Type.Macd, timePeriod, complexResult.get(MACD.Macd.class).value);
        this.updateSeries(Type.Signal, timePeriod, complexResult.get(MACD.Signal.class).value);
        this.updateSeries(Type.Histogram, timePeriod, complexResult.get(MACD.Histogram.class).value);
    }

    @Override
    public void capacity(int value) {
        this.capacity = value;
    }

    @Override
    public TimeSeriesCollection dataset() {
        return this;
    }

    private void updateSeries(Type type, RegularTimePeriod timePeriod, double value) {
        TimeSeries timeSeries = this.getSeries(type);
        if (timeSeries != null) {
            while (timeSeries.getItemCount() >= this.capacity) {
                timeSeries.delete(0, 1);
            }
            TimeSeriesDataItem timeSeriesDataItem = new TimeSeriesDataItem(timePeriod, value);
            timeSeries.addOrUpdate(timeSeriesDataItem);
        }
    }
}

class Books extends DefaultCategoryDataset implements DatasetUpdatable<DefaultCategoryDataset> {
    private int capacity;
    private List<Double> asks = new ArrayList<>();
    private List<Double> bids = new ArrayList<>();

    private enum Type {
        Asks,
        Bids
    }

    Books(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public void update(Market market) {
        if (market.asks == null || market.bids == null) {
            return;
        }

        while (this.asks.size() >= this.capacity) {
            this.asks.remove(0);
        }
        this.asks.add(market.asks.size);

        while (this.bids.size() >= this.capacity) {
            this.bids.remove(0);
        }
        this.bids.add(market.bids.size);

        for (int i = 0; i < this.asks.size(); i++) {
            this.setValue(this.asks.get(i), Type.Asks, new Integer(i));
        }
        for (int i = 0; i < this.bids.size(); i++) {
            this.setValue(this.bids.get(i), Type.Bids, new Integer(i));
        }
    }

    @Override
    public void capacity(int value) {
        this.capacity = value;
    }

    @Override
    public DefaultCategoryDataset dataset() {
        return this;
    }
}

class Trades extends DefaultCategoryDataset implements DatasetUpdatable<DefaultCategoryDataset> {
    private int capacity;
    private List<Double> buys = new ArrayList<>();
    private List<Double> sells = new ArrayList<>();

    private enum Type {
        Buys,
        Sells
    }

    Trades(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public void update(Market market) {
        if (market.buys == null || market.sells == null) {
            return;
        }

        while (this.buys.size() >= this.capacity) {
            this.buys.remove(0);
        }
        this.buys.add(market.buys.volume);

        while (this.sells.size() >= this.capacity) {
            this.sells.remove(0);
        }
        this.sells.add(market.sells.volume);

        for (int i = 0; i < this.buys.size(); i++) {
            this.setValue(this.buys.get(i), Type.Buys, new Integer(i));
        }
        for (int i = 0; i < this.sells.size(); i++) {
            this.setValue(this.sells.get(i), Type.Sells, new Integer(i));
        }
    }

    @Override
    public void capacity(int value) {
        this.capacity = value;
    }

    @Override
    public DefaultCategoryDataset dataset() {
        return this;
    }
}

class Datasets implements Iterable<DatasetUpdatable> {
    private final List<DatasetUpdatable> datasets = new ArrayList<>();

    Datasets(int capacity) {
        this.datasets.add(new Prices(capacity));
        this.datasets.add(new Indicator(capacity));
        this.datasets.add(new Books(capacity));
        this.datasets.add(new Trades(capacity));
    }

    List<Dataset> datasets() {
        return this.datasets.stream().map(x -> x.dataset()).collect(Collectors.toList());
    }

    @Override
    public Iterator<DatasetUpdatable> iterator() {
        return this.datasets.iterator();
    }

    @Override
    public void forEach(Consumer<? super DatasetUpdatable> action) {
        this.datasets.forEach(action);
    }

    @Override
    public Spliterator<DatasetUpdatable> spliterator() {
        return this.datasets.spliterator();
    }
}

class Cumulation {
    private LocalDateTime lastTimeStamp = null;
    private int step = 0;
    private List<Market> temp = new ArrayList<>();

    void setStep(int value) {
        this.step = value;
    }

    synchronized void update(Market market, Consumer<Market> consumer) {
        LocalDateTime current = LocalDateTime.now();
        if (this.lastTimeStamp == null) {
            this.lastTimeStamp = current;
        } else {
            Duration duration = Duration.between(this.lastTimeStamp, current);
            if (duration.getSeconds() >= step) {
                // construct averaged market snapshit
                // Collect all trades from current period
                List<Market.Trades> sells = this.temp.stream().map(x -> x.sells).filter(Objects::nonNull).collect(Collectors.toList());
                List<Market.Trades> buys = this.temp.stream().map(x -> x.buys).filter(Objects::nonNull).collect(Collectors.toList());

                List<Market.Book> asks = this.temp.stream().map(x -> x.asks).filter(Objects::nonNull).collect(Collectors.toList());
                List<Market.Book> bids = this.temp.stream().map(x -> x.bids).filter(Objects::nonNull).collect(Collectors.toList());

                // Construct averaged market snapshit
                Market cumulative = new Market(Market.cumulateTrades(buys), Market.cumulateTrades(sells), Market.cumulateBook(asks), Market.cumulateBook(bids));
                consumer.accept(cumulative);

                // Save current time as a last time
                this.lastTimeStamp = current;

                // Reset collection
                this.temp = new ArrayList<>();
            }
        }
        temp.add(market);
    }
}

/**
 * W przypadku takiego datasource'a, jakie datasety powinny sie tu znalezc:
 */
public class DataSourceExchange extends DataSource implements IFeedObserver {
    private final Feed feed;
    private final Datasets datasets = new Datasets(10);
    private final Cumulation cumulation = new Cumulation();

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
        this.cumulation.update(market, cumulative -> this.datasets.forEach(datasetUpdatable -> datasetUpdatable.update(cumulative)));

    }

    @Override
    public List<Dataset> datasets() {
        return this.datasets.datasets();
    }

    @Override
    public void updateCapacity(int value) {
        this.datasets.forEach(datasetUpdatable -> datasetUpdatable.capacity(value));
    }

    @Override
    public void updateStep(int value) {
        this.cumulation.setStep(value);
    }
}
