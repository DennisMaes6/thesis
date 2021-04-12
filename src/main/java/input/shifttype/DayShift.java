package input.shifttype;

public abstract class DayShift extends ShiftType {

    @Override
    public ShiftTypePeriod getSpanningPeriod() {
        return ShiftTypePeriod.DAY;
    }
}
