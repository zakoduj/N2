package Algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class FixedSizeList<T> {
    public final int capacity;
    public final List<T> collection = new ArrayList<>();

    public FixedSizeList(int capacity) {
        this.capacity = capacity;
    }

    public void add(T value) {
        while (this.collection.size() >= this.capacity) {
            this.collection.remove(0);
        }
        this.collection.add(value);
    }

    public <R> R add(T value, Function<Boolean, R> completion) {
        while (this.collection.size() >= this.capacity) {
            this.collection.remove(0);
        }
        this.collection.add(value);
        return completion.apply(this.collection.size() == this.capacity);
    }

    public List<T> asList() {
        return Collections.unmodifiableList(this.collection);
    }

    public void clear() {
        this.collection.clear();
    }

    public T get(int index) {
        return this.collection.get(index);
    }

    public T first() {
        return this.collection.get(0);
    }

    public T last() {
        return this.collection.get(this.collection.size() - 1);
    }

    public int size() {
        return this.collection.size();
    }

    public List<T> select(Predicate<T> filter) {
        List<T> result = new ArrayList<>();
        this.collection.forEach(o -> {
            if (filter.test(o)) result.add(o);
        });
        return result;
    }
}
