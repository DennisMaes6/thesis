package input.shift.encoded.jawe;


import java.util.HashSet;

public class B3 extends EncodedJAWEShift{

    static final String codeName = "B3";

    static final HashSet<Integer> allowedWeeks = new HashSet<Integer>(){{
        add(2);
    }};

    public B3(int balance, int nbWeeks, double workload) {
        super(allowedWeeks, codeName, balance, nbWeeks, workload);
    }
}