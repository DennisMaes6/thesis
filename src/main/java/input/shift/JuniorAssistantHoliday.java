package input.shift;

import input.assistant.AssistantType;
import input.time.Day;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class JuniorAssistantHoliday extends HolidayShift {

    private static final HashSet<AssistantType> ALLOWED_ASSISTANT_TYPES = new HashSet<>(
            Arrays.asList(
                    AssistantType.JA,
                    AssistantType.JA_F
            )
    );

    public JuniorAssistantHoliday(double workload) {
        super(workload);
    }


    @Override
    public int getCoverage(Day day) {
        if (day.isHoliday() && !day.isWeekend())
            return 2;
        return 0;
    }

    @Override
    public Set<AssistantType> getAllowedAssistantTypes() {
        return ALLOWED_ASSISTANT_TYPES;
    }

    @Override
    public ShiftType getType() {
        return ShiftType.JAHO;
    }
}
