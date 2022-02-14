package input.shift.encoded.sawe;

import java.util.HashSet;

public class D1 extends EncodedSAWEShift{

    static final String codeName = "D1";

    public D1( int balance, int nbWeeks, double workload) {
        super(new HashSet<>(), codeName, balance, nbWeeks, workload);
    }
}
