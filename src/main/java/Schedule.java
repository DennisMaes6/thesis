import exceptions.BadInstanceException;
import exceptions.NotSolvableException;
import input.InstanceData;
import input.ShiftTypeModelParameters;
import input.assistant.Assistant;
import exceptions.InvalidDayException;
import exceptions.InvalidShiftTypeException;
import input.ModelParameters;
import input.shift.*;
import input.time.Day;
import input.time.Week;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.*;
import java.util.stream.Collectors;

import static input.shift.ShiftType.*;

public class Schedule {

    private final InstanceData data;
    private final ModelParameters parameters;
    private final Map<ShiftType, Shift> shifts = new HashMap<>();
    public final ShiftType[][] schedule;
    private final JuniorAssistantEvening jaevShift;

    public Schedule(InstanceData data, ModelParameters parameters) throws NotSolvableException {
        this.data = data;
        this.parameters = parameters;
        initializeShifts(parameters.getShiftTypeModelParameters());
        this.schedule = new ShiftType[getNbAssistants()][getNbDays()];
        this.jaevShift = new JuniorAssistantEvening(1.0);
        initializeJaevShift(1, 1);

        for (int i = 0; i < getNbAssistants(); i++) {
            data.getAssistants().get(i).setIndex(i);
        }

        for (int j = 0; j < getNbDays(); j ++) {
            data.getDays().get(j).setIndex(j);
        }

        for (int i = 0; i < getNbAssistants(); i++) {
            for (int j = 0; j < getNbDays(); j++) {
                schedule[i][j] = FREE;
            }
        }
        /*
        if (!isSolvable()) {
            throw new NotSolvableException("not solvable!");
        } */
    }

    public Schedule(InstanceData data, ModelParameters parameters, List<Triplet<Integer, Integer, ShiftType>> assignments){
        this.data = data;
        this.parameters = parameters;
        this.schedule = new ShiftType[getNbAssistants()][getNbDays()];
        this.jaevShift = new JuniorAssistantEvening(1.0);
        initializeShifts(parameters.getShiftTypeModelParameters());
        
        initializeJaevShift(1, 1); // TODO: niet hardcoden

        fillScheduleFromInput(assignments);

    }


    private void fillScheduleFromInput(List<Triplet<Integer, Integer, ShiftType>> assignments){
        HashMap<Integer, Integer> idToIndex = new HashMap<>();
        for (int i = 0; i < getNbAssistants(); i++) {
            data.getAssistants().get(i).setIndex(i);
            idToIndex.put(data.getAssistants().get(i).getId(), i);
        }
        HashMap<Integer, Integer> dayToIndex = new HashMap<>();
        for (int j = 0; j < getNbDays(); j ++) {
            data.getDays().get(j).setIndex(j);
            dayToIndex.put(data.getDays().get(j).getId(), j);
        }

        // Triplet: (assistant_id, day_nb, shift_type)
        for(Triplet<Integer, Integer, ShiftType> assignment : assignments){
            int currentAssistantIndex = idToIndex.get(assignment.getValue0());
            int currentDayNb = dayToIndex.get(assignment.getValue1());
            ShiftType curShiftType = assignment.getValue2();
            this.schedule[currentAssistantIndex][currentDayNb] = curShiftType;
        }


    }

