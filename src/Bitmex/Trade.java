package Bitmex;

import com.fasterxml.jackson.annotation.JsonProperty;

class Trade {
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
    String getTimestamp() { return timestamp; }
    @JsonProperty("timestamp")
    void setTimestamp(String value) { this.timestamp = value; }

    @JsonProperty("symbol")
    Symbol getSymbol() { return symbol; }
    @JsonProperty("symbol")
    void setSymbol(Symbol value) { this.symbol = value; }

    @JsonProperty("side")
    Side getSide() { return side; }
    @JsonProperty("side")
    void setSide(Side value) { this.side = value; }

    @JsonProperty("size")
    double getSize() { return size; }
    @JsonProperty("size")
    void setSize(double value) { this.size = value; }

    @JsonProperty("price")
    double getPrice() { return price; }
    @JsonProperty("price")
    void setPrice(double value) { this.price = value; }

    @JsonProperty("tickDirection")
    String getTickDirection() { return tickDirection; }
    @JsonProperty("tickDirection")
    void setTickDirection(String value) { this.tickDirection = value; }

    @JsonProperty("trdMatchID")
    String getTrdMatchID() { return trdMatchID; }
    @JsonProperty("trdMatchID")
    void setTrdMatchID(String value) { this.trdMatchID = value; }

    @JsonProperty("grossValue")
    double getGrossValue() { return grossValue; }
    @JsonProperty("grossValue")
    void setGrossValue(double value) { this.grossValue = value; }

    @JsonProperty("homeNotional")
    double getHomeNotional() { return homeNotional; }
    @JsonProperty("homeNotional")
    void setHomeNotional(double value) { this.homeNotional = value; }

    @JsonProperty("foreignNotional")
    double getForeignNotional() { return foreignNotional; }
    @JsonProperty("foreignNotional")
    void setForeignNotional(double value) { this.foreignNotional = value; }
}
