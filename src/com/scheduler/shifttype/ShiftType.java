package com.scheduler.shifttype;

import com.scheduler.assistant.AssistantType;
import com.scheduler.time.Day;

import java.util.Set;

public abstract class ShiftType {
    public abstract int getRequiredNbAssistants(Day day);

    public abstract ShiftTypePeriod getSpanningPeriod();

    public abstract Set<AssistantType> getAllowedAssistantTypes();

    public abstract ShiftTypeId getId();
}
