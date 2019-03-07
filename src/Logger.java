import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void log(String text) {
        StringBuilder builder = new StringBuilder();
        builder.append(formatter.format(LocalDateTime.now()));
        builder.append(":[INFO]: ");
        builder.append(text);

        System.out.println(builder);
    }

    public void log(Exception e) {
        StringBuilder builder = new StringBuilder();
        builder.append(formatter.format(LocalDateTime.now()));
        builder.append(":[ERROR]: ");
        builder.append(e.getMessage());

        System.out.println(builder);
    }
}
