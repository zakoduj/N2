import org.jfree.chart.plot.Plot;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

class Plots implements Iterable<Plot> {
    private final List<Plot> plots = new ArrayList<>();

    <T extends Plot> T find(Class<T> type) {
        for (Plot plot : this.plots) {
            if (type.isInstance(plot)) {
                return type.cast(plot);
            }
        }
        return null;
    }

    <T extends Plot> T find(Class<T> type, Consumer<T> created) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for (Plot plot : this.plots) {
            if (type.isInstance(plot)) {
                return type.cast(plot);
            }
        }
        T plot = create(type, created);
        this.plots.add(plot);
        return plot;
    }

    void clear() {
        this.plots.clear();
    }

    private <T extends Plot> T create(Class<T> aClass, Consumer<T> created) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        T plot = aClass.getDeclaredConstructor().newInstance();
        created.accept(plot);
        return plot;
    }

    @Override
    public Iterator<Plot> iterator() {
        return this.plots.iterator();
    }

    @Override
    public void forEach(Consumer<? super Plot> action) {
        this.plots.forEach(action);
    }

    @Override
    public Spliterator<Plot> spliterator() {
        return this.plots.spliterator();
    }
}
