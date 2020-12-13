package com.scheduler.shifttype;

import com.scheduler.assistant.AssistantType;
import com.scheduler.time.Day;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Transport extends WeekendHolidayShift {
    @Override
    public int getRequiredNbAssistants(Day day) {
        return 1;
    }

    @Override
    public Set<AssistantType> getAllowedAssistantTypes() {
        return new HashSet<>(Arrays.asList(
                AssistantType.SA_N,
                AssistantType.SA_F_N
            )
        );
    }

    @Override
    public ShiftTypeId getId() {
        return ShiftTypeId.T;
    }
}
