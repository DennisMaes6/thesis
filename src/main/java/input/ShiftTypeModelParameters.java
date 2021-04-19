package input;

import input.shift.ShiftType;

public class ShiftTypeModelParameters {
    private final ShiftType shiftType;
    private final double workload;
    private final int maxBuffer;

    public ShiftTypeModelParameters(ShiftType shiftType, float workload, int maxBuffer) {
        this.shiftType = shiftType;
        this.workload = workload;
        this.maxBuffer = maxBuffer;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    public double getWorkload() {
        return workload;
    }

    public int getMaxBuffer() {
        return maxBuffer;
    }
}
