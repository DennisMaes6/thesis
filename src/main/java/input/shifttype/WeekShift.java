package input.shifttype;

public abstract class WeekShift extends ShiftType {

    @Override
    public ShiftTypePeriod getSpanningPeriod() {
        return ShiftTypePeriod.WEEK;
    }
}