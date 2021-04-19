package input.shift;

public abstract class WeekendShift extends Shift {

    WeekendShift(double workload) {
        super(workload / 2.0);
    }

    @Override
    public ShiftPeriod getPeriod() {
        return ShiftPeriod.WEEKEND;
    }
}
