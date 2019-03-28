package Algorithm;

import java.util.List;

/**
 * Snapshot obecnego stanu rynku
 */
public class Market {
    /**
     * Deskryptor poszczegolnego typu tradów.
     */
    class Trades {
        final int count;
        final double volume;

        Trades(int count, double volume) {
            this.count = count;
            this.volume = volume;
        }
    }

    /**
     * Dane dot tradow kupna.
     */
    final Trades buys;

    /**
     * Dane dot tradow sprzedazy.
     */
    final Trades sells;

    /**
     * Splaszczony book. Przynajmniej na chwile obecna.
     */
    class Book {
        final double price;
        final double size;

        Book(double price, double size) {
            this.price = price;
            this.size = size;
        }
    }

    /**
     * Ksiega askow.
     */
    final Book asks;

    /**
     * Ksiega bidow.
     */
    final Book bids;

    private Market() {
        this.asks = null;
        this.bids = null;
        this.buys = null;
        this.sells = null;
    }

    public Market(Trades buys, Trades sells) {
        this(buys, sells, null, null);
    }

    public Market(Book asks, Book bids) {
        this(null, null, asks, bids);
    }

    public Market(Trades buys, Trades sells, Book asks, Book bids) {
        this.buys = buys;
        this.sells = sells;
        this.asks = asks;
        this.bids = bids;
    }

    /**
     * @return obecna cena na gieldzie.
     */
    final Double price() {
        if (this.asks != null && this.bids != null) {
            return (this.asks.price + this.bids.price) / 2;
        }
        return null;
    }

    public static Trades createTrades(int count, double volume) {
        return new Market().new Trades(count, volume);
    }

    public static Book createBook(double price, double size) {
        return new Market().new Book(price, size);
    }

    public static Trades cumulateTrades(List<Trades> trades) {
        if (trades == null) {
            return null;
        }
        return new Market().new Trades(trades.stream().mapToInt(x -> x.count).sum(), trades.stream().mapToDouble(x -> x.volume).sum());
    }

    public static Book cumulateBook(List<Book> books) {
        if (books == null) {
            return null;
        }
        return new Market().new Book(books.stream().mapToDouble(x -> x.price).average().orElse(0), books.stream().mapToDouble(x -> x.size).average().orElse(0));
    }
}
