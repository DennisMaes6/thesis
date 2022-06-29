import input.InstanceData;
import input.ModelParameters;
import input.ShiftTypeModelParameters;
import input.assistant.Assistant;
import input.assistant.AssistantType;
import input.shift.*;
import input.time.Day;
import input.time.Week;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static input.shift.ShiftType.*;

public class WeeklySchedule {


    private final InstanceData data;
    private final ModelParameters parameters;
    private Map<Assistant, List<Shift>> scheduleWeeks;
    private Map<ShiftType, Shift> shifts;


    public WeeklySchedule(InstanceData data, ModelParameters parameters){
        this.data = data;
        this.parameters = parameters;
        scheduleWeeks = new HashMap<>();
        shifts = new HashMap<>();
        initShifts(parameters.getShiftTypeModelParameters());
        initSchedule();
    }

    public void assignShift(Assistant assistant, Shift shift, int weekNb){

        List<Shift> currentShifts = getScheduleWeeks().get(assistant);
        currentShifts.set(weekNb, shift);
        getScheduleWeeks().put(assistant, currentShifts);

    }

    public List<Assistant> getAssistants(){
        return data.getAssistants();
    }

    public Map<Assistant, List<Shift>> getScheduleWeeks() {
        return scheduleWeeks;
    }

    public InstanceData getData(){
        return data;
    }

    public ModelParameters getParameters() {
        return parameters;
    }

    public Map<ShiftType, Shift> getShifts() {
        return shifts;
    }

    public double getTotalWorkloadForAssistant(Assistant assistant){
        double result = 0.0;
        List<Shift> assistantShifts = getScheduleWeeks().get(assistant);
        for(Shift shift : assistantShifts){
            result += shift.getDailyWorkload();
        }
        return result;
    }

    public void printWorkloads(){
        for(Assistant assistant : getAssistants()){
            System.out.println( "Workload for " + assistant.getName() + ": " + getTotalWorkloadForAssistant(assistant));
        }
    }

    private void initSchedule(){
        for(Assistant assistant : getAssistants()){
            List<Shift> assistantShifts = new ArrayList<>();
            for(Week week : data.getWeeks()){
                assistantShifts.add(getShifts().get(ShiftType.FREE));
            }

            getScheduleWeeks().put(assistant, assistantShifts);
        }
        //System.out.println(getScheduleWeeks());
    }

    private void initShifts(List<ShiftTypeModelParameters> stps) {
        initializeFreeShift();

        for (ShiftTypeModelParameters stp : stps) {
            switch (stp.getShiftType()) {

                case SANW -> initializeJuniorAssistantNightWeekShift(stp.getWorkload(), stp.getCoverage());
                case JAWE -> initializeJuniorAssistantWeekendShift(stp.getWorkload(), stp.getCoverage());
                case JAHO -> initializeJuniorAssistantHolidayShift(stp.getWorkload(), stp.getCoverage());
                case SAEW -> initializeSeniorAssistantEveningWeekShift(stp.getWorkload(), stp.getCoverage());
                case SAWE -> initializeSeniorAssistantWeekendShift(stp.getWorkload(), stp.getCoverage());
                case SAHO -> initializeSeniorAssistantHolidayShift(stp.getWorkload(), stp.getCoverage());
                case TPWE -> initializeTransportWeekendShift(stp.getWorkload(), stp.getCoverage());
                case TPHO -> initializeTransportHolidayShift(stp.getWorkload(), stp.getCoverage());
                //case CALL -> initializeCallShift(stp.getWorkload(), stp.getCoverage());
                case JAEV -> initializeJuniorAssistantEvening(stp.getWorkload(), stp.getCoverage());
                case TPNF -> initializeTransportNightFriday(stp.getWorkload(), stp.getCoverage());
                case SAEV1 -> initializeSeniorEveningMondayThursday(stp.getWorkload(), stp.getCoverage());
                case SAEV2 -> initializeSeniorEveningFriday(stp.getWorkload(), stp.getCoverage());
            }
        }
    }

