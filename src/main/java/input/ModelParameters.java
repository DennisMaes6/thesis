package input;

import java.util.List;

public class ModelParameters {

    private final int minBalance;
    private final int minBalanceJaev;
    private final List<ShiftTypeModelParameters> shiftTypeModelParameters;

    public ModelParameters(int minBalance, int minBalanceJaev, List<ShiftTypeModelParameters> shiftTypeModelParameters) {
        this.minBalance = minBalance;
        this.minBalanceJaev = minBalanceJaev;
        this.shiftTypeModelParameters = shiftTypeModelParameters;
    }

    public int getMinBalance() {
        return minBalance;
    }

    public int getMinBalanceJaev() {
        return minBalanceJaev;
    }

    public List<ShiftTypeModelParameters> getShiftTypeModelParameters() {
        return shiftTypeModelParameters;
    }
}
