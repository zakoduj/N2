package Bitmex;

import Algorithm.Feed;
import Algorithm.JsonReader;
import Algorithm.JsonWriter;
import Algorithm.Logger;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;

public class Bitmex extends Feed<Bitmex> {
    private final Logger logger;

    private final TradeBook tradeBook = new TradeBook(50);
    private final OrderBook orderBook = new OrderBook();

    public Bitmex(Logger logger) throws URISyntaxException {
        super(Bitmex.class, "wss://www.bitmex.com/realtime");
        this.logger = logger;
    }

    /**
     * Jak jest opened - to trzeba odpalic mu sekwencje send z subami. Czyli trzeba sporzadzic liste subów, ktore nas interesuja, i dl kazdego elementu tej listy,
     * odpalic suba.
     * @param serverHandshake
     */
    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        this.logger.log("Opened connection");
        try {
            // Subujemy 2 kanaly
            JsonWriter jsonWriter = new JsonWriter(Command.class);
            this.send(jsonWriter.write(Command.subscription(Endpoint.orderBookL2_25 + ":" + Symbol.XBTUSD, Endpoint.trade + ":" + Symbol.XBTUSD)));
        } catch (Exception e) {
            logger.log(e);
        }
    }

    /**
     * Jak sie wszystko przeprocesuje - to wtedy powinien ciachnac pusha do wszystkich zainteresowanych. Ale ten push, jest tez w tylu edycjach co jest
     * zasubskrybowanych kanałów.
     * @param s
     */
    @Override
    public void onMessage(String s) {
        this.logger.log("Received message: " + s);

        // Partial read from json
        JsonReader jsonReader = new JsonReader();
        JsonReader.Result result = jsonReader.read(s, new HashMap<String, Class<?>>() {{
            put("table", Endpoint.class);
            put("action", Action.class);
        }});
        if (!result.isEmpty()) {
            // Mamy czesciowo sparsowany obiekt - tzn wiemy czego mozemy oczekiwac
            Endpoint endpoint = result.get(Endpoint.class);
            Action action = result.get(Action.class);
            if (endpoint != null) {
                switch (endpoint) {
                    case orderBookL2_25: {
                        // Tutaj bierzemy sobie tablice ofert
                        result = jsonReader.read(s, new HashMap<String, Class<?>>(){{
                            put("data", Order[].class);
                        }});
                        if (!result.isEmpty()) {
                            // Mamy jakies ordery - i tu na podstawie akcji wykonujemy jakas tam czynnosc.
                            Order[] orders = result.get(Order[].class);
                            if (orders != null && action != null) {
                                // Mamy wszystko, czego potrzebujemy aby wykonac jakas manipulacja na orderbook
                                switch (action) {
                                    case partial:
                                    case insert:
                                        this.orderBook.addAll(Arrays.asList(orders));
                                        break;
                                    case update:
                                        this.orderBook.updateAll(Arrays.asList(orders));
                                        break;
                                    case delete:
                                        this.orderBook.removeAll(Arrays.asList(orders));
                                        break;
                                }
                            }
                        }
                    }
                    break;
                    case trade: {
                        // Tutaj bierzemy sobie tablice orderów
                        result = jsonReader.read(s, new HashMap<String, Class<?>>(){{
                            put("data", Trade[].class);
                        }});
                        if (!result.isEmpty()) {
                            // Mamy jakies ordery - i tu na podstawie akcji wykonujemy jakas tam czynnosc.
                            Trade[] trades = result.get(Trade[].class);
                            if (trades != null && action != null) {
                                // Mamy wszystko aby podzialac na tradebooku
                                switch (action) {
                                    case insert: {
                                        // Jak narazie to obslugujemy tylko jedno zdarzenie - w sumie wiecej zdarzen tu i nie bedzie.
                                        for (Trade trade : trades) {
                                            this.tradeBook.add(trade);
                                        }
                                    }
                                    break;
                                    default:
                                    break;
                                }
                            }
                        }
                    }
                    break;
                    default:
                        break;
                }
            }
        }

        System.out.println("Current trades count: " + this.tradeBook.size());
        System.out.println("Current offers count: " + this.orderBook.size());
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
