package input.shift;

import input.assistant.AssistantType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FreeShift extends Shift {

    private static final HashSet<AssistantType> ALLOWED_ASSISTANT_TYPES = new HashSet<>(
            Arrays.asList(
                    AssistantType.JA,
                    AssistantType.JA_F,
                    AssistantType.SA,
                    AssistantType.SA_F,
                    AssistantType.SA_NEO,
                    AssistantType.SA_F_NEO,
                    AssistantType.FELLOWS
            )
    );

    public FreeShift(double dailyWorkload) {
        super(dailyWorkload);
    }


    @Override
    public ShiftPeriod getPeriod() {
        return null;
    }

    @Override
    public Set<AssistantType> getAllowedAssistantTypes() {
        return ALLOWED_ASSISTANT_TYPES;
    }

    @Override
    public ShiftType getType() {
        return ShiftType.FREE;
    }
}
