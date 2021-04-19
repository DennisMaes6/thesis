package input.shift;

import input.assistant.AssistantType;
import input.time.Day;

import java.util.Set;

public abstract class Shift {

    private final double dailyWorkload;
    private int maxAssignments;

    Shift(double dailyWorkload) {
        this.dailyWorkload = dailyWorkload;
    }

    public double getDailyWorkload() {
        return this.dailyWorkload;
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
