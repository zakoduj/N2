package Algorithm;

import org.jfree.data.general.Dataset;

import java.util.List;

/**
 * Tu, on juz by musial otrzymywac jakies sensowne dane. Datasource moze zwracac N-datasets.
 * Datasource zatem zarzadza datasetami.
 */
public abstract class DataSource implements IFeedObserver, AutoCloseable {
    /**
     * Kazdy datasource powinien zwracac dataset.
     */
    public abstract List<Dataset> datasets();

    /**
     * Aktualizujemy capacity.
     * @param value
     */
    public abstract void updateCapacity(int value);

    /**
     * To nie jest taki step generalnie, to jest bardziej cumulative period. W sekundach.
     * @param value
     */
    public abstract void updateStep(int value);
}
