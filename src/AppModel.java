import Algorithm.DataSources;
import Algorithm.FeedManager;
import Algorithm.Logger;

/**
 * Appmodel bedzie mial kolekcje data source.
 */
public class AppModel {
    final DataSources dataSources = new DataSources();
    final FeedManager feedManager = new FeedManager();
    final Logger logger = new Logger();
}