    private void initializeShifts(List<ShiftTypeModelParameters> stps) {
        for (ShiftTypeModelParameters stp : stps) {
            switch (stp.getShiftType()) {

                case JANW -> initializeJuniorAssistantNightWeekShift(stp.getWorkload(), stp.getCoverage());
                case JAWE -> initializeJuniorAssistantWeekendShift(stp.getWorkload(), stp.getCoverage());
                case JAHO -> initializeJuniorAssistantHolidayShift(stp.getWorkload(), stp.getCoverage());
                case SAEW -> initializeSeniorAssistantEveningWeekShift(stp.getWorkload(), stp.getCoverage());
                case SAWE -> initializeSeniorAssistantWeekendShift(stp.getWorkload(), stp.getCoverage());
                case SAHO -> initializeSeniorAssistantHolidayShift(stp.getWorkload(), stp.getCoverage());
                case TPWE -> initializeTransportWeekendShift(stp.getWorkload(), stp.getCoverage());
                case TPHO -> initializeTransportHolidayShift(stp.getWorkload(), stp.getCoverage());
                case CALL -> initializeCallShift(stp.getWorkload(), stp.getCoverage());
                
                //case JANW -> this.shifts.put(JANW, new JuniorAssistantNightWeek(stp.getWorkload()));
                //case JAWE -> this.shifts.put(JAWE, new JuniorAssistantWeekend(stp.getWorkload()));
                //case JAHO -> this.shifts.put(JAHO, new JuniorAssistantHoliday(stp.getWorkload()));
                //case SAEW -> this.shifts.put(SAEW, new SeniorAssistantEveningWeek(stp.getWorkload()));
                //case SAWE -> this.shifts.put(SAWE, new SeniorAssistantWeekend(stp.getWorkload()));
                //case SAHO -> this.shifts.put(SAHO, new SeniorAssistantHoliday(stp.getWorkload()));
                //case TPWE -> this.shifts.put(TPWE, new TransportWeekend(stp.getWorkload()));
                //case TPHO -> this.shifts.put(TPHO, new TransportHoliday(stp.getWorkload()));
                //case CALL -> this.shifts.put(CALL, new Call(stp.getWorkload()));
            }
        }
        initMaxAssignments(stps);
    }

    private void initializeJaevShift(double workload, int coverage){

        for(Day d : this.data.getDays()){
            if(!d.isHoliday() && !d.isWeekend()){
                this.jaevShift.setCoverage(d, coverage);
            } else {
                this.jaevShift.setCoverage(d, 0);
            }    
        }

    }
    private void initializeJuniorAssistantNightWeekShift(double workload, int coverage){
        JuniorAssistantNightWeek newShift = new JuniorAssistantNightWeek(workload);
        for(Day d : this.data.getDays()){
            newShift.setCoverage(d, coverage);
            /*
            if(!d.isHoliday() ){
            //if(!d.isHoliday() && !d.isWeekend()){
                newShift.setCoverage(d, coverage);
            } else {
                newShift.setCoverage(d, 0);
            }     */
        }
        this.shifts.put(JANW, newShift);
    }
    private void initializeJuniorAssistantWeekendShift(double workload, int coverage){
        JuniorAssistantWeekend newShift = new JuniorAssistantWeekend(workload);
        for(Day d : this.data.getDays()){
            if(!d.isHoliday() && d.isWeekend()){
                newShift.setCoverage(d, coverage);
            } else {
                newShift.setCoverage(d, 0);
            }    
        }
        this.shifts.put(JAWE, newShift);
    }
    private void initializeJuniorAssistantHolidayShift(double workload, int coverage){
        JuniorAssistantHoliday newShift = new JuniorAssistantHoliday(workload);
        for(Day d : this.data.getDays()){
            if(d.isHoliday()){
                newShift.setCoverage(d, coverage);
            } else {
                newShift.setCoverage(d, 0);
            }    
        }
        this.shifts.put(JAHO, newShift);
    }
    private void initializeSeniorAssistantEveningWeekShift(double workload, int coverage){
        SeniorAssistantEveningWeek newShift = new SeniorAssistantEveningWeek(workload);        
        for(Day d : this.data.getDays()){
            newShift.setCoverage(d, coverage);
            /*
            if(!d.isHoliday() ){
            //if(!d.isHoliday() && !d.isWeekend()){
                newShift.setCoverage(d, coverage);
            } else {
                newShift.setCoverage(d, 0);
            }    */
        } 
        this.shifts.put(SAEW, newShift);
    }
    private void initializeSeniorAssistantWeekendShift(double workload, int coverage){
        SeniorAssistantWeekend newShift = new SeniorAssistantWeekend(workload);
        for(Day d : this.data.getDays()){
            if(d.isWeekend() && !d.isHoliday()){
                newShift.setCoverage(d, coverage);
            } else {
                newShift.setCoverage(d, 0);
            }    
        }
        this.shifts.put(SAWE, newShift);
    }
    private void initializeSeniorAssistantHolidayShift(double workload, int coverage){
        SeniorAssistantHoliday newShift = new SeniorAssistantHoliday(workload);
        for(Day d : this.data.getDays()){
            if(d.isHoliday()){
                newShift.setCoverage(d, coverage);
            } else {
                newShift.setCoverage(d, 0);
            }    
        }
        this.shifts.put(SAHO, newShift);
    }


