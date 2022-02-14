package input.shift.encoded.jawe;

import java.util.HashSet;

public class B2 extends EncodedJAWEShift{

    static final String codeName = "B2";

    static final HashSet<Integer> allowedWeeks = new HashSet<Integer>(){{
        add(1);
    }};

    public B2(int balance, int nbWeeks, double workload) {
        super(allowedWeeks, codeName, balance, nbWeeks, workload);
    }
}
