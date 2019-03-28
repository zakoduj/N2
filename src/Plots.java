import Algorithm.DataSource;
import org.jfree.chart.plot.Plot;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;

/**
 * Trzeba pamietac, ze kolekcja plotow jest per data source. Tu robimy asocjacje.
 */
class Plots {
    private final Map<DataSource, List<Plot>> dataSourceListMap = new HashMap<>();

    <T extends Plot> T find(DataSource dataSource, Class<T> type, Consumer<T> created) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // First we have to grab plots collection for current datasource
        List<Plot> plots = this.dataSourceListMap.computeIfAbsent(dataSource, k -> new ArrayList<>());
        for (Plot plot : plots) {
            if (type.isInstance(plot)) {
                return type.cast(plot);
            }
        }
        T plot = create(type, created);
        plots.add(plot);
        return plot;
    }

    void clear() {
        this.dataSourceListMap.clear();
    }

    void iterateFor(DataSource dataSource, Consumer<? super Plot> action) {
        List<Plot> plots = this.dataSourceListMap.computeIfAbsent(dataSource, k -> new ArrayList<>());
        plots.forEach(action);
    }

    private <T extends Plot> T create(Class<T> aClass, Consumer<T> created) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        T plot = aClass.getDeclaredConstructor().newInstance();
        created.accept(plot);
        return plot;
    }
}