    private void initializeTransportWeekendShift(double workload, int coverage){
        TransportWeekend newShift = new TransportWeekend(workload);
        for(Day d : this.data.getDays()){
            if(!d.isHoliday() && d.isWeekend()){
                newShift.setCoverage(d, coverage);
            } else {
                newShift.setCoverage(d, 0);
            }    
        }
        this.shifts.put(TPWE, newShift);
    }
    private void initializeTransportHolidayShift(double workload, int coverage){
        TransportHoliday newShift = new TransportHoliday(workload);
        for(Day d : this.data.getDays()){
            if(d.isHoliday()){
                newShift.setCoverage(d, coverage);
            } else {
                newShift.setCoverage(d, 0);
            }    
        }
        this.shifts.put(TPHO, newShift);
    }
    private void initializeCallShift(double workload, int coverage){
        Call newShift = new Call(workload);
        for(Day d : this.data.getDays()){
            if(!d.isHoliday() && !d.isWeekend()){
                newShift.setCoverage(d, coverage);
            } else {
                newShift.setCoverage(d, 0);
            }    
        }
        this.shifts.put(CALL, newShift);
    }


    private void initMaxAssignments(List<ShiftTypeModelParameters> stps) {
        for (ShiftTypeModelParameters stp : stps) {
            Shift shift = this.shifts.get(stp.getShiftType());
            shift.setMaxAssignments(
                    (int) Math.round(Math.ceil(
                            (double) data.getWeeks().size() / (double) getAllowedAssistants(shift).size()
                    ))
                    + stp.getMaxBuffer()
            );
        }
    }

    public Map<ShiftType, Shift> getShifts() {
        return shifts;
    }

    public InstanceData getData() {
        return data;
    }

    public boolean isSolvable() {
        for (Week week : data.getWeeks()) {
            Set<Assistant> assistants = data.getAssistants()
                    .stream()
                    .filter(a -> a.availableOn(week.getDays()))
                    .collect(Collectors.toSet());

            for (ShiftType shiftType : List.of(JANW, SAEW, CALL)) {
                List<Assistant> allowedAssistants = assistants
                        .stream()
                        .filter(a -> shifts.get(shiftType).getAllowedAssistantTypes().contains(a.getType()))
                        .collect(Collectors.toList());
                if (allowedAssistants.size() < shifts.get(shiftType).getCoverage(week.getDays().get(0))) {
                    return false;
                }
                for (int i = 0; i < shifts.get(shiftType).getCoverage(week.getDays().get(0)); i++) {
                    assistants.remove(allowedAssistants.get(i));
                }
            }

            for (ShiftType shiftType : List.of(JAWE, TPWE, SAWE)) {
                List<Assistant> allowedAssistants = assistants
                        .stream()
                        .filter(a -> shifts.get(shiftType).getAllowedAssistantTypes().contains(a.getType()))
                        .collect(Collectors.toList());
               
                if(!week.getWeekendDays().isEmpty()){
                    if (allowedAssistants.size() < shifts.get(shiftType).getCoverage(week.getWeekendDays().get(0))) {
                        return false;
                    }
                    
                    for (int i = 0; i < shifts.get(shiftType).getCoverage(week.getWeekendDays().get(0)); i++) {
                        assistants.remove(allowedAssistants.get(i));
                    }
                }
            }

            for (Day day : week.getHolidays()) {
                for (ShiftType shiftType : List.of(JAHO, TPHO, SAHO)) {
                    List<Assistant> allowedAssistants = assistants
                            .stream()
                            .filter(a -> shifts.get(shiftType).getAllowedAssistantTypes().contains(a.getType()))
                            .collect(Collectors.toList());
                    if (allowedAssistants.size() < shifts.get(shiftType).getCoverage(day)) {
                        return false;
                    }
                    for (int i = 0; i < shifts.get(shiftType).getCoverage(day); i++) {
                        assistants.remove(allowedAssistants.get(i));
                    }
                }
            }

            for (Day day : week.getDays().stream().filter(d -> ! d.isWeekend() && !d.isHoliday()).collect(Collectors.toList())) {
                List<Assistant> allowedAssistants = assistants
                        .stream()
                        .filter(a -> jaevShift.getAllowedAssistantTypes().contains(a.getType()))
                        .collect(Collectors.toList());
                if (allowedAssistants.size() < jaevShift.getCoverage(day)) {
                    return false;
                }
                for (int i = 0; i < jaevShift.getCoverage(day); i++) {
                    assistants.remove(allowedAssistants.get(i));
                }
            }
        }

        return true;
    }

