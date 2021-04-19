package input.shift;

public abstract class HolidayShift extends Shift {

    public HolidayShift(double workload) {
        super(workload);
    }

    @Override
    public ShiftPeriod getPeriod() {
        return ShiftPeriod.HOLIDAY;
    }
}
