package shifttype;

import assistant.AssistantType;
import time.Day;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SeniorAssistantEveningWeek extends WeekShift {

    @Override
    public int getRequiredNbAssistants(Day day) {
        return 1;
    }

    @Override
    public Set<AssistantType> getAllowedAssistantTypes() {
        return new HashSet<>(Arrays.asList(
                AssistantType.SA,
                AssistantType.SA_N
            )
        );
    }

    @Override
    public ShiftTypeId getId() {
        return ShiftTypeId.SA_EW;
    }
}
