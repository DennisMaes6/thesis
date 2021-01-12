package shifttype;

import assistant.AssistantType;
import time.Day;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JuniorAssistantNightWeek extends WeekShift {

    @Override
    public int getRequiredNbAssistants(Day day) {
        return 1;
    }


    @Override
    public Set<AssistantType> getAllowedAssistantTypes() {
        return new HashSet<>(Collections.singleton(AssistantType.JA));
    }

    @Override
    public ShiftTypeId getId() {
        return ShiftTypeId.JA_NW;
    }
}