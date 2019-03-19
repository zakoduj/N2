package Algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Algorithm.Feed manager ma za zadanie zarzadzac roznymi feedami.
 */
public class FeedManager {
    private final List<Feed> feeds = new ArrayList<>();

    public void add(Feed feed) {
        this.feeds.add(feed);
    }

    public <T extends Feed> T find(Class<T> type) {
        for (Feed feed : this.feeds) {
            if (type.isInstance(feed)) {
                return type.cast(feed);
            }
        }
        return null;
    }

    public void remove(Feed feed) {
        this.feeds.remove(feed);
    }

    public <T extends Feed> void remove(Class<T> type) {
        Feed feed = this.find(type);
        if (feed != null) {
            this.feeds.remove(feed);
        }
    }

    public void clear() {
        this.feeds.forEach(feed -> feed.close());
        this.feeds.clear();
    }
}