    // First version: simply count the nb of shifts that haven't been assigned or are assigned too much.
    public Double getFitnessScore(){
        double result = 0.0;
        for(Day day : this.data.getDays()){
            for(Shift sh : this.getShifts().values()){
                result += Math.abs( sh.getCoverage(day) - nbAssignmentsOfShiftTypeOn(day, sh.getType()));
            }
        }

        return result;
    }

    // optimization objective
    public double fairnessScore() {
        List<Double> workloadPerAssistant = new ArrayList<>();
        for (Assistant assistant : data.getAssistants()) {
            
            workloadPerAssistant.add(workloadForAssistant(assistant));
        }
        return Collections.max(workloadPerAssistant) - Collections.min(workloadPerAssistant)
                + 0.001 * workloadPerAssistant.stream().filter(w -> w.equals(Collections.max(workloadPerAssistant))).count();
    }

    double jaevFairnessScore() {
        List<Double> jaevWorkloadPerAssistant = new ArrayList<>();
        for (Assistant assistant : getAllowedAssistants(JAEV)) {
            jaevWorkloadPerAssistant.add(jaevWorkloadForAssistant(assistant));
        }
        return Collections.max(jaevWorkloadPerAssistant) - Collections.min(jaevWorkloadPerAssistant);
    }

    public double workloadForAssistant(Assistant assistant) {
        if (daysActive(assistant) == 0) {
            return 0;
        }

        return Arrays.stream(schedule[assistant.getIndex()])
                .filter(st -> st != JAEV)
                .map(this::workload)
                .reduce(0.0, Double::sum) / daysActive(assistant);
    }

    private double jaevWorkloadForAssistant(Assistant assistant) {
        if (daysActive(assistant) == 0) {
            return 0;
        }
        return Arrays.stream(schedule[assistant.getIndex()])
                .filter(st -> st == JAEV)
                .map(this::workload)
                .reduce(0.0, Double::sum) / daysActive(assistant);
    }

    private double workload(ShiftType shiftType) {
        if (shiftType == FREE)
            return 0.0;
        else {
            //return 1.0;
            
            if (shiftType == JAEV) {
                return this.jaevShift.getDailyWorkload();      
            }
   
            return shifts.get(shiftType).getDailyWorkload(); 
        }
    }

    private int daysActive(Assistant assistant) {
        return getNbDays() - assistant.getFreeDayIds().size();
    }

    private int getNbAssistants() {
        return data.getAssistants().size();
    }

    private int getNbDays() {
        return data.getDays().size();
    }

    public double balanceScore() {
        List<Integer> idleStreaks = new ArrayList<>();
        for (int i = 0; i < getNbAssistants(); i++) {
            boolean idleStreak = false;
            int startDay = 0;
            boolean afterFirst = false;
            for (int j = 0; j < getNbDays(); j++) {
                if (afterFirst) {
                    if (!idleStreak && (schedule[i][j] == FREE || schedule[i][j] == JAEV)) {
                        idleStreak = true;
                        startDay = j;
                    }

                    if (idleStreak && (schedule[i][j] != FREE && schedule[i][j] != JAEV)) {
                        idleStreaks.add(j - startDay);
                        startDay = 0;
                        idleStreak = false;
                    }
                }

                if (!afterFirst && (schedule[i][j] != FREE && schedule[i][j] != JAEV)) {
                    afterFirst = true;
                }
            }
        }

        return (Collections.min(idleStreaks) - idleStreaks.stream().filter(b -> b.equals(Collections.min(idleStreaks))).count() / 100.0);
    }

