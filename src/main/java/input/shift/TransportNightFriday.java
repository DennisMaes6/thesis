package input.shift;

import input.assistant.AssistantType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class TransportNightFriday extends Shift {

    private static final HashSet<AssistantType> ALLOWED_ASSISTANT_TYPES = new HashSet<>(
            Arrays.asList(
                    AssistantType.SA_NEO,
                    AssistantType.SA_F_NEO,
                    AssistantType.FELLOWS
            )
    );


    public TransportNightFriday(double dailyWorkload) {
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
        return ShiftType.TPNF;
    }
}
