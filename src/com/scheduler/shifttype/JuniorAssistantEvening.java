package com.scheduler.shifttype;

import com.scheduler.assistant.AssistantType;
import com.scheduler.time.Day;

import java.util.Collections;
import java.util.HashSet;

public class JuniorAssistantEvening extends ShiftType {

    @Override
    public int getRequiredNbAssistants(Day day) {
        if (day.isHoliday() || day.isWeekend()) return 0;
        return 1;
    }

    @Override
    public ShiftTypePeriod getSpanningPeriod() {
        return ShiftTypePeriod.DAY;
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