    public int jaevBalanceScore() {
        List<Integer> idleStreaks = new ArrayList<>();
        for (Assistant assistant : getAllowedAssistants(JAEV)) {
            boolean idleStreak = false;
            int startDay = 0;
            boolean afterFirst = false;
            for (int j = 0; j < getNbDays(); j++) {
                if (afterFirst) {
                    if (!idleStreak && schedule[assistant.getIndex()][j] != JAEV) {
                        idleStreak = true;
                        startDay = j;
                    }

                    if (idleStreak && schedule[assistant.getIndex()][j] == JAEV) {
                        idleStreaks.add(j - startDay);
                        startDay = 0;
                        idleStreak = false;
                    }
                }

                if (!afterFirst && schedule[assistant.getIndex()][j] == JAEV) {
                    afterFirst = true;
                }
            }
        }
        try {
            return Collections.min(idleStreaks);
        } catch (NoSuchElementException e) {
            return 0;
        }

    }

    private int nbFreeDaysBefore(Assistant assistant, Day day) {
        int count = 0;
        for (int j = day.getIndex()-1; j >= 0; j--) {
            if (schedule[assistant.getIndex()][j] == FREE) {
                count++;
            } else {
                return count;
            }
        }
        return getNbDays(); // if no assignment before this one
    }

    private int nbFreeDaysAfter(Assistant assistant, Day day) {
        int count = 0;
        for (int j = day.getIndex()+1; j < getNbDays(); j++) {
            if (schedule[assistant.getIndex()][j] == FREE) {
                count++;
            } else {
                return count;
            }
        }
        return getNbDays(); // if no assignment after this one
    }

    public void assign(Assistant assistant, List<Day> days, Shift shift) throws InvalidDayException, InvalidShiftTypeException {
        // all hard constraints
        if (!shift.getAllowedAssistantTypes().contains(assistant.getType())) {
            throw new InvalidShiftTypeException("Shift type not allowed for assistant");
        }

        for (Day day : days) {
            if (assistant.getFreeDayIds().contains(day.getId())) {
                throw new InvalidDayException("Cannot assign on a free day");
            }
            if (schedule[assistant.getIndex()][day.getIndex()] != FREE) {
                throw new InvalidDayException("A shift was already assigned for this assistant on this day");
            }
        }

        // respect min balance + no consecutive assignments
        if (nbFreeDaysBefore(assistant, days.get(0)) < parameters.getMinBalance()) {
            throw new InvalidDayException("Assignment violates min balance");
        }

        if (nbFreeDaysAfter(assistant, days.get(days.size()-1)) < parameters.getMinBalance()) {
            throw new InvalidDayException("Assignment violates min balance");
        }
        
        if (nbAssignmentsOfShiftType(assistant, shift) >= shift.getMaxAssignments()) {
            throw new InvalidShiftTypeException("Assignment violates max assignments");
        }

        for (Day day : days)
            this.schedule[assistant.getIndex()][day.getIndex()] = shift.getType();
    }

    private int nbAssignmentsOfShiftType(Assistant assistant, Shift shift) {
        return switch(shift.getPeriod()) {
            case WEEK -> nbWeekAssignments(assistant, (WeekShift) shift);
            case WEEKEND -> nbWeekendAssignments(assistant, (WeekendShift) shift);
            case HOLIDAY -> nbHolidayAssignments(assistant, (HolidayShift) shift);
            default -> throw new IllegalStateException("Unexpected value: " + shift.getPeriod());
        };
    }

