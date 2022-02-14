package input.shift.encoded.janw;

import java.util.HashSet;

public class A3 extends EncodedJANWShift {

    static final String codeName = "A3";

    static final HashSet<Integer> allowedWeeks = new HashSet<Integer>(){{
        add(2);
    }};

    public A3(int balance, int nbWeeks, double workload) {
        super(allowedWeeks, codeName, balance, nbWeeks, workload);
    }
}