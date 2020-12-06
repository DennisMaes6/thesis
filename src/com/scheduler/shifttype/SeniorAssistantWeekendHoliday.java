package com.scheduler.shifttype;

import com.scheduler.assistants.AssistantType;
import com.scheduler.shifttype.period.ShiftTypePeriod;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SeniorAssistantWeekendHoliday extends ShiftType {

    @Override
    public int getRequiredNbAssistants() {
        return 2;
    }

    @Override
    public ShiftTypePeriod getSpanningPeriod() {
        return ShiftTypePeriod.WEEKEND_HOLIDAY;
    }

    @Override
    public Set<AssistantType> getAllowedAssistantTypes() {
        return new HashSet<>(Arrays.asList(
                AssistantType.SA,
                AssistantType.SA_F,
                AssistantType.SA_NEO,
                AssistantType.SA_F_NEO
            )
        );
    }
}
