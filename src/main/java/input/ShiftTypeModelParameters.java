package input;

import input.shifttype.ShiftTypeId;

public class ShiftTypeModelParameters {
    private final ShiftTypeId shiftType;
    private final double workload;
    private final int maxBuffer;

    public ShiftTypeModelParameters(ShiftTypeId shiftType, float workload, int maxBuffer) {
        this.shiftType = shiftType;
        this.workload = workload;
        this.maxBuffer = maxBuffer;
    }

    public ShiftTypeId getShiftTypeId() {
        return shiftType;
    }

    public double getWorkload() {
        return workload;
    }

    public int getMaxBuffer() {
        return maxBuffer;
    }
}
