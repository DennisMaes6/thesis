package com.scheduler.shifttype;

import com.scheduler.assistant.AssistantType;
import com.scheduler.time.Day;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SeniorAssistantWeekendHoliday extends WeekendHolidayShift {


    @Override
    public int getRequiredNbAssistants(Day day) {
        if (day.isWeekend() || day.isHoliday()) return 2;
        return 0;
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

    @Override
    public ShiftTypeId getId() {
        return ShiftTypeId.SA_WH;
    }
}
