package Algorithm;

import java.util.ArrayList;

public class DataSources extends ArrayList<DataSource> {
    public <T extends DataSource> T find(Class<T> type) {
        for (DataSource entry : this) {
            if (type.isInstance(entry)) {
                return type.cast(entry);
            }
        }
        return null;
    }
}
