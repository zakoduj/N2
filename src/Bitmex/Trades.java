package Bitmex;

import Algorithm.FixedSizeList;

/**
 * Klasa tradów, w której trzymamy poprostu tablice tradów. Trzeba tym umiejetnie zarzadzac.
 */
public class Trades extends FixedSizeList<Trade> {
    public Trades(int capacity) {
        super(capacity);
    }
}
