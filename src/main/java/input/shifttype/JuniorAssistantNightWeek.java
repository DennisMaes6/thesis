package input.shifttype;

import input.assistant.AssistantType;
import input.time.Day;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JuniorAssistantNightWeek extends WeekShift {

    private static final HashSet<AssistantType> ALLOWED_ASSISTANT_TYPES = new HashSet<>(
            Collections.singleton(AssistantType.JA)
    );

    @Override
    public int getRequiredNbAssistants(Day day) {
        return 1;
    }


    @Override
    public Set<AssistantType> getAllowedAssistantTypes() {
        return ALLOWED_ASSISTANT_TYPES;
    }

    @Override
    public ShiftTypeId getId() {
        return ShiftTypeId.JANW;
    }
}