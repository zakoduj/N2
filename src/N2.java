import Algorithm.ImageLoader;
import Algorithm.Logger;
import com.bulenkov.darcula.DarculaLaf;
import org.apache.commons.lang3.SystemUtils;
import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

import javax.swing.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Predicate;

class Standardizer {
    private static final double EPSILON = Math.pow(2.0, -52.0);

    /**
     * Standardizes an array to mean 0 and variance 1.
     */
    public static double[] apply(double... x) {
        double mu = sum(x) / x.length;
        double sigma = Math.sqrt(variance(x));

        if (Math.abs(sigma) < EPSILON) {
            System.out.println("array has variance of 0.");
            return x;
        }

        for (int i = 0; i < x.length; i++) {
            x[i] = (x[i] - mu) / sigma;
        }

        return x;
    }

    private static double sum(double[] values) {
        double sum = 0;
        double c = 0;
        for(double iv : values) {
            double y = iv - c;
            double t = sum + y;
            c = (t - sum) - y;
            sum = t;
        }
        return sum;
    }

    private static double variance(double[] data) {
        double sum = 0.0;
        double sumsq = 0.0;

        for (double xi : data) {
            sum += xi;
            sumsq += xi * xi;
        }

        int n = data.length - 1;
        return sumsq / n - (sum / data.length) * (sum / n);
    }
}

class GRU {
    private final int inSize;
    private final int hiddenSize;
    private final int outSize;

    private final double scale = 0.1;
    private final double rate = 1;

    private DoubleMatrix Wxr;
    private DoubleMatrix Whr;
    private DoubleMatrix br;

    private DoubleMatrix Wxz;
    private DoubleMatrix Whz;
    private DoubleMatrix bz;

    private DoubleMatrix Wxh;
    private DoubleMatrix Whh;
    private DoubleMatrix bh;

    private DoubleMatrix Why;
    private DoubleMatrix by;

    enum Type {
        x_, h_, r_, z_, gh_, py_, y_, dy_, dh_, dgh_, dr_, dz_
    }

    class Link {
        private final Map<Type, DoubleMatrix> points = new HashMap<>();

        void add(Type type, DoubleMatrix matrix) {
            this.points.put(type, matrix);
        }

        DoubleMatrix get(Type type) {
            return this.points.get(type);
        }
    }

    public GRU(int inSize, int hiddenSize, int outSize) {
        this.inSize = inSize;
        this.hiddenSize = hiddenSize;
        this.outSize = outSize;

        this.Wxr = uniform(inSize, hiddenSize);
        this.Whr = uniform(hiddenSize, hiddenSize);
        this.br = new DoubleMatrix(1, hiddenSize);

        this.Wxz = uniform(inSize, hiddenSize);
        this.Whz = uniform(hiddenSize, hiddenSize);
        this.bz = new DoubleMatrix(1, hiddenSize);

        this.Wxh = uniform(inSize, hiddenSize);
        this.Whh = uniform(hiddenSize, hiddenSize);
        this.bh = new DoubleMatrix(1, hiddenSize);

        this.Why = new DoubleMatrix(hiddenSize, outSize);
        this.by = new DoubleMatrix(1, outSize);
    }

    /**
     * Trenuj poki poziom bledu jest taki a nie inny.
     */
    public void train(Map<double[], double[]> map, LinkedList<Link> chain, Predicate<Double> predicate) {
        double error = map.size();
        while (predicate.test(error / map.size())) {
            for (Map.Entry<double[], double[]> entry : map.entrySet()) {
                error += this.train(entry.getKey(), entry.getValue(), chain);
            }
            this.bptt(chain);
        }
    }

    /**
     * W procesie klasyfikacji, nie dodajemy prediction do lancucha. On jest osobnym bytem.
     */
    public double[] classify(LinkedList<Link> chain, double... in) {
        Link current = this.activate(in, chain.getLast());
        return current.get(Type.py_).data;
    }

    /**
     * W procesie nauki, wszystko zostaje dodane do łańcucha.
     */
    private double train(double[] in, double[] out, LinkedList<Link> chain) {
        Link current = this.activate(in, chain.getLast());

        DoubleMatrix output = new DoubleMatrix(1, out.length, out);
        current.add(Type.y_, output);
        chain.add(current);
        return this.getMeanCategoricalCrossEntropy(current.get(Type.py_), output);
    }

