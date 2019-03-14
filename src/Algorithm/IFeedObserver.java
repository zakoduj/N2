package Algorithm;

/**
 * Ten gosc powinien miec metody, ktore sa wolane aby pobierac dane z feeda.
 */
interface IFeedObserver {
    void onMarketSnapshot(MarketSnapshot snapshot);
}