    private int nbWeekAssignments(Assistant assistant, WeekShift shift) {
        int result = 0;
        for (Week week : data.getWeeks()) {
            if (assignmentOn(assistant, week.getDays().get(0)) == shift.getType()) {
                result++;
            }
        }
        return result;
    }

    private int nbWeekendAssignments(Assistant assistant, WeekendShift shift) {
        int result = 0;
        for (Week week : data.getWeeks()) {
            if(!week.getWeekendDays().isEmpty()){
                if (assignmentOn(assistant, week.getWeekendDays().get(0)) == shift.getType()) {
                    result++;
                }
            }
        }
        return result;
    }

    private int nbHolidayAssignments(Assistant assistant, HolidayShift shift) {
        int result = 0;
        for (Week week : data.getWeeks()) {
            for (Day day : week.getHolidays()) {
                if (assignmentOn(assistant, day) == shift.getType()) {
                    result++;
                }
            }
        }
        return result;
    }

    public void clear(Assistant a, List<Day> days) {
        for (Day day : days)
            this.schedule[a.getIndex()][day.getIndex()] = FREE;
    }

    public int nbAssignmentsOfShiftTypeOn(Day day, ShiftType st) {
        int count = 0;
        for (int i = 0; i < getNbAssistants(); i++) {
            if (schedule[i][day.getIndex()] == st) {
                count++;
            }
        }
        return count;
    }

    public ShiftType assignmentOn(Assistant assistant, Day day) {
        return schedule[assistant.getIndex()][day.getIndex()];
    }

    public List<Swap> getWeekSwaps(Week week, WeekShift shift) {
        return getSwaps(week.getDays(), shift.getType());
    }

    public List<Swap> getWeekendSwaps(Week week, WeekendShift shift) {
        List<Swap> result = new ArrayList<>();
        if(!week.getWeekendDays().isEmpty()){
            result = getSwaps(week.getWeekendDays(), shift.getType());
        }
        return result;
    }

    public List<Swap> getHolidaySwaps(List<Day> holidays, HolidayShift shift) {
        List<Swap> candidateSwaps = new ArrayList<>();
        if(!holidays.isEmpty()){
            candidateSwaps = getSwaps(holidays, shift.getType());
        }
        return candidateSwaps;
    }

    private List<Swap> getSwaps(List<Day> days, ShiftType st) {
        List<Assistant> allowedAssistants = getAllowedAssistants(st);
        List<Swap> candidateSwaps = new ArrayList<>();
        for (Assistant assistant : allowedAssistants) {
            // find assignment of given shift type
            if (assignmentOn(assistant, days.get(0)) == st) {
                // find all other assistants that are free that week
                List<Assistant> otherAssistants = allowedAssistants.stream()
                        .filter(a -> !a.equals(assistant)
                                && days.stream()
                                .allMatch(d -> assignmentOn(a, d) == FREE)
                        )
                        .collect(Collectors.toList());
                // add new swaps
                for (Assistant other : otherAssistants) {
                    try {
                        Pair<Double, Double> res = computeNewFairness(assistant, other, days, st);
                        candidateSwaps.add(new Swap(assistant, other, days, st, res.getValue0(), res.getValue1()));
                    } catch (InvalidDayException | InvalidShiftTypeException ignore) {
                        // assignment failed -> swap is invalid
                    }
                }

            }
        }
        return candidateSwaps;
    }

    private List<Assistant> getAllowedAssistants(ShiftType st) {
        if (st == JAEV) {
            return data.getAssistants()
                    .stream()
                    .filter(a -> this.jaevShift.getAllowedAssistantTypes().contains(a.getType()))
                    .collect(Collectors.toList());
        }
        return data.getAssistants()
                .stream()
                .filter(a -> shifts.get(st).getAllowedAssistantTypes().contains(a.getType()))
                .collect(Collectors.toList());
    }

    private List<Assistant> getAllowedAssistants(Shift shift) {
        return getAllowedAssistants(shift.getType());
    }

