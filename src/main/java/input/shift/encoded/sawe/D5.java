package input.shift.encoded.sawe;

import java.util.HashSet;

public class D5 extends EncodedSAWEShift{

    static final String codeName = "D5";
    static final HashSet<Integer> allowedWeeks = new HashSet<Integer>(){{
    }};

    public D5( int balance, int nbWeeks, double workload) {
        super(allowedWeeks, codeName, balance, nbWeeks, workload);
        HashSet<Integer> aWeeks = new HashSet<Integer>();
        aWeeks.add(nbWeeks - 1);
        setAllowedWeeks(aWeeks);
    }
}
