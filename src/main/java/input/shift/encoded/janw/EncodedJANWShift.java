package input.shift.encoded.janw;

import input.shift.JuniorAssistantNightWeek;
import input.shift.JuniorAssistantWeekend;
import input.shift.Shift;
import input.shift.encoded.EncodedShift;

import java.util.Set;

public class EncodedJANWShift extends EncodedShift {


    public EncodedJANWShift(Set<Integer> allowedWeeks, String codeName, int balance, int nbWeeks, double workload) {
        super(new JuniorAssistantNightWeek(workload), allowedWeeks, codeName, balance, nbWeeks);
    }



}
