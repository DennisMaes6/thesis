package algorithm;

import assistant.Assistant;
import shifttype.ShiftType;
import time.Week;

import java.util.List;
import java.util.Set;

public class AlgorithmInput {

    private final List<Assistant> assistants;

    private final List<Week> weeks;

    private final Set<ShiftType> shiftTypes;

    public AlgorithmInput(List<Assistant> assistants, List<Week> weeks, Set<ShiftType> shiftTypes) {
        this.assistants = assistants;
        this.weeks = weeks;
        this.shiftTypes = shiftTypes;
    }


    public List<Assistant> getAssistants() {
        return assistants;
    }

    public List<Week> getWeeks() {
        return weeks;
    }

    public Set<ShiftType> getShiftTypes() {
        return shiftTypes;
    }
}
