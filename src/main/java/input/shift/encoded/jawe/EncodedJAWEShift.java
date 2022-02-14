package input.shift.encoded.jawe;

import input.shift.JuniorAssistantWeekend;
import input.shift.Shift;
import input.shift.encoded.EncodedShift;

import java.util.Set;

public class EncodedJAWEShift extends EncodedShift {
    public EncodedJAWEShift(Set<Integer> allowedWeeks, String codeName, int balance, int nbWeeks, double workload) {
        super(new JuniorAssistantWeekend(workload), allowedWeeks, codeName, balance, nbWeeks);
    }
}
