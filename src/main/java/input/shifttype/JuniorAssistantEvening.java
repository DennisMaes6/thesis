package input.shifttype;

import input.assistant.AssistantType;
import input.time.Day;

import java.util.Collections;
import java.util.HashSet;

public class JuniorAssistantEvening extends DayShift {

    private static final HashSet<AssistantType> ALLOWED_ASSISTANT_TYPES = new HashSet<>(
            Collections.singleton(AssistantType.JA)
    );

    @Override
    public int getRequiredNbAssistants(Day day) {
        if (day.isHoliday() || day.isWeekend())
            return 0;
        return 1;
    }

    @Override
    public HashSet<AssistantType> getAllowedAssistantTypes() {
        return ALLOWED_ASSISTANT_TYPES;
    }

    @Override
    public ShiftTypeId getId() {
        return ShiftTypeId.JAEV;
    }
}
