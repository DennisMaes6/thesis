package input.shift.encoded.call;

import input.shift.Call;
import input.shift.Shift;
import input.shift.encoded.EncodedShift;

import java.util.Set;

public class EncodedCALLShift extends EncodedShift {
    public EncodedCALLShift(Set<Integer> allowedWeeks, String codeName, int balance, int nbWeeks, double workload) {
        super(new Call(workload), allowedWeeks, codeName, balance, nbWeeks);
    }
}
