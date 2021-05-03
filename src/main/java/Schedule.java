import input.InstanceData;
import input.ShiftTypeModelParameters;
import input.assistant.Assistant;
import exceptions.InvalidDayException;
import exceptions.InvalidShiftTypeException;
import input.ModelParameters;
import input.shift.*;
import input.time.Day;
import input.time.Week;

import java.util.*;
import java.util.stream.Collectors;

import static input.shift.ShiftType.*;

public class Schedule {

    private final InstanceData data;
    private final ModelParameters parameters;
    private final Map<ShiftType, Shift> shifts = new HashMap<>();
    private final ShiftType[][] schedule;
    private final JuniorAssistantEvening jaevShift;

    public Schedule(InstanceData data, ModelParameters parameters) {
        this.data = data;
        this.parameters = parameters;
        initializeShifts(parameters.getShiftTypeModelParameters());
        this.schedule = new ShiftType[getNbAssistants()][getNbDays()];
        this.jaevShift = new JuniorAssistantEvening();

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
    }

    private void initializeShifts(List<ShiftTypeModelParameters> stps) {
        for (ShiftTypeModelParameters stp : stps) {
            switch (stp.getShiftType()) {
                case JANW -> this.shifts.put(JANW, new JuniorAssistantNightWeek(stp.getWorkload()));
                case JAWE -> this.shifts.put(JAWE, new JuniorAssistantWeekend(stp.getWorkload()));
                case JAHO -> this.shifts.put(JAHO, new JuniorAssistantHoliday(stp.getWorkload()));
                case SAEW -> this.shifts.put(SAEW, new SeniorAssistantEveningWeek(stp.getWorkload()));
                case SAWE -> this.shifts.put(SAWE, new SeniorAssistantWeekend(stp.getWorkload()));
                case SAHO -> this.shifts.put(SAHO, new SeniorAssistantHoliday(stp.getWorkload()));
                case TPWE -> this.shifts.put(TPWE, new TransportWeekend(stp.getWorkload()));
                case TPHO -> this.shifts.put(TPHO, new TransportHoliday(stp.getWorkload()));
                case CALL -> this.shifts.put(CALL, new Call(stp.getWorkload()));
            }
        }
        initMaxAssignments(stps);
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

    // optimization objective
    public double fairnessScore() {
        List<Double> workloadPerAssistant = new ArrayList<>();
        for (Assistant assistant : data.getAssistants()) {
            workloadPerAssistant.add(workloadForAssistant(assistant));
        }
        return Collections.max(workloadPerAssistant) - Collections.min(workloadPerAssistant);
    }

    double jaevFairnessScore() {
        List<Double> jaevWorkloadPerAssistant = new ArrayList<>();
        for (Assistant assistant : getAllowedAssistants(JAEV)) {
            jaevWorkloadPerAssistant.add(jaevWorkloadForAssistant(assistant));
        }
        return Collections.max(jaevWorkloadPerAssistant) - Collections.min(jaevWorkloadPerAssistant);
    }

    public double workloadForAssistant(Assistant assistant) {
        return Arrays.stream(schedule[assistant.getIndex()])
                .filter(st -> st != JAEV)
                .map(this::workload)
                .reduce(0.0, Double::sum) / daysActive(assistant);
    }

    private double jaevWorkloadForAssistant(Assistant assistant) {
        return Arrays.stream(schedule[assistant.getIndex()])
                .filter(st -> st == JAEV)
                .map(this::workload)
                .reduce(0.0, Double::sum) / daysActive(assistant);
    }

    private double workload(ShiftType shiftType) {
        if (shiftType == FREE)
            return 0.0;
        else {
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

    public int balanceScore() {
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

        return Collections.min(idleStreaks);
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
            if (assignmentOn(assistant, week.getWeekendDays().get(0)) == shift.getType()) {
                result++;
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

    private void clear(Assistant a, List<Day> days) {
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
        return getSwaps(week.getWeekendDays(), shift.getType());
    }

    public List<Swap> getHolidaySwaps(Day holiday, HolidayShift shift) {
        return getSwaps(Collections.singletonList(holiday), shift.getType());
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
                        double fairness = computeNewFairness(assistant, other, days, st);
                        candidateSwaps.add(new Swap(assistant, other, days, st, fairness));
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

    private double computeNewFairness(Assistant from, Assistant to, List<Day> days, ShiftType st)
            throws InvalidDayException, InvalidShiftTypeException {

        assign(to, days, shifts.get(st));
        clear(from, days);
        double fairness = fairnessScore();

        // undo assignments
        try {
            assign(from, days, shifts.get(st));
        } catch (InvalidDayException e) {
            throw new RuntimeException("cannot undo assignments");
        }
        clear(to, days);
        return fairness;
    }

    public void performSwap(Swap bestSwap) throws InvalidDayException, InvalidShiftTypeException {
        clear(bestSwap.getFrom(), bestSwap.getDays());
        assign(bestSwap.getTo(), bestSwap.getDays(), shifts.get(bestSwap.getShiftType()));
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
}
