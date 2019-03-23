package TA;

import java.util.ArrayList;
import java.util.List;

public class EMA extends Tool {
    private final double multiplier;
    private final int periods;
    private final List<Double> doubles = new ArrayList<>();
    private double value;

    public EMA(int periods) {
        this.periods = periods;
        this.multiplier = 2.0 / (periods + 1);
        this.value = 0;
    }

    int periods() {
        return this.periods;
    }

    @Override
    public ComplexResult next(double value) {
        synchronized (this) {
            if (this.value == 0) {
                this.doubles.add(value);
                if (this.doubles.size() < this.periods) {
                    return new ComplexResult(new Result(this.doubles.stream().mapToDouble(a -> a).average().orElse(value)));
                }
                return new ComplexResult(new Result(this.value = this.doubles.stream().mapToDouble(a -> a).average().orElse(value)));
            }
            return new ComplexResult(new Result(this.value = ((value - this.value) * this.multiplier) + this.value));
        }
    }

    @Override
    public void reset() {
        synchronized (this) {
            this.doubles.clear();
            this.value = 0;
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":" + this.periods;
    }
}
