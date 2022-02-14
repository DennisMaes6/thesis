package input.shift.encoded.call;

import java.util.HashSet;

public class F3 extends EncodedCALLShift{

    static final String codeName = "F3";
    static final HashSet<Integer> allowedWeeks = new HashSet<Integer>(){{
        add(2);
    }};

    public F3( int balance, int nbWeeks, double workload) {
        super(allowedWeeks, codeName, balance, nbWeeks, workload);
    }
}
