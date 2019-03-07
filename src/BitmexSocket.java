import com.fasterxml.jackson.annotation.JsonProperty;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

class Command {
    private String op;
    private String[] args;

    @JsonProperty("op")
    public String getOp() { return op; }
    @JsonProperty("op")
    public void setOp(String value) { this.op = value; }

    @JsonProperty("args")
    public String[] getArgs() { return args; }
    @JsonProperty("args")
    public void setArgs(String[] value) { this.args = value; }
}

class Subscription extends Command {
    Subscription(String... args) {
        this.setOp("subscribe");
        this.setArgs(args);
    }
}

/**
 * Klasa tradów, w której trzymamy poprostu tablice tradów. Trzeba tym umiejetnie zarzadzac.
 */
class Trades {

}

public class BitmexSocket extends WebSocketClient {
    private final Logger logger;
    private final List<Class<?>> types = new ArrayList<>();
    private final JsonConverter jsonConverter = new JsonConverter();

    public BitmexSocket(Logger logger) throws URISyntaxException {
        super(new URI("wss://www.bitmex.com/realtime"));
        this.logger = logger;

        this.types.add(Info.class);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        this.logger.log("Opened connection");

        Subscription subscription = new Subscription("trade:XBTUSD");
        try {
            this.send(this.jsonConverter.toJsonString(subscription, Command.class));
        } catch (Exception e) {
            logger.log(e);
        }
    }

    @Override
    public void onMessage(String s) {
        this.logger.log("Received message: " + s);

        for (Class<?> type : this.types) {
            try {
                Object object = this.jsonConverter.fromJsonString(s, type);
                if (object instanceof Info) {
                    Info info = ((Info) object);
                }
            } catch (Exception e) {
                this.logger.log(e);
            }
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
