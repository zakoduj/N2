package Bitmex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class OrderBook extends ArrayList<Order> {
    public void updateAll(Collection<Order> c) {
        c.forEach(order -> {
            int index = this.indexOf(order);
            if (index >= 0) {
                this.set(index, order);
            }
        });
    }

    public List<Order> select(Predicate<Order> filter) {
        List<Order> result = new ArrayList<>();
        this.forEach(order -> {
            if (filter.test(order))
                result.add(order);
        });
        return result;
    }
}
