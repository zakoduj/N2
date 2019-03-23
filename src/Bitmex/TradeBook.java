package Bitmex;

import Algorithm.FixedSizeList;

class TradeBook extends FixedSizeList<Trade> {
    TradeBook(int capacity) {
        super(capacity);
    }
}
