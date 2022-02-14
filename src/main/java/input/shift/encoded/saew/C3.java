package input.shift.encoded.saew;

import java.util.HashSet;
import java.util.Set;

public class C3 extends EncodedSAEWShift{

    static final String codeName = "C3";
    static final HashSet<Integer> allowedWeeks = new HashSet<Integer>(){{
        add(2);
    }};

    public C3(int balance, int nbWeeks, double workload) {
        super(allowedWeeks, codeName, balance, nbWeeks, workload);
    }
}
