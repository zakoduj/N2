package Algorithm;

public class MarketSnapshot {
    final double longs;
    final double shorts;
    final double buys;
    final double sells;
    final double price;

    public MarketSnapshot(double longs, double shorts, double buys, double sells, double price) {
        this.longs = longs;
        this.shorts = shorts;
        this.buys = buys;
        this.sells = sells;
        this.price = price;
    }
}