    private Link activate(double[] in, Link previous) {
        Link current = new Link();
        DoubleMatrix input = new DoubleMatrix(1, in.length, in);
        current.add(Type.x_, input);

        DoubleMatrix preH = previous == null ? new DoubleMatrix(1, hiddenSize) : previous.get(Type.h_);

        DoubleMatrix r = logistic(input.mmul(this.Wxr).add(preH.mmul(this.Whr)).add(this.br));
        DoubleMatrix z = logistic(input.mmul(this.Wxz).add(preH.mmul(this.Whz)).add(this.bz));
        DoubleMatrix gh = tanh(input.mmul(this.Wxh).add(r.mul(preH).mmul(this.Whh)).add(this.bh));
        DoubleMatrix h = (DoubleMatrix.ones(1, z.columns).sub(z)).mul(preH).add(z.mul(gh));

        current.add(Type.r_, r);
        current.add(Type.z_, z);
        current.add(Type.gh_, gh);
        current.add(Type.h_, h);

        // Activation
        DoubleMatrix a = softmax(h.mmul(this.Why).add(this.by));
        current.add(Type.py_, a);

        return current;
    }

    private void bptt(LinkedList<Link> chain) {
        for (int i = chain.size() - 1; i >= 0; i--) {
//            if (i == chain.size() - 1) {
//                continue;
//            }
            Link current = chain.get(i);

            DoubleMatrix py = current.get(Type.py_);
            DoubleMatrix y = current.get(Type.y_);
            DoubleMatrix deltaY = py.sub(y);
            current.add(Type.dy_, deltaY);

            // cell output errors
            DoubleMatrix h = current.get(Type.h_);
            DoubleMatrix z = current.get(Type.z_);
            DoubleMatrix r = current.get(Type.r_);
            DoubleMatrix gh = current.get(Type.gh_);

            DoubleMatrix deltaH;
            if (current == chain.getLast()) {
                deltaH = this.Why.mmul(deltaY.transpose()).transpose();
            } else {
                Link previous = chain.get(i + 1);
                DoubleMatrix lateDh = previous.get(Type.dh_);
                DoubleMatrix lateDgh = previous.get(Type.dgh_);
                DoubleMatrix lateDr = previous.get(Type.dr_);
                DoubleMatrix lateDz = previous.get(Type.dz_);
                DoubleMatrix lateR = previous.get(Type.r_);
                DoubleMatrix lateZ = previous.get(Type.z_);
                deltaH = this.Why.mmul(deltaY.transpose()).transpose()
                        .add(this.Whr.mmul(lateDr.transpose()).transpose())
                        .add(this.Whz.mmul(lateDz.transpose()).transpose())
                        .add(this.Whh.mmul(lateDgh.mul(lateR).transpose()).transpose())
                        .add(lateDh.mul(DoubleMatrix.ones(1, lateZ.columns).sub(lateZ)));
            }

            current.add(Type.dh_, deltaH);

            // gh
            DoubleMatrix deltaGh = deltaH.mul(z).mul(deriveTanh(gh));
            current.add(Type.dgh_, deltaGh);

            DoubleMatrix preH;
            if (i > 0) {
                Link next = chain.get(i - 1);
                preH = next.get(Type.h_);
            } else {
                preH = DoubleMatrix.zeros(1, h.length);
            }

            // reset gates
            DoubleMatrix deltaR = (this.Whh.mmul(deltaGh.mul(preH).transpose()).transpose()).mul(deriveExp(r));
            current.add(Type.dr_, deltaR);

            // update gates
            DoubleMatrix deltaZ = deltaH.mul(gh.sub(preH)).mul(deriveExp(z));
            current.add(Type.dz_, deltaZ);
        }
        updateParameters(chain);
    }



