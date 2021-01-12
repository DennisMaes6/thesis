package shifttype;

import assistant.AssistantType;
import time.Day;

import java.util.Set;

public abstract class ShiftType {
    public abstract int getRequiredNbAssistants(Day day);

    public abstract ShiftTypePeriod getSpanningPeriod();

    public abstract Set<AssistantType> getAllowedAssistantTypes();

    public abstract ShiftTypeId getId();
}
