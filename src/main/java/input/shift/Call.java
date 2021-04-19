package input.shift;

import input.assistant.AssistantType;
import input.time.Day;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Call extends WeekShift {

    private static final HashSet<AssistantType> ALLOWED_ASSISTANT_TYPES = new HashSet<>(
            Arrays.asList(
                    AssistantType.SA,
                    AssistantType.SA_F,
                    AssistantType.SA_NEO,
                    AssistantType.SA_F_NEO
            )
    );

    public Call(double workload) {
        super(workload);
    }

    @Override
    public int getCoverage(Day day) {
        return 1;
    }

    @Override
    public ShiftPeriod getPeriod() {
        return ShiftPeriod.WEEK;
    }

    @Override
    public Set<AssistantType> getAllowedAssistantTypes() {
        return ALLOWED_ASSISTANT_TYPES;
    }

    @Override
    public ShiftType getType() {
        return ShiftType.CALL;
    }
}
