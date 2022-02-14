package input.shift.encoded.janw;

import java.util.HashSet;

public class A4 extends EncodedJANWShift {

    static final String codeName = "A4";

    static final HashSet<Integer> allowedWeeks = new HashSet<Integer>(){{
    }};

    public A4(int balance, int nbWeeks, double workload) {
        super(allowedWeeks, codeName, balance, nbWeeks, workload);
        HashSet<Integer> aWeeks = new HashSet<Integer>();
        aWeeks.add(nbWeeks - 2);
        setAllowedWeeks(aWeeks);
    }
}