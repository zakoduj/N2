package Bitfinex;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Command {
    private Event event;
    private Channel channel;
    private Symbol symbol;

    @JsonProperty("event")
    public Event getEvent() { return event; }
    @JsonProperty("event")
    public void setEvent(Event value) { this.event = value; }

    @JsonProperty("channel")
    public Channel getChannel() { return channel; }
    @JsonProperty("channel")
    public void setChannel(Channel value) { this.channel = value; }

    @JsonProperty("symbol")
    public Symbol getSymbol() { return symbol; }
    @JsonProperty("symbol")
    public void setSymbol(Symbol value) { this.symbol = value; }

    public static Command subscription(Channel channel, Symbol symbol) {
        Command command = new Command();
        command.setEvent(Event.subscribe);
        command.setChannel(channel);
        command.setSymbol(symbol);
        return command;
    }
}
