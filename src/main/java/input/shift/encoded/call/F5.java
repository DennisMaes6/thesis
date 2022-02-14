package input.shift.encoded.call;

import java.util.HashSet;

public class F5 extends EncodedCALLShift{

    static final String codeName = "F5";
    static final HashSet<Integer> allowedWeeks = new HashSet<Integer>(){{
    }};

    public F5( int balance, int nbWeeks, double workload) {
        super(allowedWeeks, codeName, balance, nbWeeks, workload);
        HashSet<Integer> aWeeks = new HashSet<Integer>();
        aWeeks.add(nbWeeks - 1);
        setAllowedWeeks(aWeeks);
    }
}