    private void initializeJuniorAssistantEvening(double workload, int coverage){
        JuniorAssistantEvening newShift = new JuniorAssistantEvening(workload);
        for(Day d : this.data.getDays()){
            if(!d.isHoliday() ){
                newShift.setCoverage(d, coverage);
            } else {
                newShift.setCoverage(d, 0);
            }
        }
        getShifts().put(newShift.getType(), newShift);
    }

    private void initializeSeniorEveningFriday(double workload, int coverage){
        SeniorAssistantEvening2 newShift = new SeniorAssistantEvening2(workload);
        for(Day d : this.data.getDays()){
            if(d.getDay0fWeek() == "Fri" && !d.isHoliday() ){
                newShift.setCoverage(d, coverage);
            } else {
                newShift.setCoverage(d, 0);
            }
        }
        this.shifts.put(SAEV2, newShift);
    }

    private void initializeSeniorEveningMondayThursday(double workload, int coverage){
        SeniorAssistantEvening1 newShift = new SeniorAssistantEvening1(workload);
        for(Day d : this.data.getDays()){
            if(d.isHoliday() || d.isWeekend() || d.getDay0fWeek() == "Fri" ){
                newShift.setCoverage(d, 0);
            } else {
                newShift.setCoverage(d, coverage);
            }
        }
        this.shifts.put(SAEV1, newShift);
    }

    private void initializeTransportNightFriday(double workload, int coverage){
        TransportNightFriday newShift = new TransportNightFriday(workload);
        for(Day d : this.data.getDays()){
            if(d.getDay0fWeek() == "Fri" && !d.isHoliday() ){
                newShift.setCoverage(d, coverage);
            } else {
                newShift.setCoverage(d, 0);
            }
        }
        this.shifts.put(TPNF, newShift);
    }

    private void initializeFreeShift() {
        FreeShift freeShift = new FreeShift(0);
        getShifts().put(freeShift.getType(), freeShift);
    }



    private void initializeTransportHolidayShift(double workload, int coverage) {
        TransportHoliday newShift = new TransportHoliday(workload);
        for(Day d : this.data.getDays()){
            if(d.isHoliday()){
                newShift.setCoverage(d, coverage);
            } else {
                newShift.setCoverage(d, 0);
            }
        }
        getShifts().put(newShift.getType(), newShift);
    }

    private void initializeTransportWeekendShift(double workload, int coverage) {
        TransportWeekend newShift = new TransportWeekend(workload);
        for(Day d : this.data.getDays()){
            if(!d.isHoliday() && d.isWeekend()){
                newShift.setCoverage(d, coverage);
            } else {
                newShift.setCoverage(d, 0);
            }
        }
        getShifts().put(newShift.getType(), newShift);
    }

    private void initializeSeniorAssistantHolidayShift(double workload, int coverage) {
        SeniorAssistantHoliday newShift = new SeniorAssistantHoliday(workload);
        for(Day d : this.data.getDays()){
            if(d.isHoliday()){
                newShift.setCoverage(d, coverage);
            } else {
                newShift.setCoverage(d, 0);
            }
        }
        getShifts().put(newShift.getType(), newShift);
    }

    private void initializeSeniorAssistantWeekendShift(double workload, int coverage) {
        SeniorAssistantWeekend newShift = new SeniorAssistantWeekend(workload);
        for(Day d : this.data.getDays()){
            if(d.isWeekend() && !d.isHoliday()){
                newShift.setCoverage(d, coverage);
            } else {
                newShift.setCoverage(d, 0);
            }
        }
        getShifts().put(newShift.getType(), newShift);
    }

    private void initializeSeniorAssistantEveningWeekShift(double workload, int coverage) {
        SeniorAssistantEveningWeek newShift = new SeniorAssistantEveningWeek(workload);
        for(Day d : this.data.getDays()){
            if(!d.isHoliday()){
                newShift.setCoverage(d, coverage);
            } else {
                newShift.setCoverage(d, 0);
            }
        }
        getShifts().put(newShift.getType(), newShift);
    }

