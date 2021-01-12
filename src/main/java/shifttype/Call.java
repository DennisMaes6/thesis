package shifttype;

import assistant.AssistantType;
import time.Day;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Call extends WeekShift {

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
        return new HashSet<>(Arrays.asList(
                AssistantType.SA,
                AssistantType.SA_F,
                AssistantType.SA_N,
                AssistantType.SA_F_N
            )
        );
    }

    @Override
    public ShiftTypeId getId() {
        return ShiftTypeId.C;
    }
}
