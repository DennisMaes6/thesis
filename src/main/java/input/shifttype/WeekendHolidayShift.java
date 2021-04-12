package input.shifttype;

public abstract class WeekendHolidayShift extends ShiftType {

    @Override
    public ShiftTypePeriod getSpanningPeriod() {
        return ShiftTypePeriod.WEEKEND_HOLIDAY;
    }
}
