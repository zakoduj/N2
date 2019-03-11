package Bitmex;

import com.fasterxml.jackson.annotation.JsonProperty;

class Table {
    private String table;
    private String action;

    @JsonProperty("table")
    public String getTable() { return table; }
    @JsonProperty("table")
    public void setTable(String value) { this.table = value; }

    @JsonProperty("action")
    public String getAction() { return action; }
    @JsonProperty("action")
    public void setAction(String value) { this.action = value; }
}

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
}

class TableOrders extends Table {
    private Order[] data;

    @JsonProperty("data")
    public Order[] getData() { return data; }
    @JsonProperty("data")
    public void setData(Order[] value) { this.data = value; }
}