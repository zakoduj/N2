package Bitmex;

public enum Symbol {
    XBTUSD("XBTUSD");

    private String value;

    Symbol(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