    private void initializeJuniorAssistantHolidayShift(double workload, int coverage) {
        JuniorAssistantHoliday newShift = new JuniorAssistantHoliday(workload);
        for(Day d : this.data.getDays()){
            if(d.isHoliday()){
                newShift.setCoverage(d, coverage);
            } else {
                newShift.setCoverage(d, 0);
            }
        }
        getShifts().put(newShift.getType(), newShift);
    }

    private void initializeJuniorAssistantWeekendShift(double workload, int coverage) {
        JuniorAssistantWeekend newShift = new JuniorAssistantWeekend(workload);
        for(Day d : this.data.getDays()){
            if(!d.isHoliday() && d.isWeekend()){
                newShift.setCoverage(d, coverage);
            } else {
                newShift.setCoverage(d, 0);
            }
        }
        getShifts().put(newShift.getType(), newShift);
    }

    private void initializeJuniorAssistantNightWeekShift(double workload, int coverage) {
        SeniorAssistantNightWeek newShift = new SeniorAssistantNightWeek(workload);
        for(Day d : this.data.getDays()){
            if(!d.isHoliday()){
                newShift.setCoverage(d, coverage);
            } else {
                newShift.setCoverage(d, 0);
            }
        }
        getShifts().put(newShift.getType(), newShift);
    }


    public double getBalanceScore(){
        double result = 0.0;
        for(Assistant assistant : getAssistants()){
            List<Shift> currentShifts = getScheduleWeeks().get(assistant);
            int streak = 0;

            for(int i = 0; i < currentShifts.size() - 1; i++)
                if(currentShifts.get(i).getType() != ShiftType.FREE && currentShifts.get(i + 1).getType() != ShiftType.FREE){
                    result += 1;
                    streak += 1;
                    result += streak;
                } else {
                    streak = 0;
                }
            }
        return result;
    }

    public double getFairnessScore(){
        double best = 0;
        double worst = 100000;
        for(Assistant assistant : getAssistants()){
            double currentTotalWL = getScheduleWeeks().get(assistant)
                    .stream().
                    filter( s -> s.getType() != ShiftType.FREE)
                    .mapToDouble(Shift::getDailyWorkload)
                    .sum();
            currentTotalWL = currentTotalWL /  ( getData().getDays().size() - assistant.getFreeDayIds().size() ); // Normaliseren vakantiedagen
            if(currentTotalWL > best){
                best = currentTotalWL;
            } else if(currentTotalWL < worst){
                worst = currentTotalWL;
            }
        }
        return (best - worst);
    }

    public double getWorstFairness(){
        double highestWL = getShifts().values().stream().map(s -> s.getDailyWorkload()).max(Double::compare).get();
        return highestWL * getData().getWeeks().size();
    }

    public int getTotalNbShifts(){

        int count = 0;

        int nbHolidays = getNbHolidays();
        int nbWeeks = getData().getWeeks().size();

        for(ShiftTypeModelParameters stmp : getParameters().getShiftTypeModelParameters()){
            if(getShifts().get(stmp.getShiftType()).getPeriod() == ShiftPeriod.HOLIDAY){
                count += nbHolidays * stmp.getCoverage();
            } else {
                count += nbWeeks * stmp.getCoverage();
            }
        }
        return count;

    }

    public int getAssignedShiftsTotal(){
        int count = 0;
        for(Assistant assistant : getAssistants()){
            for(Shift shift : getScheduleWeeks().get(assistant)){
                if(shift.getType() != FREE){
                    count += 1;
                }
            }
        }
        return count;
    }

    private int getNbHolidays(){
        int count = 0;
        for(Day day : getData().getDays()){
            if(day.isHoliday()){
                count += 1;
            }
        }
        return count;
    }

}