    private Pair<Double, Double> computeNewFairness(Assistant from, Assistant to, List<Day> days, ShiftType st)
            throws InvalidDayException, InvalidShiftTypeException {

        assignNoBalance(to, days, shifts.get(st));
        clear(from, days);
        double fairness = fairnessScore();
        double balance = balanceScore();

        // undo assignments
        try {
            assignNoBalance(from, days, shifts.get(st));
        } catch (InvalidDayException e) {
            throw new RuntimeException("cannot undo assignments");
        }
        clear(to, days);
        return new Pair<>(fairness, balance);
    }

    public void performSwap(Swap bestSwap) throws InvalidDayException, InvalidShiftTypeException {
        clear(bestSwap.getFrom(), bestSwap.getDays());
        assignNoBalance(bestSwap.getTo(), bestSwap.getDays(), shifts.get(bestSwap.getShiftType()));
    }


    public String toString() {

        StringBuilder result = new StringBuilder();

        result.append(String.format("%1$20s", ""));
        for (Week week : data.getWeeks()) {
            result.append(String.format("%1$-68s", "WEEK " + week.getWeekNumber()));
        }
        result.append("\n");
        result.append(String.format("%1$20s", ""));

        for (Week week : data.getWeeks()) {
            for (Day day : week.getDays()) {
                result.append(String.format("%1$-9s", day.getDay0fWeek()));
            }
            result.append(String.format("%1$5s", ""));
        }
        result.append("\n\n");

        for (Assistant assistant : data.getAssistants()) {
            result.append(String.format("%1$-8s", assistant.getName()));
            result.append("  ");
            result.append(String.format("%1$-10s", assistant.getType().toString()));
            for (Week week : data.getWeeks()) {
                for (Day day : week.getDays())
                    result.append(String.format("%1$-9s", assignmentOn(assistant, day).toString()));
                result.append(String.format("%1$5s", ""));
            }
            result.append("\n");
        }
        return result.toString();
    }

    public JuniorAssistantEvening getJaevShift() {
        return this.jaevShift;
    }

    public void addJaevAssignmentOn(Assistant assistant, Day day) throws InvalidShiftTypeException, InvalidDayException {

        if (!this.jaevShift.getAllowedAssistantTypes().contains(assistant.getType())) {
            throw new InvalidShiftTypeException("Shift type not allowed for assistant");
        }

        assignJaev(assistant, day);
    }

    private void assignJaev(Assistant assistant, Day day) throws InvalidDayException {
        if (assistant.getFreeDayIds().contains(day.getId())) {
            throw new InvalidDayException("Cannot assign on a free day");
        }

        if (schedule[assistant.getIndex()][day.getIndex()] != FREE) {
            throw new InvalidDayException("A shift was already assigned for this assistant on this day");
        }

        if (day.getIndex() - 1 >= 0 && schedule[assistant.getIndex()][day.getIndex()-1] != FREE) {
            throw new InvalidDayException("Cannot assign one day after other shift");
        }

        if (day.getIndex() + 1 < getNbDays() && schedule[assistant.getIndex()][day.getIndex()+1] != FREE) {
            throw new InvalidDayException("Cannot assign one day before other shift");
        }

        for (int i = 2; i <= 3; i++) {
            if (day.getIndex() - i >= 0 && isWeekendOrHoliday(schedule[assistant.getIndex()][day.getIndex()-i])) {
                throw new InvalidDayException("Cannot assign too close after other weekend/holiday shift");
            }

            if (day.getIndex() + i < getNbDays() && isWeekendOrHoliday(schedule[assistant.getIndex()][day.getIndex()+i])) {
                throw new InvalidDayException("Cannot assign too close before other weekend/holiday shift");
            }
        }

        // respect min balance Jaev
        if (nbFreeDaysBeforeJaev(assistant, day) < parameters.getMinBalanceJaev()) {
            throw new InvalidDayException("Assignment violates min balance");
        }

        if (nbFreeDaysAfterJaev(assistant, day) < parameters.getMinBalanceJaev()) {
            throw new InvalidDayException("Assignment violates min balance");
        }

        this.schedule[assistant.getIndex()][day.getIndex()] = this.jaevShift.getType();
    }

