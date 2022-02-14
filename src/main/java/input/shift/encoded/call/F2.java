package input.shift.encoded.call;

import java.util.HashSet;

public class F2 extends EncodedCALLShift{

    static final String codeName = "F2";
    static final HashSet<Integer> allowedWeeks = new HashSet<Integer>(){{
        add(1);
    }};

    public F2( int balance, int nbWeeks, double workload) {
        super(allowedWeeks, codeName, balance, nbWeeks, workload);
    }
}
