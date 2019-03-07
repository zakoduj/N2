package Bitmex;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TradeWrapper {
    private String table;
    private String action;
    private Trade[] data;

    @JsonProperty("table")
    public String getTable() { return table; }
    @JsonProperty("table")
    public void setTable(String value) { this.table = value; }

    @JsonProperty("action")
    public String getAction() { return action; }
    @JsonProperty("action")
    public void setAction(String value) { this.action = value; }

    @JsonProperty("data")
    public Trade[] getData() { return data; }
    @JsonProperty("data")
    public void setData(Trade[] value) { this.data = value; }
}
