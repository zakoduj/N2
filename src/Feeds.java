import Algorithm.Feed;
import Algorithm.ILogger;
import Bitfinex.Bitfinex;
import Bitmex.Bitmex;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Klaska, ktora trzyma dostepne feedy.
 */
class Feeds implements Iterable<Class<? extends Feed>> {
    private final List<Class<? extends Feed>> feeds = new ArrayList<>();

    Feeds() {
        this.feeds.add(Bitmex.class);
        this.feeds.add(Bitfinex.class);
    }

    @Override
    public Iterator<Class<? extends Feed>> iterator() {
        return this.feeds.iterator();
    }

    @Override
    public void forEach(Consumer<? super Class<? extends Feed>> action) {
        this.feeds.forEach(action);
    }

    @Override
    public Spliterator<Class<? extends Feed>> spliterator() {
        return this.feeds.spliterator();
    }

    <T extends Feed> T create(Class<T> aClass, ILogger logger) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return aClass.getDeclaredConstructor(new Class[]{ILogger.class}).newInstance(logger);
    }
}
