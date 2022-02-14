package input.shift.encoded.sawe;

import java.util.HashSet;

public class D2 extends EncodedSAWEShift{

    static final String codeName = "D2";
    static final HashSet<Integer> allowedWeeks = new HashSet<Integer>(){{
        add(1);
    }};

    public D2( int balance, int nbWeeks, double workload) {
        super(allowedWeeks, codeName, balance, nbWeeks, workload);
    }
}
