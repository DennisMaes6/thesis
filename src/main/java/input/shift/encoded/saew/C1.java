package input.shift.encoded.saew;

import java.util.HashSet;
import java.util.Set;

public class C1 extends EncodedSAEWShift{

    static final String codeName = "C1";

    public C1( int balance, int nbWeeks, double workload) {
        super(new HashSet<>(), codeName, balance, nbWeeks, workload);
    }
}
