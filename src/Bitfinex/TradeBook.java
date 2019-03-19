package Bitfinex;

import Algorithm.FixedSizeList;

public class TradeBook extends FixedSizeList<Trade> {
    public TradeBook(int capacity) {
        super(capacity);
    }
}
