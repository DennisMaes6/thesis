package input.shift;

import input.assistant.AssistantType;
import input.time.Day;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JuniorAssistantEvening extends Shift {

    private static final HashSet<AssistantType> ALLOWED_ASSISTANT_TYPES = new HashSet<>(
            Collections.singleton(AssistantType.JA)
    );


    public JuniorAssistantEvening() {
        super(1.0);
    }

    @Override
    public int getCoverage(Day day) {
        if (day.isWeekend() || day.isHoliday()) {
            return 0;
        }
        return 1;
    }

    @Override
    public ShiftPeriod getPeriod() {
        return ShiftPeriod.WEEKDAY;
    }

    @Override
    public Set<AssistantType> getAllowedAssistantTypes() {
        return ALLOWED_ASSISTANT_TYPES;
    }

    @Override
    public ShiftType getType() {
        return ShiftType.JAEV;
    }
}
