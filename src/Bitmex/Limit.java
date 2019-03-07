package Bitmex;

import com.fasterxml.jackson.annotation.JsonProperty;

class Limit {
    private long remaining;

    @JsonProperty("remaining")
    public long getRemaining() { return remaining; }
    @JsonProperty("remaining")
    public void setRemaining(long value) { this.remaining = value; }
}
