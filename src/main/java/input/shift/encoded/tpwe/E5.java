package input.shift.encoded.tpwe;

import java.util.HashSet;

public class E5 extends EncodedTPWEShift{
    static final String codeName = "E5";
    static final HashSet<Integer> allowedWeeks = new HashSet<Integer>(){{
    }};

    public E5( int balance, int nbWeeks, double workload) {
        super(allowedWeeks, codeName, balance, nbWeeks, workload);
        HashSet<Integer> aWeeks = new HashSet<Integer>();
        aWeeks.add(nbWeeks - 1);
        setAllowedWeeks(aWeeks);
    }
}
