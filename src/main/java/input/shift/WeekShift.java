package input.shift;

public abstract class WeekShift extends Shift {

    WeekShift(double workload) {
        super(workload);
    }

    @Override
    public ShiftPeriod getPeriod() {
        return ShiftPeriod.WEEK;
    }
}
