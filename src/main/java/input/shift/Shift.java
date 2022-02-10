package input.shift;

import input.assistant.AssistantType;
import input.time.Day;

import java.util.HashMap;
import java.util.Set;

public abstract class Shift {

    private final double dailyWorkload;
    private int maxAssignments;
    
    private HashMap<Day, Integer> coverage;

    Shift(double dailyWorkload) {
        this.dailyWorkload = dailyWorkload;
        this.coverage = new HashMap<Day, Integer>();
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

    public int getCoverage(Day day) {
        return this.coverage.get(day);
    }

    public void setCoverage(Day day, int cov){
        this.coverage.put(day, cov);
    }

    public abstract ShiftPeriod getPeriod();

    public abstract Set<AssistantType> getAllowedAssistantTypes();

    public abstract ShiftType getType();
}