    private int nbFreeDaysBeforeJaev(Assistant assistant, Day day) {
        int count = 0;
        for (int j = day.getIndex()-1; j >= 0; j--) {
            if (schedule[assistant.getIndex()][j] != JAEV) {
                count++;
            } else {
                return count;
            }
        }
        return getNbDays(); // if no assignment before this one
    }

    private int nbFreeDaysAfterJaev(Assistant assistant, Day day) {
        int count = 0;
        for (int j = day.getIndex()+1; j < getNbDays(); j++) {
            if (schedule[assistant.getIndex()][j] != JAEV) {
                count++;
            } else {
                return count;
            }
        }
        return getNbDays(); // if no assignment after this one
    }


    private boolean isWeekendOrHoliday(ShiftType shiftType){
        Set<ShiftType> weekendHolidayShiftTypes = this.shifts.values()
                .stream().filter(s -> s.getPeriod() == ShiftPeriod.WEEKEND || s.getPeriod() == ShiftPeriod.HOLIDAY)
                .map(Shift::getType)
                .collect(Collectors.toSet());

        return weekendHolidayShiftTypes.contains(shiftType);
    }

    public List<JaevSwap> getJaevSwaps(Day day) {
        List<Assistant> allowedAssistants = getAllowedAssistants(JAEV);
        List<JaevSwap> candidateSwaps = new ArrayList<>();
        for (Assistant assistant : allowedAssistants) {
            // find assignment of given shift type
            if (assignmentOn(assistant, day) == JAEV) {
                // find all other assistants that are free that week
                List<Assistant> otherAssistants = allowedAssistants.stream()
                        .filter(a -> !a.equals(assistant) && assignmentOn(a, day) == FREE)
                        .collect(Collectors.toList());
                // add new swaps
                for (Assistant other : otherAssistants) {
                    try {
                        double fairness = computeNewJaevFairness(assistant, other, day);
                        candidateSwaps.add(new JaevSwap(assistant, other, day, fairness));
                    } catch (InvalidDayException ignore) {
                        // assignment failed -> swap is invalid
                    }
                }

            }
        }
        return candidateSwaps;
    }

    private double computeNewJaevFairness(Assistant from, Assistant to, Day day) throws InvalidDayException {
        assignJaev(to, day);
        clear(from, Collections.singletonList(day));
        double jaevFairness = jaevFairnessScore();

        // undo assignments
        try {
            assignJaev(from, day);
        } catch (InvalidDayException e) {
            throw new RuntimeException("cannot undo assignments");
        }
        clear(to, Collections.singletonList(day));
        return jaevFairness;
    }

    public void performJaevSwap(JaevSwap bestSwap) throws InvalidDayException {
        clear(bestSwap.getFrom(), Collections.singletonList(bestSwap.getDay()));
        assignJaev(bestSwap.getTo(), bestSwap.getDay());
    }

    public void assignNoBalance(Assistant assistant, List<Day> days, Shift shift) throws InvalidDayException, InvalidShiftTypeException {
        // all hard constraints
        if (!shift.getAllowedAssistantTypes().contains(assistant.getType())) {
            throw new InvalidShiftTypeException("Shift type not allowed for assistant");
        }

        for (Day day : days) {
            if (assistant.getFreeDayIds().contains(day.getId())) {
                throw new InvalidDayException("Cannot assign on a free day");
            }
            if (schedule[assistant.getIndex()][day.getIndex()] != FREE) {
                throw new InvalidDayException("A shift was already assigned for this assistant on this day");
            }
        }

        // respect min balance + no consecutive assignments
        if (nbFreeDaysBefore(assistant, days.get(0)) < 1) {
            throw new InvalidDayException("Assignment violates min balance");
        }

        if (nbFreeDaysAfter(assistant, days.get(days.size()-1)) < 1) {
            throw new InvalidDayException("Assignment violates min balance");
        }

        if (nbAssignmentsOfShiftType(assistant, shift) >= shift.getMaxAssignments()) {
            throw new InvalidShiftTypeException("Assignment violates max assignments");
        }

        for (Day day : days)
            this.schedule[assistant.getIndex()][day.getIndex()] = shift.getType();
    }
}
