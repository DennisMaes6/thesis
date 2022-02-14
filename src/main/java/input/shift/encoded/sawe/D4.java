package input.shift.encoded.sawe;

import java.util.HashSet;

public class D4 extends EncodedSAWEShift{
    static final String codeName = "D4";
    static final HashSet<Integer> allowedWeeks = new HashSet<Integer>(){{
    }};

    public D4( int balance, int nbWeeks, double workload) {
        super(allowedWeeks, codeName, balance, nbWeeks, workload);
        HashSet<Integer> aWeeks = new HashSet<Integer>();
        aWeeks.add(nbWeeks - 2);
        setAllowedWeeks(aWeeks);
    }
}
