import Algorithm.ImageLoader;
import Algorithm.Logger;
import com.bulenkov.darcula.DarculaLaf;
import org.apache.commons.lang3.SystemUtils;
import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

import javax.swing.*;
import java.util.*;
import java.util.function.Consumer;

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
        x_, h_, r_, z_, gh_
    }

    class Link {
        private final Map<Type, DoubleMatrix> points = new HashMap<>();

        DoubleMatrix get(Type type) {
            return this.points.get(type);
        }

        boolean has(Type type) {
            return this.points.containsKey(type);
        }
    }

    class Chain implements Iterable<Link> {
        private final List<Link> links = new ArrayList<>();

        int size() {
            return this.links.size();
        }

        Link last() {
            return this.links.get(this.links.size() - 1);
        }

        boolean has(Type type) {
            for (int i = this.links.size() - 1; i >= 0; i--) {

            }
        }

        @Override
        public Iterator<Link> iterator() {
            return this.links.iterator();
        }

        @Override
        public void forEach(Consumer<? super Link> action) {
            this.links.forEach(action);
        }

        @Override
        public Spliterator<Link> spliterator() {
            return this.links.spliterator();
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

    public void active2(Chain chain) {
        DoubleMatrix x = chain.last().get(Type.x_);
//                acts.get("x" + t);
        DoubleMatrix preH = chain.size() == 1 ?
                t == 0 ? new DoubleMatrix(1, hiddenSize) : acts.get("h" + (t - 1));

        DoubleMatrix r = logistic(x.mmul(Wxr).add(preH.mmul(Whr)).add(br));
        DoubleMatrix z = logistic(x.mmul(Wxz).add(preH.mmul(Whz)).add(bz));
        DoubleMatrix gh = tanh(x.mmul(Wxh).add(r.mul(preH).mmul(Whh)).add(bh));
        DoubleMatrix h = (DoubleMatrix.ones(1, z.columns).sub(z)).mul(preH).add(z.mul(gh));

        acts.put("r" + t, r);
        acts.put("z" + t, z);
        acts.put("gh" + t, gh);
        acts.put("h" + t, h);
    }

    public void active(int t, Map<String, DoubleMatrix> acts) {
        DoubleMatrix x = acts.get("x" + t);
        DoubleMatrix preH = t == 0 ? new DoubleMatrix(1, hiddenSize) : acts.get("h" + (t - 1));

        DoubleMatrix r = logistic(x.mmul(Wxr).add(preH.mmul(Whr)).add(br));
        DoubleMatrix z = logistic(x.mmul(Wxz).add(preH.mmul(Whz)).add(bz));
        DoubleMatrix gh = tanh(x.mmul(Wxh).add(r.mul(preH).mmul(Whh)).add(bh));
        DoubleMatrix h = (DoubleMatrix.ones(1, z.columns).sub(z)).mul(preH).add(z.mul(gh));

        acts.put("r" + t, r);
        acts.put("z" + t, z);
        acts.put("gh" + t, gh);
        acts.put("h" + t, h);
    }

    public void bptt(Map<String, DoubleMatrix> acts, int lastT) {
        for (int t = lastT; t > -1; t--) {
            DoubleMatrix py = acts.get("py" + t);
            DoubleMatrix y = acts.get("y" + t);
            DoubleMatrix deltaY = py.sub(y);
            acts.put("dy" + t, deltaY);

            // cell output errors
            DoubleMatrix h = acts.get("h" + t);
            DoubleMatrix z = acts.get("z" + t);
            DoubleMatrix r = acts.get("r" + t);
            DoubleMatrix gh = acts.get("gh" + t);

            DoubleMatrix deltaH;
            if (t == lastT) {
                deltaH = Why.mmul(deltaY.transpose()).transpose();
            } else {
                DoubleMatrix lateDh = acts.get("dh" + (t + 1));
                DoubleMatrix lateDgh = acts.get("dgh" + (t + 1));
                DoubleMatrix lateDr = acts.get("dr" + (t + 1));
                DoubleMatrix lateDz = acts.get("dz" + (t + 1));
                DoubleMatrix lateR = acts.get("r" + (t + 1));
                DoubleMatrix lateZ = acts.get("z" + (t + 1));
                deltaH = Why.mmul(deltaY.transpose()).transpose()
                        .add(Whr.mmul(lateDr.transpose()).transpose())
                        .add(Whz.mmul(lateDz.transpose()).transpose())
                        .add(Whh.mmul(lateDgh.mul(lateR).transpose()).transpose())
                        .add(lateDh.mul(DoubleMatrix.ones(1, lateZ.columns).sub(lateZ)));
            }
            acts.put("dh" + t, deltaH);

            // gh
            DoubleMatrix deltaGh = deltaH.mul(z).mul(deriveTanh(gh));
            acts.put("dgh" + t, deltaGh);

            DoubleMatrix preH = t > 0 ? acts.get("h" + (t - 1)) : DoubleMatrix.zeros(1, h.length);

            // reset gates
            DoubleMatrix deltaR = (Whh.mmul(deltaGh.mul(preH).transpose()).transpose()).mul(deriveExp(r));
            acts.put("dr" + t, deltaR);

            // update gates
            DoubleMatrix deltaZ = deltaH.mul(gh.sub(preH)).mul(deriveExp(z));
            acts.put("dz" + t, deltaZ);
        }
        updateParameters(acts, lastT, rate);
    }

    public DoubleMatrix decode(DoubleMatrix ht) {
        return softmax(ht.mmul(Why).add(by));
    }

    private void updateParameters(Map<String, DoubleMatrix> acts, int lastT, double lr) {
        DoubleMatrix gWxr = new DoubleMatrix(Wxr.rows, Wxr.columns);
        DoubleMatrix gWhr = new DoubleMatrix(Whr.rows, Whr.columns);
        DoubleMatrix gbr = new DoubleMatrix(br.rows, br.columns);

        DoubleMatrix gWxz = new DoubleMatrix(Wxz.rows, Wxz.columns);
        DoubleMatrix gWhz = new DoubleMatrix(Whz.rows, Whz.columns);
        DoubleMatrix gbz = new DoubleMatrix(bz.rows, bz.columns);

        DoubleMatrix gWxh = new DoubleMatrix(Wxh.rows, Wxh.columns);
        DoubleMatrix gWhh = new DoubleMatrix(Whh.rows, Whh.columns);
        DoubleMatrix gbh = new DoubleMatrix(bh.rows, bh.columns);

        DoubleMatrix gWhy = new DoubleMatrix(Why.rows, Why.columns);
        DoubleMatrix gby = new DoubleMatrix(by.rows, by.columns);

        for (int t = 0; t < lastT + 1; t++) {
            DoubleMatrix x = acts.get("x" + t).transpose();
            gWxr = gWxr.add(x.mmul(acts.get("dr" + t)));
            gWxz = gWxz.add(x.mmul(acts.get("dz" + t)));
            gWxh = gWxh.add(x.mmul(acts.get("dgh" + t)));

            if (t > 0) {
                DoubleMatrix preH = acts.get("h" + (t - 1)).transpose();
                gWhr = gWhr.add(preH.mmul(acts.get("dr" + t)));
                gWhz = gWhz.add(preH.mmul(acts.get("dz" + t)));
                gWhh = gWhh.add(acts.get("r" + t).transpose().mul(preH).mmul(acts.get("dgh" + t)));
            }
            gWhy = gWhy.add(acts.get("h" + t).transpose().mmul(acts.get("dy" + t)));

            gbr = gbr.add(acts.get("dr" + t));
            gbz = gbz.add(acts.get("dz" + t));
            gbh = gbh.add(acts.get("dgh" + t));
            gby = gby.add(acts.get("dy" + t));
        }

        Wxr = Wxr.sub(gWxr.div(lastT).mul(lr));
        Whr = Whr.sub(gWhr.div(lastT < 2 ? 1 : (lastT - 1)).mul(lr));
        br = br.sub(gbr.div(lastT).mul(lr));

        Wxz = Wxz.sub(gWxz.div(lastT).mul(lr));
        Whz = Whz.sub(gWhz.div(lastT < 2 ? 1 : (lastT - 1)).mul(lr));
        bz = bz.sub(gbz.div(lastT).mul(lr));

        Wxh = Wxh.sub(gWxh.div(lastT).mul(lr));
        Whh = Whh.sub(gWhh.div(lastT < 2 ? 1 : (lastT - 1)).mul(lr));
        bh = bh.sub(gbh.div(lastT).mul(lr));

        Why = Why.sub(gWhy.div(lastT).mul(lr));
        by = by.sub(gby.div(lastT).mul(lr));
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
        return DoubleMatrix.rand(rows, cols).mul(2 * scale).sub(scale);
    }

    private DoubleMatrix softmax(DoubleMatrix X) {
        DoubleMatrix expM = MatrixFunctions.exp(X);
        for (int i = 0; i < X.rows; i++) {
            DoubleMatrix expMi = expM.getRow(i);
            expM.putRow(i, expMi.div(expMi.sum()));
        }
        return expM;
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
        SwingUtilities.invokeLater(() -> {
            AppController appController = new AppController(new Logger());
            appController.setVisible(true);
        });
    }
}
