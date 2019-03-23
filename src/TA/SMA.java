package TA;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple moving average - the example of the simplest tool possible
 */
public class SMA extends Tool {
    private final int periods;
    private final List<Double> doubles = new ArrayList<>();

    public SMA(int periods) {
        this.periods = periods;
    }

    int periods() {
        return this.periods;
    }

    @Override
    public ComplexResult next(double value) {
        synchronized (this) {
            while (this.doubles.size() >= this.periods) {
                this.doubles.remove(0);
            }
            this.doubles.add(value);
            return new ComplexResult(
                    new Result(this.doubles.stream().mapToDouble(a -> a).average().orElse(value))
            );
        }
    }

    @Override
    public void reset() {
        synchronized (this) {
            this.doubles.clear();
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":" + this.periods;
    }
}
