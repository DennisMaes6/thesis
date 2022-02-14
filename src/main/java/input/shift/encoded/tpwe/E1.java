package input.shift.encoded.tpwe;

import java.util.HashSet;

public class E1 extends EncodedTPWEShift{

    static final String codeName = "E1";


    public E1( int balance, int nbWeeks, double workload) {
        super(new HashSet<>(), codeName, balance, nbWeeks, workload);
    }
}
