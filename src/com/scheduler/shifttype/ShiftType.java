package com.scheduler.shifttype;

import com.scheduler.assistants.AssistantType;
import com.scheduler.period.Period;

import java.util.Set;

public abstract class ShiftType {

    public abstract int getRequiredNbAssistants();
    public abstract Period getSpanningPeriod();
    public abstract Set<AssistantType> getAllowedAssistantTypes();

}
