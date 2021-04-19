package input.shift;

import input.assistant.AssistantType;
import input.time.Day;

import java.util.Set;

public abstract class Shift {

    private final double workload;
    private int maxAssignments;

    Shift(double workload) {
        this.workload= workload;
    }

    public double getWorkload() {
        return this.workload;
    }

    public int getMaxAssignments() {
        return this.maxAssignments;
    }

    public void setMaxAssignments(int maxAssignments) {
        this.maxAssignments = maxAssignments;
    }

    public abstract int getCoverage(Day day);

    public abstract ShiftPeriod getPeriod();

    public abstract Set<AssistantType> getAllowedAssistantTypes();

    public abstract ShiftType getType();
}
