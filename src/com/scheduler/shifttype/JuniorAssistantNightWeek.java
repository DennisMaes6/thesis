package com.scheduler.shifttype;

import com.scheduler.assistant.AssistantType;
import com.scheduler.shifttype.period.ShiftTypePeriod;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JuniorAssistantNightWeek extends ShiftType {

    @Override
    public int getRequiredNbAssistants() {
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
}