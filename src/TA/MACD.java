package TA;

public class MACD extends Tool {
    private final EMA shortEma, longEma, signalEma;

    public class Macd extends Result {
        Macd(double value) {
            super(value);
        }
    }

    public class Signal extends Result {
        Signal(double value) {
            super(value);
        }
    }

    public class Histogram extends Result {
        Histogram(double value) {
            super(value);
        }
    }

    public MACD(int shortPeriod, int longPeriod, int signalPeriod) {
        this.shortEma = new EMA(shortPeriod);
        this.longEma = new EMA(longPeriod);
        this.signalEma = new EMA(signalPeriod);
    }

    @Override
    public ComplexResult next(double value) {
        ComplexResult r1 = this.shortEma.next(value);
        ComplexResult r2 = this.longEma.next(value);

        double macd = r1.get(Result.class).value - r2.get(Result.class).value;
        ComplexResult r3 = this.signalEma.next(macd);

        double signal = r3.get(Result.class).value;
        double histogram = macd - signal;

        return new ComplexResult(
                new Macd(macd), new Signal(signal), new Histogram(histogram)
        );
    }

    @Override
    public void reset() {
        this.shortEma.reset();
        this.longEma.reset();
        this.signalEma.reset();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":" + this.shortEma.periods() + ":" + this.longEma.periods() + ":" + this.signalEma.periods();
    }
}
