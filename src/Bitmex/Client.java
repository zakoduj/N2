package Bitmex;

import Algorithm.JsonConverter;
import Algorithm.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Client extends WebSocketClient {
    private final Logger logger;
    private final List<Class<?>> types = new ArrayList<>();

    // Trzymamy w naszej tablicy domyslnie 50 ostatnich tradów.
    private final Trades trades = new Trades(50);

    public Client(Logger logger) throws URISyntaxException {
        super(new URI("wss://www.bitmex.com/realtime"));
        this.logger = logger;

        this.types.add(Info.class);
        this.types.add(TradeWrapper.class);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        this.logger.log("Opened connection");

        Subscription subscription = new Subscription(PublicEndpoint.orderBookL2_25 + ":" + Symbol.XBTUSD);
        try {
            JsonConverter jsonConverter = new JsonConverter(Command.class);
            this.send(jsonConverter.toJsonString(subscription));
        } catch (Exception e) {
            logger.log(e);
        }
    }

    @Override
    public void onMessage(String s) {
        this.logger.log("Received message: " + s);

        for (Class<?> type : this.types) {
            try {
                JsonConverter jsonConverter = new JsonConverter(type);
                Object object = jsonConverter.fromJsonString(s);
                if (object instanceof Info) {
                    // Mamy jakies info - nie wiem co z tym robi - gdzies mozna wyswietlic.
                    Info info = ((Info) object);
                } else if (object instanceof TradeWrapper) {
                    // Mamy trade - trzeba go wsadzic do tablicy tradów
                    TradeWrapper tradeWrapper = ((TradeWrapper) object);
                    for (Trade trade : tradeWrapper.getData()) {
                        this.trades.add(trade);
                    }
                }
            } catch (Exception e) {
                this.logger.log(e);
            }
        }

        System.out.println("Current trades count: " + this.trades.size());
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
