package input.shift.encoded.tpwe;

import java.util.HashSet;

public class E4 extends EncodedTPWEShift{

    static final String codeName = "E4";
    static final HashSet<Integer> allowedWeeks = new HashSet<Integer>(){{
    }};

    public E4( int balance, int nbWeeks, double workload) {
        super(allowedWeeks, codeName, balance, nbWeeks, workload);
        HashSet<Integer> aWeeks = new HashSet<Integer>();
        aWeeks.add(nbWeeks - 2);
        setAllowedWeeks(aWeeks);
    }
}
