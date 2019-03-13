package Algorithm;

public class DataSourceExchange extends DataSource implements IFeedObserver {
    private final Feed feed;

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
}
