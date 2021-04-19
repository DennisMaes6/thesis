package input.shifttype;

public abstract class WeekendShift extends Shift {

    WeekendShift(double workload) {
        super(workload);
    }

    @Override
    public ShiftPeriod getPeriod() {
        return ShiftPeriod.WEEKEND;
    }
}
