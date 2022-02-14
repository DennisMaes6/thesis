package input.shift.encoded.janw;

import java.util.HashSet;

public class A2 extends EncodedJANWShift {

    static final String codeName = "A2";

    static final HashSet<Integer> allowedWeeks = new HashSet<Integer>(){{
        add(1);
    }};

    public A2(int balance, int nbWeeks, double workload) {
        super(allowedWeeks, codeName, balance, nbWeeks, workload);
    }
}