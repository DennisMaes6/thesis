package input.shift.encoded.call;

import java.util.HashSet;

public class F1 extends EncodedCALLShift{

    static final String codeName = "F1";

    public F1( int balance, int nbWeeks, double workload) {
        super(new HashSet<>(), codeName, balance, nbWeeks, workload);
    }
}
