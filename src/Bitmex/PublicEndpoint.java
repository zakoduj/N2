package Bitmex;

public enum PublicEndpoint {
    announcement("announcement"),        // Site announcements
    chat("chat"),                       // Trollbox chat
    connected("connected"),           // Statistics of connected users/bots
    funding("funding"),                 // Updates of swap funding rates. Sent every funding interval (usually 8hrs)
    instrument("instrument"),          // Instrument updates including turnover and bid/ask
    insurance("insurance"),           // Daily Insurance Fund updates
    liquidation("liquidation"),         // Liquidation orders as they're entered into the book
    orderBookL2_25("orderBookL2_25"),      // Top 25 levels of level 2 order book
    orderBookL2("orderBookL2"),         // Full level 2 order book
    orderBook10("orderBook10"),         // Top 10 levels using traditional full book push
    publicNotifications("publicNotifications"), // System-wide notifications (used for short-lived messages)
    quote("quote"),                     // Top level of the book
    quoteBin1m("quoteBin1m"),          // 1-minute quote bins
    quoteBin5m("quoteBin5m"),          // 5-minute quote bins
    quoteBin1h("quoteBin1h"),          // 1-hour quote bins
    quoteBin1d("quoteBin1d"),          // 1-day quote bins
    settlement("settlement"),          // Settlements
    trade("trade"),                     // Live trades
    tradeBin1m("tradeBin1m"),          // 1-minute trade bins
    tradeBin5m("tradeBin5m"),          // 5-minute trade bins
    tradeBin1h("tradeBin1h"),          // 1-hour trade bins
    tradeBin1d("tradeBin1d");          // 1-day trade bins

    private String value;

    PublicEndpoint(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
