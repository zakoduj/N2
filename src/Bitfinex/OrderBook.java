package Bitfinex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

class OrderBook {

    class Level implements Comparable<Level> {
        final Double price;
        Double size;

        private Level(double price, double size) {
            this.price = price;
            this.size = size;
        }

        double getPrice() {
            return this.price;
        }

        @Override
        public int compareTo(Level o) {
            return this.price.compareTo(o.price);
        }

        @Override
        public boolean equals(Object other) {
            if (other == null) {
                return false;
            }
            if (other == this) {
                return true;
            }
            if (!(other instanceof Level)) {
                return false;
            }
            Level level = (Level)other;
            return level.price.equals(this.price);
        }
    }

    // Przychodzac na gielde:
    // Za tyle kupisz
    final List<Level> asks = new ArrayList<>();
    // Za tyle sprzedasz
    final List<Level> bids = new ArrayList<>();

    void update(Part[] parts) {
        // Przede wszystkim tak, ze jezeli ktorys z nich jest complex - wtedy mamy do czynienia z cala paczka rzeczy
        if (Arrays.stream(parts).anyMatch(part -> part.isComplex())) {
            // Ok, zatem mamy cala paczke.
            Arrays.stream(parts).filter(part -> part.isComplex()).forEach(part -> {
                part.get(objects -> {
                    // Pobieramy cene
                    Double price = objects[0].is(Double.class) ? objects[0].get(Double.class) : objects[0].is(Integer.class) ? objects[0].get(Integer.class).doubleValue() : null;
                    if (price == null) {
                        return;
                    }
                    // Teraz bierzemy size
                    Double size = objects[2].is(Double.class) ? objects[2].get(Double.class) : objects[2].is(Integer.class) ? objects[2].get(Integer.class).doubleValue() : null;
                    if (size == null) {
                        return;
                    }
                    // Tworzymy nowy level.
                    Level level = new Level(price, Math.abs(size));
                    // Sprawdzamy teraz gdzie trzeba dodac
                    if (Double.doubleToLongBits(size) < 0) {
                        // Dodajemy do ASKS
                        int index = this.asks.indexOf(level);
                        if (index >= 0) {
                            // Już jest taki gosciu
                            this.asks.get(index).size = Math.abs(size);
                        } else {
                            this.asks.add(level);
                        }
                    } else {
                        // Dodajemy do BIDS
                        int index = this.bids.indexOf(level);
                        if (index >= 0) {
                            // Już jest taki gosciu
                            this.bids.get(index).size = Math.abs(size);
                        } else {
                            this.bids.add(level);
                        }
                    }
                });
            });
        } else {
            // Nie ma calej paczki, ale jest pojedynczy.
            if (parts.length > 2) {
                // Koniecznie musi zawierac wiecej elementow niz 2 - bo inaczej to nie jest zaden update cenowego poziomu.
                Double price = parts[1].is(Double.class) ? parts[1].get(Double.class) : parts[1].is(Integer.class) ? parts[1].get(Integer.class).doubleValue() : null;
                if (price == null) {
                    return;
                }
                Integer count = parts[2].is(Double.class) ? parts[2].get(Double.class).intValue() : parts[2].is(Integer.class) ? parts[2].get(Integer.class) : null;
                if (count == null) {
                    return;
                }
                Double size = parts[3].is(Double.class) ? parts[3].get(Double.class) : parts[3].is(Integer.class) ? parts[3].get(Integer.class).doubleValue() : null;
                if (size == null) {
                    return;
                }

                // Tworzymy nowy level.
                Level level = new Level(price, Math.abs(size));

                // I teraz lecimy
                if (count > 0) {
                    // when count > 0 then you have to add or update the price level
                    // Sprawdzamy teraz gdzie trzeba dodac
                    if (Double.doubleToLongBits(size) < 0) {
                        // Dodajemy do ASKS
                        int index = this.asks.indexOf(level);
                        if (index >= 0) {
                            // Już jest taki gosciu
                            this.asks.get(index).size = Math.abs(size);
                        } else {
                            this.asks.add(level);
                        }
                    } else {
                        // Dodajemy do BIDS
                        int index = this.bids.indexOf(level);
                        if (index >= 0) {
                            // Już jest taki gosciu
                            this.bids.get(index).size = Math.abs(size);
                        } else {
                            this.bids.add(level);
                        }
                    }
                } else if (count == 0) {
                    // when count = 0 then you have to delete the price level.
                    if (Double.doubleToLongBits(size) < 0) {
                        int index = this.asks.indexOf(level);
                        if (index >= 0) {
                            this.asks.remove(index);
                        }
                    } else {
                        int index = this.bids.indexOf(level);
                        if (index >= 0) {
                            this.bids.remove(index);
                        }
                    }
                }
            }
        }

        // A teraz czas zeby to wszystko posortowac jak trzeba
        this.asks.sort(Comparator.comparing(Level::getPrice));
        this.bids.sort(Comparator.comparing(Level::getPrice).reversed());
    }
}
