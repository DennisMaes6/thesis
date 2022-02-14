package input.shift.encoded.jawe;

import java.util.HashSet;

public class B5 extends EncodedJAWEShift{

    static final String codeName = "B5";

    static final HashSet<Integer> allowedWeeks = new HashSet<Integer>(){{
    }};

    public B5(int balance, int nbWeeks, double workload) {
        super(allowedWeeks, codeName, balance, nbWeeks, workload);
        HashSet<Integer> aWeeks = new HashSet<Integer>();
        aWeeks.add(nbWeeks - 1);
        setAllowedWeeks(aWeeks);
    }
}
