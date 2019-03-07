package Bitmex;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Trade {
    private String timestamp;
    private String symbol;
    private String side;
    private long size;
    private double price;
    private String tickDirection;
    private String trdMatchID;
    private long grossValue;
    private double homeNotional;
    private long foreignNotional;

    @JsonProperty("timestamp")
    public String getTimestamp() { return timestamp; }
    @JsonProperty("timestamp")
    public void setTimestamp(String value) { this.timestamp = value; }

    @JsonProperty("symbol")
    public String getSymbol() { return symbol; }
    @JsonProperty("symbol")
    public void setSymbol(String value) { this.symbol = value; }

    @JsonProperty("side")
    public String getSide() { return side; }
    @JsonProperty("side")
    public void setSide(String value) { this.side = value; }

    @JsonProperty("size")
    public long getSize() { return size; }
    @JsonProperty("size")
    public void setSize(long value) { this.size = value; }

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
    public long getGrossValue() { return grossValue; }
    @JsonProperty("grossValue")
    public void setGrossValue(long value) { this.grossValue = value; }

    @JsonProperty("homeNotional")
    public double getHomeNotional() { return homeNotional; }
    @JsonProperty("homeNotional")
    public void setHomeNotional(double value) { this.homeNotional = value; }

    @JsonProperty("foreignNotional")
    public long getForeignNotional() { return foreignNotional; }
    @JsonProperty("foreignNotional")
    public void setForeignNotional(long value) { this.foreignNotional = value; }
}
