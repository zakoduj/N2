package Algorithm;

/**
 * Data source powinien wypluwac dane z jakiegos okresu tak samo jak w Neuronie. Kumulacyjnie.
 */
public abstract class DataSource implements IFeedObserver, AutoCloseable {
    @Override
    public void onMarketSnapshot(MarketSnapshot snapshot) {
        // Jak tu przychodzi snapshot, to nastepnie powinien on byc rozdysponowany po chart modelach
    }
}
