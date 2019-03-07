package Bitmex;

public enum PrivateEndpoint {
    affiliate("affiliate"),   // Affiliate status, such as total referred users & payout %
    execution("execution"),   // Individual executions; can be multiple per order
    order("order"),       // Live updates on your orders
    margin("margin"),      // Updates on your current account balance and margin requirements
    position("position"),    // Updates on your positions
    privateNotifications("privateNotifications"), // Individual notifications - currently not used
    transact("transact"),     // Deposit/Withdrawal updates
    wallet("wallet");       // Bitcoin address balance data, including total deposits & withdrawals

    private String value;

    PrivateEndpoint(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
