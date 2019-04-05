package AI;

import org.jblas.DoubleMatrix;

import java.util.HashMap;

public class Link extends HashMap<Link.Type, DoubleMatrix> {
    enum Type {
        x_, h_, r_, z_, gh_, py_, y_, dy_, dh_, dgh_, dr_, dz_
    }
}
