package input.shift;

import java.util.Set;

public class EncodedShift {

    private Shift shift;
    private Set<Integer> allowedWeeks;
    private String codeName;
    private int balance;
    private int nbWeeks;


    public EncodedShift(Shift shift, Set<Integer> allowedWeeks, String codeName, int balance, int nbWeeks) {
        this.shift = shift;
        this.allowedWeeks = allowedWeeks;
        this.codeName = codeName;
        this.balance = balance;
        this.nbWeeks = nbWeeks;
    }


    public Shift getShift() {
        return shift;
    }

    public Set<Integer> getAllowedWeeks() {
        return allowedWeeks;
    }

    public String getCodeName() {
        return codeName;
    }

    public int getBalance() {
        return balance;
    }

    public int getDuration(){
        if(getShift().getType() == ShiftType.FREE){
            return 1;
        }
        // Balance delen door 2 want bij het achtereenzetten van 2 shiften wordt van beiden de balance opgeteld
        switch(getShift().getPeriod()){
            case WEEK -> {
                if(getAllowedWeeks().contains(1)) return 7 + (getBalance() / 2);
                else if(getAllowedWeeks().contains(2)) return Math.min(getBalance(), 7) + 7 + (getBalance() / 2);
                else if(getAllowedWeeks().contains(getNbWeeks() - 2)) return (getBalance() / 2) + 7 + Math.min(getBalance(), 7);
                else if(getAllowedWeeks().contains(getNbWeeks() - 1)) return (getBalance() / 2) + 7;
                else return (getBalance() / 2) * 2 + 7;
            }
            case WEEKEND -> {
                if(getAllowedWeeks().contains(1)) return 1 + 2 + (getBalance() / 2);
                else if(getAllowedWeeks().contains(2)) return Math.min(getBalance(), 9) + 2 + (getBalance() / 2);
                else if(getAllowedWeeks().contains(getNbWeeks() - 2)) return (getBalance() / 2) + 2 + Math.min(getBalance(), 11);
                else if(getAllowedWeeks().contains(getNbWeeks() - 1)) return (getBalance() / 2) + 2 + Math.min(getBalance(), 4);
                else return (getBalance() / 2) * 2 + 2;
            }
            case HOLIDAY, WEEKDAY -> {
                return 2 * (getBalance() / 2) + 1;
            }
        }
        return 30; // Kan hier nooit geraken normaal
    }

    public ShiftType[] getShiftTypes(){
        ShiftType[] result = new ShiftType[getDuration()];
        if(getShift().getType() == ShiftType.FREE){
            result[0] = ShiftType.FREE;
            return result;
        }
        switch(getShift().getPeriod()){
            case WEEK -> {
                return getWeekShiftTypes();
            }
            case WEEKEND -> {
                return getWeekendShiftTypes();
            }
            case HOLIDAY, WEEKDAY -> {

            }
        }
        return result;
    }

    private ShiftType[] getWeekShiftTypes(){
        ShiftType[] result = new ShiftType[getDuration()];
        if(getAllowedWeeks().contains(1)) {
            for (int i = 0; i < 7; i++) {
                result[i] = getShift().getType();
            }
            for (int i = 7; i < 7 + (getBalance() / 2); i++) {
                result[i] = ShiftType.FREE;
            }
        }
        else if(getAllowedWeeks().contains(2)) {
            int border = Math.min(getBalance(), 7);
            for(int i = 0; i < border; i++){
                result[i] = ShiftType.FREE;
            }
            for(int i = border; i < 7 + border; i++){
                result[i] = getShift().getType();
            }
            for(int i = border + 7; i < 7 + border + (getBalance() / 2); i++){
                result[i] = ShiftType.FREE;
            }
        }
        else if(getAllowedWeeks().contains(getNbWeeks() - 2)){
            for(int i = 0; i < (getBalance() / 2); i++){
                result[i] = ShiftType.FREE;
            }
            for(int i = (getBalance() / 2); i < 7 + (getBalance() / 2); i++){
                result[i] = getShift().getType();
            }
            for(int i = 7 + (getBalance() / 2); i < 7 + (getBalance() / 2) + Math.min(getBalance(), 7); i++){
                result[i] = ShiftType.FREE;
            }
        }
        else if(getAllowedWeeks().contains(getNbWeeks() - 1)){
            for (int i = 0; i < (getBalance() / 2); i++) {
                result[i] = ShiftType.FREE;
            }
            for (int i = (getBalance() / 2); i < 7 + (getBalance() / 2); i++) {
                result[i] = getShift().getType();
            }
        }
        else {
            for(int i = 0; i < (getBalance() / 2); i++){
                result[i] = ShiftType.FREE;
            }
            for(int i = (getBalance() / 2); i < 7 + (getBalance() / 2); i++){
                result[i] = getShift().getType();
            }
            for(int i = 7 + (getBalance() / 2); i < 7 + 2 * (getBalance() / 2); i++){
                result[i] = ShiftType.FREE;
            }
        }
        return result;
    }

    private ShiftType[] getWeekendShiftTypes(){
        ShiftType[] result = new ShiftType[getDuration()];
        if(getAllowedWeeks().contains(1)) {
            for (int i = 0; i < 1; i++) {
                result[i] = ShiftType.FREE;
            }
            for (int i = 1; i < 3; i++) {
                result[i] = getShift().getType();
            }
            for (int i = 3; i < 3 + (getBalance() / 2); i++) {
                result[i] = ShiftType.FREE;
            }
        }
        else if(getAllowedWeeks().contains(2)) {
            int border = Math.min(getBalance(), 9);
            for(int i = 0; i < border; i++){
                result[i] = ShiftType.FREE;
            }
            for(int i = border; i < 2 + border; i++){
                result[i] = getShift().getType();
            }
            for(int i = border + 2; i < 2 + border + (getBalance() / 2); i++){
                result[i] = ShiftType.FREE;
            }
        }
        else if(getAllowedWeeks().contains(getNbWeeks() - 2)){
            for(int i = 0; i < (getBalance() / 2); i++){
                result[i] = ShiftType.FREE;
            }
            for(int i = (getBalance() / 2); i < 2 + (getBalance() / 2); i++){
                result[i] = getShift().getType();
            }
            for(int i = 2 + (getBalance() / 2); i < 2 + (getBalance() / 2) + Math.min(getBalance(), 9); i++){
                result[i] = ShiftType.FREE;
            }
        }
        else if(getAllowedWeeks().contains(getNbWeeks() - 1)){
            for (int i = 0; i < (getBalance() / 2); i++) {
                result[i] = ShiftType.FREE;
            }
            for (int i = (getBalance() / 2); i < 2 + (getBalance() / 2); i++) {
                result[i] = getShift().getType();
            }
        }
        else {
            for(int i = 0; i < (getBalance() / 2); i++){
                result[i] = ShiftType.FREE;
            }
            for(int i = (getBalance() / 2); i < 2 + (getBalance() / 2); i++){
                result[i] = getShift().getType();
            }
            for(int i = 2 + (getBalance() / 2); i < 2 + 2 * (getBalance() / 2); i++){
                result[i] = ShiftType.FREE;
            }
        }
        return result;
    }

    public int getNbWeeks() {
        return nbWeeks;
    }
}
