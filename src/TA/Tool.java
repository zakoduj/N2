package TA;

import java.util.*;
import java.util.function.Consumer;

public abstract class Tool {
    public class Result {
        final double value;

        Result(double value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null)
                return false;
            if (o == this)
                return true;
            if (!(o instanceof Result))
                return false;
            Result r = (Result) o;
            return r.value == this.value;
        }
    }

    public class ComplexResult implements Iterable<Result> {
        private final List<Result> results = new ArrayList<>();

        ComplexResult(Result... results) {
            this.results.addAll(Arrays.asList(results));
        }

        <T extends Result> T get(Class<T> type) {
            for (Result result : this.results) {
                if (type.isInstance(result)) {
                    return type.cast(result);
                }
            }
            return null;
        }

        @Override
        public Iterator<Result> iterator() {
            return this.results.iterator();
        }

        @Override
        public void forEach(Consumer<? super Result> action) {
            this.results.forEach(action);
        }

        @Override
        public Spliterator<Result> spliterator() {
            return this.results.spliterator();
        }
    }

    /**
     * Ja mysle, ze mozna by tu zrobic taki patent. Ze mozna zwrocic null - oznaczalo by to ze chuj kurwa, nic sie nie dalo zrobic.
     * A moze pusty?
     * @param value
     * @return
     */
    abstract ComplexResult next(double value);
    abstract void reset();
}
