package input.shift.encoded.tpwe;

import java.util.HashSet;

public class E2 extends EncodedTPWEShift{

    static final String codeName = "E2";
    static final HashSet<Integer> allowedWeeks = new HashSet<Integer>(){{
        add(1);
    }};

    public E2( int balance, int nbWeeks, double workload) {
        super(allowedWeeks, codeName, balance, nbWeeks, workload);
    }
}
