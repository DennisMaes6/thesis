package input.shift;

import input.assistant.AssistantType;
import input.time.Day;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JuniorAssistantNightWeek extends WeekShift {

    private static final HashSet<AssistantType> ALLOWED_ASSISTANT_TYPES = new HashSet<>(
            Collections.singleton(AssistantType.JA)
    );

    public JuniorAssistantNightWeek(double workload) {
        
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
        return ShiftType.JANW;
    }
}