package com.scheduler.shifttype;

import com.scheduler.assistants.AssistantType;
import com.scheduler.shifttype.period.ShiftTypePeriod;

import java.util.Collections;
import java.util.HashSet;

public class JuniorAssistantEvening extends ShiftType {

    @Override
    public int getRequiredNbAssistants() {
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
}
