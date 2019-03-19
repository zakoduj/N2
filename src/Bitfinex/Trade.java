package Bitfinex;

public class Trade {
    private Side side;
    private double size;
    private double price;

    Trade(Side side, double size, double price) {
        this.side = side;
        this.size = size;
        this.price = price;
    }
}
