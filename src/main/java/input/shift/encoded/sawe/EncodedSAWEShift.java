package input.shift.encoded.sawe;

import input.shift.SeniorAssistantWeekend;
import input.shift.Shift;
import input.shift.encoded.EncodedShift;

import java.util.Set;

public class EncodedSAWEShift extends EncodedShift {
    public EncodedSAWEShift(Set<Integer> allowedWeeks, String codeName, int balance, int nbWeeks, double workload) {
        super(new SeniorAssistantWeekend(workload), allowedWeeks, codeName, balance, nbWeeks);
    }
}
