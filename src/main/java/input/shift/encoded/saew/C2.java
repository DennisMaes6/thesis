package input.shift.encoded.saew;

import java.util.HashSet;
import java.util.Set;

public class C2 extends EncodedSAEWShift{

    static final String codeName = "C2";
    static final HashSet<Integer> allowedWeeks = new HashSet<Integer>(){{
        add(1);
    }};

    public C2(int balance, int nbWeeks, double workload) {
        super(allowedWeeks, codeName, balance, nbWeeks, workload);
    }
}
