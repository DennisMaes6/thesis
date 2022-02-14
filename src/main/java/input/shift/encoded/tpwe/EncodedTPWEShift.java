package input.shift.encoded.tpwe;

import input.shift.Shift;
import input.shift.TransportWeekend;
import input.shift.encoded.EncodedShift;

import java.util.Set;

public class EncodedTPWEShift extends EncodedShift {
    public EncodedTPWEShift(Set<Integer> allowedWeeks, String codeName, int balance, int nbWeeks, double workload) {
        super(new TransportWeekend(workload), allowedWeeks, codeName, balance, nbWeeks);
    }
}
