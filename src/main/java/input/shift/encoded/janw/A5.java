package input.shift.encoded.janw;

import java.util.HashSet;

public class A5 extends EncodedJANWShift {

    static final String codeName = "A5";

    static final HashSet<Integer> allowedWeeks = new HashSet<Integer>(){{
    }};

    public A5(int balance, int nbWeeks, double workload) {
        super(allowedWeeks, codeName, balance, nbWeeks, workload);
        HashSet<Integer> aWeeks = new HashSet<Integer>();
        aWeeks.add(nbWeeks - 1);
        setAllowedWeeks(aWeeks);
    }
}