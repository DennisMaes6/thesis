package input.shift.encoded.saew;

import java.util.HashSet;
import java.util.Set;

public class C5 extends EncodedSAEWShift{

    static final String codeName = "C5";
    static final HashSet<Integer> allowedWeeks = new HashSet<Integer>(){{
    }};

    public C5( int balance, int nbWeeks, double workload) {
        super(allowedWeeks, codeName, balance, nbWeeks, workload);
        HashSet<Integer> aWeeks = new HashSet<Integer>();
        aWeeks.add(nbWeeks - 1);
        setAllowedWeeks(aWeeks);
    }
}
