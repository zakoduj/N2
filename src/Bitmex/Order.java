package Bitmex;

import com.fasterxml.jackson.annotation.JsonProperty;

class Order {
    private Symbol symbol;
    private long id;
    private Side side;
    private double size;
    private double price;

    @JsonProperty("symbol")
    public Symbol getSymbol() { return symbol; }
    @JsonProperty("symbol")
    public void setSymbol(Symbol value) { this.symbol = value; }

    @JsonProperty("id")
    public long getID() { return id; }
    @JsonProperty("id")
    public void setID(long value) { this.id = value; }

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

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (other == this)
            return true;
        if (!(other instanceof Order))
            return false;
        Order order = (Order)other;
        return this.getID() == order.getID() &&
                this.symbol.equals(order.getSymbol()) &&
                this.side.equals(order.getSide());
    }
}
