package com.scheduler.shifttype;

import com.scheduler.assistant.AssistantType;
import com.scheduler.time.Day;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JuniorAssistantNightWeek extends ShiftType {

    @Override
    public int getRequiredNbAssistants(Day day) {
        return 1;
    }

    @Override
    public ShiftTypePeriod getSpanningPeriod() {
        return ShiftTypePeriod.WEEK;
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