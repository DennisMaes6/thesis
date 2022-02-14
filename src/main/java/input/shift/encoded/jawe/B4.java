package input.shift.encoded.jawe;


import java.util.HashSet;

public class B4 extends EncodedJAWEShift{

    static final String codeName = "B4";

    static final HashSet<Integer> allowedWeeks = new HashSet<Integer>(){{
    }};

    public B4(int balance, int nbWeeks, double workload) {
        super(allowedWeeks, codeName, balance, nbWeeks, workload);
        HashSet<Integer> aWeeks = new HashSet<Integer>();
        aWeeks.add(nbWeeks - 2);
        setAllowedWeeks(aWeeks);
    }
}
