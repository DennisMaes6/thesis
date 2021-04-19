import input.assistant.Assistant;
import input.shifttype.ShiftType;
import input.time.Day;

import java.util.List;

public class Swap {

    private final Assistant from;
    private final Assistant to;
    private final List<Day> days;
    private final ShiftType shiftType;
    private final double fairnessScore;

    Swap(Assistant from, Assistant to, List<Day> days, ShiftType shiftType, double fairnessScore) {
        this.from = from;
        this.to = to;
        this.days = days;
        this.shiftType = shiftType;
        this.fairnessScore = fairnessScore;
    }

    public Assistant getFrom() {
        return from;
    }

    public Assistant getTo() {
        return to;
    }

    public List<Day> getDays() {
        return days;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    public double getFairnessScore() {
        return fairnessScore;
    }



}
