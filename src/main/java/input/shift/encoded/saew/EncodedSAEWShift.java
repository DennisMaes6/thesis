package input.shift.encoded.saew;

import input.shift.SeniorAssistantEveningWeek;
import input.shift.Shift;
import input.shift.encoded.EncodedShift;

import java.util.Set;

public class EncodedSAEWShift extends EncodedShift {
    public EncodedSAEWShift(Set<Integer> allowedWeeks, String codeName, int balance, int nbWeeks, double workload) {
        super(new SeniorAssistantEveningWeek(workload), allowedWeeks, codeName, balance, nbWeeks);
    }
}
