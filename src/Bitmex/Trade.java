package Bitmex;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Trade {
    private String timestamp;
    private Symbol symbol;
    private Side side;
    private double size;
    private double price;
    private String tickDirection;
    private String trdMatchID;
    private double grossValue;
    private double homeNotional;
    private double foreignNotional;

    @JsonProperty("timestamp")
    public String getTimestamp() { return timestamp; }
    @JsonProperty("timestamp")
    public void setTimestamp(String value) { this.timestamp = value; }

    @JsonProperty("symbol")
    public Symbol getSymbol() { return symbol; }
    @JsonProperty("symbol")
    public void setSymbol(Symbol value) { this.symbol = value; }

    @JsonProperty("side")
    public Side getSide() { return side; }
    @JsonProperty("side")
    public void setSide(Side value) { this.side = value; }

    @JsonProperty("size")
    public double getSize() { return size; }
    @JsonProperty("size")
    public void setSize(double value) { this.size = value; }

    @JsonProperty("price")
    public double getPrice() { return price; }
    @JsonProperty("price")
    public void setPrice(double value) { this.price = value; }

    @JsonProperty("tickDirection")
    public String getTickDirection() { return tickDirection; }
    @JsonProperty("tickDirection")
    public void setTickDirection(String value) { this.tickDirection = value; }

    @JsonProperty("trdMatchID")
    public String getTrdMatchID() { return trdMatchID; }
    @JsonProperty("trdMatchID")
    public void setTrdMatchID(String value) { this.trdMatchID = value; }

    @JsonProperty("grossValue")
    public double getGrossValue() { return grossValue; }
    @JsonProperty("grossValue")
    public void setGrossValue(double value) { this.grossValue = value; }

    @JsonProperty("homeNotional")
    public double getHomeNotional() { return homeNotional; }
    @JsonProperty("homeNotional")
    public void setHomeNotional(double value) { this.homeNotional = value; }

    @JsonProperty("foreignNotional")
    public double getForeignNotional() { return foreignNotional; }
    @JsonProperty("foreignNotional")
    public void setForeignNotional(double value) { this.foreignNotional = value; }
}
