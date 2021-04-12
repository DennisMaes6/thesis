package input.shifttype;

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

    @Override
    public int getRequiredNbAssistants(Day day) {
        return 1;
    }

    @Override
    public ShiftTypePeriod getSpanningPeriod() {
        return ShiftTypePeriod.WEEK;
    }

    @Override
    public Set<AssistantType> getAllowedAssistantTypes() {
        return ALLOWED_ASSISTANT_TYPES;
    }

    @Override
    public ShiftTypeId getId() {
        return ShiftTypeId.CALL;
    }
}
