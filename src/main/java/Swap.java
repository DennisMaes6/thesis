import input.assistant.Assistant;
import input.shift.ShiftType;
import input.time.Day;

import java.util.List;

public class Swap {

    private final Assistant from;
    private final Assistant to;
    private final List<Day> days;
    private final ShiftType shiftType;
    private final double fairnessScore;
    private final double balanceScore;

    Swap(Assistant from, Assistant to, List<Day> days, ShiftType shiftType, double fairnessScore, double balanceScore) {
        this.from = from;
        this.to = to;
        this.days = days;
        this.shiftType = shiftType;
        this.fairnessScore = fairnessScore;
        this.balanceScore = balanceScore;
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

    public double getBalanceScore() {
        return balanceScore;
    }

}
