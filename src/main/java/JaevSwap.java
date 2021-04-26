import input.assistant.Assistant;
import input.shift.ShiftType;
import input.time.Day;

import java.util.List;

public class JaevSwap {

    private final Assistant from;
    private final Assistant to;
    private final Day day;
    private final double jaevFairnessScore;

    JaevSwap(Assistant from, Assistant to, Day day, double jaevFairnessScore) {
        this.from = from;
        this.to = to;
        this.day = day;
        this.jaevFairnessScore = jaevFairnessScore;
    }

    public Assistant getFrom() {
        return from;
    }

    public Assistant getTo() {
        return to;
    }

    public Day getDay() {
        return day;
    }

    public ShiftType getShiftType() {
        return ShiftType.JAEV;
    }

    public double getJaevFairnessScore() {
        return jaevFairnessScore;
    }

}