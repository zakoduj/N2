package Bitfinex;

import Algorithm.Feed;
import Algorithm.ILogger;
import Algorithm.JsonReader;
import Algorithm.JsonWriter;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
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

                // We need as fucking channel here. Channel number is fuckin' always 1st part.
                Channel channel = this.channelMap.get((parts[0].get(Integer.class)));
                switch (channel) {
                    case trades: {
                        // First shit, check if the bitch is initial
                        if (Arrays.stream(parts).anyMatch(part -> part.isComplex())) {
                            // Is initial
                            Arrays.stream(parts).filter(p -> p.isComplex()).findFirst().ifPresent(part -> part.get(objects -> {
                                Integer timestamp = objects[1].get(Integer.class);
                                Double price = objects[2].get(Double.class);
                                Double value = objects[3].get(Double.class);
                                this.tradeBook.add(new Trade(Double.doubleToLongBits(value) < 0 ? Side.Sell : Side.Buy, Math.abs(value), price));
                            }));
                        } else {
                            // Is not initial
                            Integer timestamp = parts[3].get(Integer.class);
                            Double price = parts[4].get(Double.class);
                            Double value = parts[5].get(Double.class);
                            this.tradeBook.add(new Trade(Double.doubleToLongBits(value) < 0 ? Side.Sell : Side.Buy, Math.abs(value), price));
                        }
                    }
                    break;
                    case book: {
                        if (Arrays.stream(parts).anyMatch(part -> part.isComplex())) {
                            Arrays.stream(parts).filter(p -> p.isComplex()).findFirst().ifPresent(part -> part.get(objects -> {
                                Double price = objects[0].get(Double.class);
                                Double count = objects[1].get(Double.class);
                                Double amount = objects[2].get(Double.class);
                            }));
                        } else {
                            Double price = parts[1].get(Double.class);
                            Double count = parts[2].get(Double.class);
                            Double amount = parts[3].get(Double.class);
                        }
                    }
                    break;
                }
                int a = 0;
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
