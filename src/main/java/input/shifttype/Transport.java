package input.shifttype;

import input.assistant.AssistantType;
import input.time.Day;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Transport extends WeekendHolidayShift {
    @Override
    public int getRequiredNbAssistants(Day day) {
        if (day.isWeekend()) {
            return 1;
        }
        return 0;
    }

    @Override
    public Set<AssistantType> getAllowedAssistantTypes() {
        return new HashSet<>(Arrays.asList(
                AssistantType.SA_NEO,
                AssistantType.SA_F_NEO
            )
        );
    }

    @Override
    public ShiftTypeId getId() {
        return ShiftTypeId.TSPT;
    }
}
