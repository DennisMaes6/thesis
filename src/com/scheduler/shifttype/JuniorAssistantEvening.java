package com.scheduler.shifttype;

import com.scheduler.assistants.AssistantType;
import com.scheduler.period.Period;

import java.util.Collections;
import java.util.HashSet;

public class JuniorAssistantEvening extends ShiftType {

    @Override
    public int getRequiredNbAssistants() {
        return 1;
    }

    @Override
    public Period getSpanningPeriod() {
        return Period.DAY;
    }

    @Override
    public HashSet<AssistantType> getAllowedAssistantTypes() {
        return new HashSet<>(Collections.singleton(AssistantType.JA));
    }
}
