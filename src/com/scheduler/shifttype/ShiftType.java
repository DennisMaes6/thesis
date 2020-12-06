package com.scheduler.shifttype;

import com.scheduler.assistant.AssistantType;
import com.scheduler.shifttype.period.ShiftTypePeriod;

import java.util.Set;

public abstract class ShiftType {

    public abstract int getRequiredNbAssistants();
    public abstract ShiftTypePeriod getSpanningPeriod();
    public abstract Set<AssistantType> getAllowedAssistantTypes();

}
