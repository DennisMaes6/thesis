package input.shift;

public abstract class WeekShift extends Shift {

    WeekShift(double workload) {
        super(workload / 7.0);
    }

    @Override
    public ShiftPeriod getPeriod() {
        return ShiftPeriod.WEEK;
    }
}
