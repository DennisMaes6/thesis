package input.shift.encoded.jawe;


import java.util.HashSet;

public class B1 extends EncodedJAWEShift {

    static final String codeName = "B1";

    public B1(int balance, int nbWeeks, double workload) {
        super(new HashSet<>(), codeName, balance, nbWeeks, workload);
    }
}
