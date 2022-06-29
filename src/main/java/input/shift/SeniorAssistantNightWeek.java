package input.shift;

import input.assistant.AssistantType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SeniorAssistantNightWeek extends WeekShift {

    private static final HashSet<AssistantType> ALLOWED_ASSISTANT_TYPES = new HashSet<>(
            Arrays.asList(
                    AssistantType.SA,
                    AssistantType.SA_NEO
            )
    );

    public SeniorAssistantNightWeek(double workload) {
        
        super(workload);
        //super(1.0);
        
    }
    /*
    @Override
    public int getCoverage(Day day) {
        return 1;
    } 
    */


    @Override
    public Set<AssistantType> getAllowedAssistantTypes() {
        return ALLOWED_ASSISTANT_TYPES;
    }

    @Override
    public ShiftType getType() {
        return ShiftType.SANW;
    }
}