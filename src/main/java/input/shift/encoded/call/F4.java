package input.shift.encoded.call;

import java.util.HashSet;

public class F4 extends EncodedCALLShift{

    static final String codeName = "F4";
    static final HashSet<Integer> allowedWeeks = new HashSet<Integer>(){{
    }};

    public F4( int balance, int nbWeeks, double workload) {
        super(allowedWeeks, codeName, balance, nbWeeks, workload);
        HashSet<Integer> aWeeks = new HashSet<Integer>();
        aWeeks.add(nbWeeks - 2);
        setAllowedWeeks(aWeeks);
    }
}
