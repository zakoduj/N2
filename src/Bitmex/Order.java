package Bitmex;

import com.fasterxml.jackson.annotation.JsonProperty;

class Order {
    private String symbol;
    private long id;
    private String side;
    private long size;
    private long price;

    @JsonProperty("symbol")
    public String getSymbol() { return symbol; }
    @JsonProperty("symbol")
    public void setSymbol(String value) { this.symbol = value; }

    @JsonProperty("id")
    public long getID() { return id; }
    @JsonProperty("id")
    public void setID(long value) { this.id = value; }

    @JsonProperty("side")
    public String getSide() { return side; }
    @JsonProperty("side")
    public void setSide(String value) { this.side = value; }

    @JsonProperty("size")
    public long getSize() { return size; }
    @JsonProperty("size")
    public void setSize(long value) { this.size = value; }

    @JsonProperty("price")
    public long getPrice() { return price; }
    @JsonProperty("price")
    public void setPrice(long value) { this.price = value; }

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
