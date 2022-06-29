package input;

import java.util.List;

public class ModelParameters {

    private final int minBalance;
    private final int minBalanceJaev;
    private final List<ShiftTypeModelParameters> shiftTypeModelParameters;
    private List<Double> weightParameters;

    public ModelParameters(int minBalance, int minBalanceJaev, List<ShiftTypeModelParameters> shiftTypeModelParameters) {
        this.minBalance = minBalance;
        this.minBalanceJaev = minBalanceJaev;
        this.shiftTypeModelParameters = shiftTypeModelParameters;
    }

    public ModelParameters(int minBalance, int minBalanceJaev, List<ShiftTypeModelParameters> shiftTypeModelParameters, List<Double> weightParams) {
        this.minBalance = minBalance;
        this.minBalanceJaev = minBalanceJaev;
        this.shiftTypeModelParameters = shiftTypeModelParameters;
        this.weightParameters = weightParams;
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

    public List<Double> getWeightParameters() {
        return weightParameters;
    }

    public Double getCoverageParameter(){
        return getWeightParameters().get(0);
    }

    public Double getBalanceParameter(){
        return getWeightParameters().get(1);
    }

    public Double getFairnessParameter(){
        return getWeightParameters().get(2);
    }
}
