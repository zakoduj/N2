package Bitmex;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Command {
    private String op;
    private String[] args;

    @JsonProperty("op")
    public String getOp() { return op; }
    @JsonProperty("op")
    public void setOp(String value) { this.op = value; }

    @JsonProperty("args")
    public String[] getArgs() { return args; }
    @JsonProperty("args")
    public void setArgs(String[] value) { this.args = value; }

    public static Command subscription(String... args) {
        Command command = new Command();
        command.setOp("subscribe");
        command.setArgs(args);
        return command;
    }
}
