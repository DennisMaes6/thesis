package input.shifttype;

import input.assistant.AssistantType;
import input.time.Day;

import java.util.Set;

public abstract class ShiftType {
    public abstract int getRequiredNbAssistants(Day day);

    public abstract ShiftTypePeriod getSpanningPeriod();

    public abstract Set<AssistantType> getAllowedAssistantTypes();

    public abstract ShiftTypeId getId();
}
