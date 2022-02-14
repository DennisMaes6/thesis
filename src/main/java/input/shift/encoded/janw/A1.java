package input.shift.encoded.janw;

import java.util.HashSet;
import java.util.Set;

public class A1 extends EncodedJANWShift {
    static final String codeName = "A1";

    public A1(int balance, int nbWeeks, double workload) {
        super(new HashSet<>(), codeName, balance, nbWeeks, workload);
    }
}
