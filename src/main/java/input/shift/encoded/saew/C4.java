package input.shift.encoded.saew;

import java.util.HashSet;
import java.util.Set;

public class C4 extends EncodedSAEWShift{

    static final String codeName = "C4";
    static final HashSet<Integer> allowedWeeks = new HashSet<Integer>(){{
    }};

    public C4(int balance, int nbWeeks, double workload) {
        super(allowedWeeks, codeName, balance, nbWeeks, workload);
        HashSet<Integer> aWeeks = new HashSet<Integer>();
        aWeeks.add(nbWeeks - 2);
        setAllowedWeeks(aWeeks);
    }
}
