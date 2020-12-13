package com.scheduler.shifttype;

import com.scheduler.assistant.AssistantType;
import com.scheduler.time.Day;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class JuniorAssistantWeekendHoliday extends ShiftType {
    @Override
    public int getRequiredNbAssistants(Day day) {
        if (day.isWeekend() || day.isHoliday()) {
            return 2;
        }
        return 0;
    }

    @Override
    public ShiftTypePeriod getSpanningPeriod() {
        return ShiftTypePeriod.WEEKEND_HOLIDAY;
    }

    @Override
    public Set<AssistantType> getAllowedAssistantTypes() {
        return new HashSet<>(Arrays.asList(
                AssistantType.JA,
                AssistantType.JA_F
            )
        );
    }

    @Override
    public ShiftTypeId getId() {
        return ShiftTypeId.JA_WH;
    }
}