    private void updateParameters(LinkedList<Link> chain) {
        DoubleMatrix gWxr = new DoubleMatrix(this.Wxr.rows, this.Wxr.columns);
        DoubleMatrix gWhr = new DoubleMatrix(this.Whr.rows, this.Whr.columns);
        DoubleMatrix gbr = new DoubleMatrix(this.br.rows, this.br.columns);

        DoubleMatrix gWxz = new DoubleMatrix(this.Wxz.rows, this.Wxz.columns);
        DoubleMatrix gWhz = new DoubleMatrix(this.Whz.rows, this.Whz.columns);
        DoubleMatrix gbz = new DoubleMatrix(this.bz.rows, this.bz.columns);

        DoubleMatrix gWxh = new DoubleMatrix(this.Wxh.rows, this.Wxh.columns);
        DoubleMatrix gWhh = new DoubleMatrix(this.Whh.rows, this.Whh.columns);
        DoubleMatrix gbh = new DoubleMatrix(this.bh.rows, this.bh.columns);

        DoubleMatrix gWhy = new DoubleMatrix(this.Why.rows, this.Why.columns);
        DoubleMatrix gby = new DoubleMatrix(this.by.rows, this.by.columns);

        for (int i = 0; i < chain.size(); i++) {
            Link current = chain.get(i);

            DoubleMatrix x = current.get(Type.x_).transpose();
            gWxr = gWxr.add(x.mmul(current.get(Type.dr_)));
            gWxz = gWxz.add(x.mmul(current.get(Type.dz_)));
            gWxh = gWxh.add(x.mmul(current.get(Type.dgh_)));

            if (i > 0) {
                Link previous = chain.get(i - 1);
                DoubleMatrix preH = previous.get(Type.h_).transpose();
                gWhr = gWhr.add(preH.mmul(current.get(Type.dr_)));
                gWhz = gWhz.add(preH.mmul(current.get(Type.dz_)));
                gWhh = gWhh.add(current.get(Type.r_).transpose().mul(preH).mmul(current.get(Type.dgh_)));
            }
            gWhy = gWhy.add(current.get(Type.h_).transpose().mmul(current.get(Type.dy_)));

            gbr = gbr.add(current.get(Type.dr_));
            gbz = gbz.add(current.get(Type.dz_));
            gbh = gbh.add(current.get(Type.dgh_));
            gby = gby.add(current.get(Type.dy_));
        }

        int size = chain.size();

        this.Wxr = this.Wxr.sub(gWxr.div(size).mul(this.rate));
        this.Whr = this.Whr.sub(gWhr.div(size < 2 ? 1 : (size - 1)).mul(this.rate));
        this.br = this.br.sub(gbr.div(size).mul(this.rate));

        this.Wxz = this.Wxz.sub(gWxz.div(size).mul(this.rate));
        this.Whz = this.Whz.sub(gWhz.div(size < 2 ? 1 : (size - 1)).mul(this.rate));
        this.bz = this.bz.sub(gbz.div(size).mul(this.rate));

        this.Wxh = this.Wxh.sub(gWxh.div(size).mul(this.rate));
        this.Whh = this.Whh.sub(gWhh.div(size < 2 ? 1 : (size - 1)).mul(this.rate));
        this.bh = this.bh.sub(gbh.div(size).mul(this.rate));

        this.Why = this.Why.sub(gWhy.div(size).mul(this.rate));
        this.by = this.by.sub(gby.div(size).mul(this.rate));
    }

    private DoubleMatrix deriveExp(DoubleMatrix f) {
        return f.mul(DoubleMatrix.ones(1, f.length).sub(f));
    }

    private DoubleMatrix deriveTanh(DoubleMatrix f) {
        return DoubleMatrix.ones(1, f.length).sub(MatrixFunctions.pow(f, 2));
    }

    private DoubleMatrix logistic(DoubleMatrix X) {
        return MatrixFunctions.pow(MatrixFunctions.exp(X.mul(-1)).add(1), -1);
    }

    private DoubleMatrix tanh(DoubleMatrix X) {
        return MatrixFunctions.tanh(X);
    }

    private DoubleMatrix uniform(int rows, int cols) {
        return DoubleMatrix.rand(rows, cols).mul(2 * this.scale).sub(this.scale);
    }

    private DoubleMatrix softmax(DoubleMatrix X) {
        DoubleMatrix expM = MatrixFunctions.exp(X);
        for (int i = 0; i < X.rows; i++) {
            DoubleMatrix expMi = expM.getRow(i);
            expM.putRow(i, expMi.div(expMi.sum()));
        }
        return expM;
    }

    private double getMeanCategoricalCrossEntropy(DoubleMatrix P, DoubleMatrix Q) {
        double e = 0;
        if (P.rows == Q.rows) {
            for (int i = 0; i < P.rows; i++) {
                e += getCategoricalCrossEntropy(P.getRow(i), Q.getRow(i));
            }
            e /= P.rows;
        } else {
            System.exit(-1);
        }
        return e;
    }

    private double getCategoricalCrossEntropy(DoubleMatrix p, DoubleMatrix q) {
        for (int i = 0; i < q.length; i++) {
            if (q.get(i) == 0) {
                q.put(i, 1e-10);
            }
        }
        return -p.mul(MatrixFunctions.log(q)).sum();
    }
}

/**
 * Logger jest per cala aplikacja. Logger jest takim systemem jednostronnej komunikacji z tworca aplikacji.
 */
public class N2 {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new DarculaLaf());
            if (SystemUtils.IS_OS_MAC) {
                com.apple.eawt.Application application = com.apple.eawt.Application.getApplication();
                if (application != null) {
                    application.setDockIconImage(ImageLoader.loadAsImage("N2.png"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<double[], double[]> set = new HashMap<double[], double[]>(){{
            put(Standardizer.apply(12, 14, 13, 15, 16), new double[] {0, 1});
            put(Standardizer.apply(15, 13, 11, 9, 5), new double[] {1, 0});
        }};
        GRU gru = new GRU(5, 5 * 5, 2);
        LinkedList<GRU.Link> chain = new LinkedList<>();
        gru.train(set, chain, error -> error > 1);
        double[] cls = gru.classify(chain, 12, 14, 13, 15, 16);


        SwingUtilities.invokeLater(() -> {
            AppController appController = new AppController(new Logger());
            appController.setVisible(true);
        });
    }
}
