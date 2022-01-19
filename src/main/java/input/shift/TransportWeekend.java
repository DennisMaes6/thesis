package input.shift;

import input.assistant.AssistantType;
import input.time.Day;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TransportWeekend extends WeekendShift {

    private static final HashSet<AssistantType> ALLOWED_ASSISTANT_TYPES = new HashSet<>(
            Arrays.asList(
                    AssistantType.SA_NEO,
                    AssistantType.SA_F_NEO
            )
    );

    public TransportWeekend(double workload) {
        super(workload);
    }

    /*
    @Override
    public int getCoverage(Day day) {
        if (day.isWeekend())
            return 1;
        return 0;
    }
    */
    @Override
    public Set<AssistantType> getAllowedAssistantTypes() {
        return ALLOWED_ASSISTANT_TYPES;
    }

    @Override
    public ShiftType getType() {
        return ShiftType.TPWE;
    }
}
