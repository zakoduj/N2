package Bitfinex;

class Trade {
    final double size;
    final double price;

    enum  Side {
        Buy,
        Sell
    }

    final Side side;

    Trade(double price, double size) {
        this.price = price;
        this.size = Math.abs(size);

        if (Double.doubleToLongBits(size) < 0) {
            this.side = Side.Sell;
        } else {
            this.side = Side.Buy;
        }
    }
}
