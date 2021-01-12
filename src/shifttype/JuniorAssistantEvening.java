package shifttype;

import assistant.AssistantType;
import time.Day;

import java.util.Collections;
import java.util.HashSet;

public class JuniorAssistantEvening extends DayShift {

    @Override
    public int getRequiredNbAssistants(Day day) {
        if (day.isHoliday() || day.isWeekend()) return 0;
        return 1;
    }

    @Override
    public HashSet<AssistantType> getAllowedAssistantTypes() {
        return new HashSet<>(Collections.singleton(AssistantType.JA));
    }

    @Override
    public ShiftTypeId getId() {
        return ShiftTypeId.JA_E;
    }
}
