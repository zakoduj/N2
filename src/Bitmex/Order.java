package Bitmex;

import com.fasterxml.jackson.annotation.JsonProperty;

class Order {
    private Symbol symbol;
    private long id;
    private Side side;
    private double size;
    private double price;

    @JsonProperty("symbol")
    Symbol getSymbol() { return symbol; }
    @JsonProperty("symbol")
    void setSymbol(Symbol value) { this.symbol = value; }

    @JsonProperty("id")
    long getID() { return id; }
    @JsonProperty("id")
    void setID(long value) { this.id = value; }

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

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (other == this)
            return true;
        if (!(other instanceof Order))
            return false;
        Order order = (Order)other;
        return this.getID() == order.getID() && this.symbol.equals(order.getSymbol()) && this.side.equals(order.getSide());
    }
}
