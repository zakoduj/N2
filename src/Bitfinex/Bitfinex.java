package Bitfinex;

import Algorithm.*;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bitfinex extends Feed<Bitfinex> {
    private final ILogger logger;

    private final TradeBook tradeBook = new TradeBook(50);
    private final OrderBook orderBook = new OrderBook();
    private final Map<Integer, Channel> channelMap = new HashMap<Integer, Channel>();

    public Bitfinex(ILogger logger) throws URISyntaxException {
        super(Bitfinex.class, "wss://api.bitfinex.com/ws/");
        this.logger = logger;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        this.logger.log("Opened connection");
        try {
            JsonWriter jsonWriter = new JsonWriter(Command.class);
            this.send(jsonWriter.write(Command.subscription(Channel.trades, Symbol.tBTCUSD)));
            this.send(jsonWriter.write(Command.subscription(Channel.book, Symbol.tBTCUSD)));
        } catch (Exception e) {
            this.logger.log(e);
        }
    }

    @Override
    public void onMessage(String s) {
        this.logger.log("Received message: " + s);

        try {
            JsonReader jsonReader = new JsonReader();
            JsonReader.Result result = jsonReader.read(s, new HashMap<String, Class<?>>() {{
                put("event", Event.class);
                put("channel", Channel.class);
                put("chanId", Integer.class);
            }});
            if (!result.isEmpty()) {
                Event event = result.get(Event.class);
                if (event == Event.subscribed) {
                    // Interesuje nas generalnie tylko ten event
                    Channel channel = result.get(Channel.class);
                    if (channel != null) {
                        // Get channel ID
                        Integer channelID = result.get(Integer.class);
                        this.channelMap.put(channelID, channel);
                    }
                }
            } else {
                // A tu sprawdzamy czy w naszej opowiedzi jest nr kanału.
                jsonReader = new JsonReader(Part[].class);
                Part[] parts = jsonReader.read(s);

                // Market snapshot
                Market market = null;

                // We need as fucking channel here. Channel number is fuckin' always 1st part.
                Channel channel = this.channelMap.get((parts[0].get(Integer.class)));
                if (channel == Channel.trades) {
                    // Aktualizujemy trady
                    this.tradeBook.update(parts);

                    // Nastepnie, snapshot jest tworzony na podstawie tradow
                    List<Trade> buys = this.tradeBook.select(t -> t.side == Trade.Side.Buy);
                    List<Trade> sells = this.tradeBook.select(t -> t.side == Trade.Side.Sell);
                    market = new Market(
                            Market.createTrades(buys.size(), buys.stream().mapToDouble(d -> d.size).sum()),
                            Market.createTrades(sells.size(), sells.stream().mapToDouble(d -> d.size).sum())
                    );
                } else if (channel == Channel.book) {
                    // Aktualizujemy orderbook
                    this.orderBook.update(parts);

                    // A w tym przypadku - na podstawie orderbook
                    market = new Market(
                            Market.createBook(this.orderBook.asks.get(0).price, this.orderBook.asks.get(0).size),
                            Market.createBook(this.orderBook.bids.get(0).price, this.orderBook.bids.get(0).size)
                    );
                }

                // Pushujemy dane do obserwatorów
                super.push(market);
            }
        } catch (Exception e) {
            this.logger.log(e);
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        this.logger.log("Connection closed by " + ( b ? "remote peer" : "us" ) + " Code: " + i + " Reason: " + s);
    }

    @Override
    public void onError(Exception e) {
        this.logger.log(e);
    }
}
