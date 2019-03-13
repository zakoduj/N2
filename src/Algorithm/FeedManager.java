package Algorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * Algorithm.Feed manager ma za zadanie zarzadzac roznymi feedami.
 */
public class FeedManager {
    private final Map<String, Feed> feedMap;

    public FeedManager() {
        this.feedMap = new HashMap<>();
    }

    public void add(Feed feed) {
        this.feedMap.put(feed.toString(), feed);
    }

    public Feed find(String name) {
        return this.feedMap.get(name);
    }

    public <T extends Feed> T find(Class<T> type) {
        for (Map.Entry<String, Feed> entry : this.feedMap.entrySet()) {
            if (type.isInstance(entry.getValue())) {
                return type.cast(entry.getValue());
            }
        }
        return null;
    }

    public void remove(String name) {
        this.feedMap.remove(name);
    }

    public <T extends Feed> void remove(Class<T> type) {
        Feed feed = this.find(type);
        if (feed != null) {
            this.feedMap.remove(feed.toString());
        }
    }

    public void clear() {
        for (Map.Entry<String, Feed> entry : this.feedMap.entrySet()) {
            entry.getValue().close();
        }
        this.feedMap.clear();
    }
}
