package AI;

import com.sun.istack.internal.Nullable;
import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;


public class GRU {
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
    public void train(Map<double[], double[]> map, List<Link> chain, Predicate<Double> predicate) {
        double error;
        do {
            error = 0;
            for (Map.Entry<double[], double[]> entry : map.entrySet()) {
                error += this.train(entry.getKey(), entry.getValue(), chain);
            }
            this.bptt(chain);
        } while (predicate.test(error / map.size()));
    }

    /**
     * W procesie klasyfikacji, nie dodajemy prediction do lancucha. On jest osobnym bytem.
     */
    public double[] classify(List<Link> chain, double... in) {
        Link current = this.activate(in, chain.get(chain.size() - 1));
        return current.get(Link.Type.py_).data;
    }

    /**
     * W procesie nauki, wszystko zostaje dodane do łańcucha.
     */
    private double train(double[] in, double[] out, List<Link> chain) {
        Link current = this.activate(in, chain.isEmpty() ? null : chain.get(chain.size() - 1));

        DoubleMatrix output = new DoubleMatrix(1, out.length, out);
        current.put(Link.Type.y_, output);
        chain.add(current);
        return this.getMeanCategoricalCrossEntropy(current.get(Link.Type.py_), output);
    }

    private Link activate(double[] in, @Nullable Link previous) {
        Link current = new Link();
        DoubleMatrix input = new DoubleMatrix(1, in.length, in);
        current.put(Link.Type.x_, input);

        DoubleMatrix preH = previous == null ? new DoubleMatrix(1, hiddenSize) : previous.get(Link.Type.h_);

        DoubleMatrix r = logistic(input.mmul(this.Wxr).add(preH.mmul(this.Whr)).add(this.br));
        DoubleMatrix z = logistic(input.mmul(this.Wxz).add(preH.mmul(this.Whz)).add(this.bz));
        DoubleMatrix gh = tanh(input.mmul(this.Wxh).add(r.mul(preH).mmul(this.Whh)).add(this.bh));
        DoubleMatrix h = (DoubleMatrix.ones(1, z.columns).sub(z)).mul(preH).add(z.mul(gh));

        current.put(Link.Type.r_, r);
        current.put(Link.Type.z_, z);
        current.put(Link.Type.gh_, gh);
        current.put(Link.Type.h_, h);

        // Activation
        DoubleMatrix a = softmax(h.mmul(this.Why).add(this.by));
        current.put(Link.Type.py_, a);

        return current;
    }

    private void bptt(List<Link> chain) {
        for (int i = chain.size() - 1; i >= 0; i--) {
            Link current = chain.get(i);

            DoubleMatrix py = current.get(Link.Type.py_);
            DoubleMatrix y = current.get(Link.Type.y_);
            DoubleMatrix deltaY = py.sub(y);
            current.put(Link.Type.dy_, deltaY);

            // cell output errors
            DoubleMatrix h = current.get(Link.Type.h_);
            DoubleMatrix z = current.get(Link.Type.z_);
            DoubleMatrix r = current.get(Link.Type.r_);
            DoubleMatrix gh = current.get(Link.Type.gh_);

            DoubleMatrix deltaH;
            if (i == chain.size() - 1) {
                deltaH = this.Why.mmul(deltaY.transpose()).transpose();
            } else {
                Link previous = chain.get(i + 1);
                DoubleMatrix lateDh = previous.get(Link.Type.dh_);
                DoubleMatrix lateDgh = previous.get(Link.Type.dgh_);
                DoubleMatrix lateDr = previous.get(Link.Type.dr_);
                DoubleMatrix lateDz = previous.get(Link.Type.dz_);
                DoubleMatrix lateR = previous.get(Link.Type.r_);
                DoubleMatrix lateZ = previous.get(Link.Type.z_);
                deltaH = this.Why.mmul(deltaY.transpose()).transpose()
                        .add(this.Whr.mmul(lateDr.transpose()).transpose())
                        .add(this.Whz.mmul(lateDz.transpose()).transpose())
                        .add(this.Whh.mmul(lateDgh.mul(lateR).transpose()).transpose())
                        .add(lateDh.mul(DoubleMatrix.ones(1, lateZ.columns).sub(lateZ)));
            }

            current.put(Link.Type.dh_, deltaH);

            // gh
            DoubleMatrix deltaGh = deltaH.mul(z).mul(deriveTanh(gh));
            current.put(Link.Type.dgh_, deltaGh);

            DoubleMatrix preH;
            if (i > 0) {
                Link next = chain.get(i - 1);
                preH = next.get(Link.Type.h_);
            } else {
                preH = DoubleMatrix.zeros(1, h.length);
            }

            // reset gates
            DoubleMatrix deltaR = (this.Whh.mmul(deltaGh.mul(preH).transpose()).transpose()).mul(deriveExp(r));
            current.put(Link.Type.dr_, deltaR);

            // update gates
            DoubleMatrix deltaZ = deltaH.mul(gh.sub(preH)).mul(deriveExp(z));
            current.put(Link.Type.dz_, deltaZ);
        }
        updateParameters(chain);
    }

    private void updateParameters(List<Link> chain) {
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

            DoubleMatrix x = current.get(Link.Type.x_).transpose();
            gWxr = gWxr.add(x.mmul(current.get(Link.Type.dr_)));
            gWxz = gWxz.add(x.mmul(current.get(Link.Type.dz_)));
            gWxh = gWxh.add(x.mmul(current.get(Link.Type.dgh_)));

            if (i > 0) {
                Link previous = chain.get(i - 1);
                DoubleMatrix preH = previous.get(Link.Type.h_).transpose();
                gWhr = gWhr.add(preH.mmul(current.get(Link.Type.dr_)));
                gWhz = gWhz.add(preH.mmul(current.get(Link.Type.dz_)));
                gWhh = gWhh.add(current.get(Link.Type.r_).transpose().mul(preH).mmul(current.get(Link.Type.dgh_)));
            }
            gWhy = gWhy.add(current.get(Link.Type.h_).transpose().mmul(current.get(Link.Type.dy_)));

            gbr = gbr.add(current.get(Link.Type.dr_));
            gbz = gbz.add(current.get(Link.Type.dz_));
            gbh = gbh.add(current.get(Link.Type.dgh_));
            gby = gby.add(current.get(Link.Type.dy_));
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
