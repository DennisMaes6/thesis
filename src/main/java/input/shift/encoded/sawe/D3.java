package input.shift.encoded.sawe;

import java.util.HashSet;

public class D3 extends EncodedSAWEShift{
    static final String codeName = "D3";
    static final HashSet<Integer> allowedWeeks = new HashSet<Integer>(){{
        add(2);
    }};
    public D3( int balance, int nbWeeks, double workload) {
        super(allowedWeeks, codeName, balance, nbWeeks, workload);
    }
}
