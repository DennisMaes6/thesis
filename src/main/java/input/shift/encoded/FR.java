package input.shift.encoded;

import input.shift.FreeShift;
import input.shift.Shift;

import java.util.HashSet;
import java.util.Set;

public class FR extends EncodedShift{

    static final String codeName = "FR";

    public FR(int balance, int nbWeeks) {
        super(new FreeShift(1), new HashSet<>(), codeName, balance, nbWeeks);
    }
}
