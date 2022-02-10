package input;

import input.shift.ShiftType;

public class ShiftTypeModelParameters {
    private final ShiftType shiftType;
    private final double workload;
    private final int coverage;
    private final int maxBuffer;

    public ShiftTypeModelParameters(ShiftType shiftType, float workload, int coverage, int maxBuffer) {
        this.shiftType = shiftType;
        this.workload = workload;
        this.coverage = coverage;
        this.maxBuffer = maxBuffer;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    public double getWorkload() {
        return workload;
    }
    public int getCoverage() {
        return coverage;
    }

    public int getMaxBuffer() {
        return maxBuffer;
    }
}
