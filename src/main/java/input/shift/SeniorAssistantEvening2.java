package input.shift;

import input.assistant.AssistantType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SeniorAssistantEvening2 extends Shift{

    private static final HashSet<AssistantType> ALLOWED_ASSISTANT_TYPES = new HashSet<>(
            Arrays.asList(
                    AssistantType.SA,
                    AssistantType.SA_NEO
            )
    );

    public SeniorAssistantEvening2(double dailyWorkload) {
        super(dailyWorkload);
    }

    @Override
    public ShiftPeriod getPeriod() {
        return ShiftPeriod.FRIDAY;
    }

    @Override
    public Set<AssistantType> getAllowedAssistantTypes() {
        return ALLOWED_ASSISTANT_TYPES;
    }

    @Override
    public ShiftType getType() {
        return ShiftType.SAEV2;
    }
}
