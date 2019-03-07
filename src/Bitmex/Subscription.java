package Bitmex;

public class Subscription extends Command {
    public Subscription(String... args) {
        this.setOp("subscribe");
        this.setArgs(args);
    }
}
