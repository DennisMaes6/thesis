package input.shift.encoded.tpwe;

import java.util.HashSet;

public class E3 extends EncodedTPWEShift{

    static final String codeName = "E3";
    static final HashSet<Integer> allowedWeeks = new HashSet<Integer>(){{
        add(2);
    }};

    public E3( int balance, int nbWeeks, double workload) {
        super(allowedWeeks, codeName, balance, nbWeeks, workload);
    }
}
