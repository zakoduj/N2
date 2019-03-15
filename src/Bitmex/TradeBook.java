package Bitmex;

import Algorithm.FixedSizeList;

public class TradeBook extends FixedSizeList<Trade> {
    public TradeBook(int capacity) {
        super(capacity);
    }
}
