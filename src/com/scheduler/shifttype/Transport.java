package com.scheduler.shifttype;

import com.scheduler.assistants.AssistantType;
import com.scheduler.period.Period;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Transport extends ShiftType {

    @Override
    public int getRequiredNbAssistants() {
        return 1;
    }

    @Override
    public Period getSpanningPeriod() {
        return Period.WEEKEND_HOLIDAY;
    }

    @Override
    public Set<AssistantType> getAllowedAssistantTypes() {
        return new HashSet<>(Arrays.asList(
                AssistantType.SA_NEO,
                AssistantType.SA_F_NEO
            )
        );
    }
}
