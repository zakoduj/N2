package Bitfinex;

import Algorithm.FixedSizeList;

import java.util.Arrays;

class TradeBook extends FixedSizeList<Trade> {
    TradeBook(int capacity) {
        super(capacity);
    }

    void update(Part[] parts) {
        if (Arrays.stream(parts).anyMatch(part -> part.isComplex())) {
            // Ktorys z nich jest complex - tzn ze mamy paczke
            Arrays.stream(parts).filter(p -> p.isComplex()).forEach(part -> {
                part.get(objects -> {
                    Double price = objects[2].is(Double.class) ? objects[2].get(Double.class) : objects[2].is(Integer.class) ? objects[2].get(Integer.class).doubleValue() : null;
                    if (price == null) {
                        return;
                    }
                    Double size = objects[3].is(Double.class) ? objects[3].get(Double.class) : objects[3].is(Integer.class) ? objects[3].get(Integer.class).doubleValue() : null;
                    if (size == null) {
                        return;
                    }
                    this.add(new Trade(price, size));
                });
            });
        } else {
            // Mamy pojedynczy trade, ale trzeba jeszcze sprawdzic male conieco
            if (parts.length > 2) {
                Double price = parts[4].is(Double.class) ? parts[4].get(Double.class) : parts[4].is(Integer.class) ? parts[4].get(Integer.class).doubleValue() : null;
                if (price == null) {
                    return;
                }
                Double size = parts[4].is(Double.class) ? parts[4].get(Double.class) : parts[4].is(Integer.class) ? parts[4].get(Integer.class).doubleValue() : null;
                if (size == null) {
                    return;
                }
                this.add(new Trade(price, size));
            }
        }
    }
}
