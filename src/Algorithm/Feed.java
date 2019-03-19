package Algorithm;

import org.java_websocket.client.WebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * feed to jest obiekt ktory dostarcza nam danych. Taki obiekt jest zamykalny. Feed wypluwa info w real time.
 * Powienien zatem miec intefejs do pushowania informacji, ktore nas interesuja.
 */
public abstract class Feed<T> extends WebSocketClient {
    private final Class<T> type;
    private final List<IFeedObserver> observerList = new ArrayList<>();

    protected Feed(Class<T> type, String uri) throws URISyntaxException {
        super(new URI(uri));
        this.type = type;
    }

    @Override
    public String toString() {
        // Powinien zwrocic nazwe dla tego feeda
        return this.type.getSimpleName();
    }

    void addObserver(IFeedObserver observer) {
        this.observerList.add(observer);
        if (!this.observerList.isEmpty()) {
            this.connect();
        }
    }

    void removeObserver(IFeedObserver observer) {
        this.observerList.remove(observer);
        if (this.observerList.isEmpty()) {
            this.close();
        }
    }
}
