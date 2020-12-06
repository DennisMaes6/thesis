package com.scheduler.shifttype;

import com.scheduler.assistants.AssistantType;
import com.scheduler.period.Period;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JuniorAssistantNightWeek extends ShiftType {

    @Override
    public int getRequiredNbAssistants() {
        return 1;
    }

    @Override
    public Period getSpanningPeriod() {
        return Period.WEEK;
    }

    @Override
    public Set<AssistantType> getAllowedAssistantTypes() {
        return new HashSet<>(Collections.singleton(AssistantType.JA));
    }
}