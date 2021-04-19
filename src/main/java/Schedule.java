import input.InstanceData;
import input.ShiftTypeModelParameters;
import input.assistant.Assistant;
import exceptions.AssignWholeWeekendsException;
import exceptions.InvalidDayException;
import exceptions.InvalidShiftTypeException;
import input.ModelParameters;
import input.shifttype.*;
import input.time.Day;
import input.time.Week;

import java.util.*;
import java.util.stream.Collectors;

import static input.shifttype.ShiftType.*;

public class Schedule {

    private final InstanceData data;
    private final ModelParameters parameters;
    private final Map<ShiftType, Shift> shifts = new HashMap<>();
    private final ShiftType[][] schedule;

    public Schedule(InstanceData data, ModelParameters parameters) {
        this.data = data;
        this.parameters = parameters;
        initializeShifts(parameters.getShiftTypeModelParameters());
        this.schedule = new ShiftType[getNbAssistants()][getNbDays()];

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
                case JANW:
                    this.shifts.put(JANW, new JuniorAssistantNightWeek(stp.getWorkload()));
                case JAWE:
                    this.shifts.put(JAWE, new JuniorAssistantWeekend(stp.getWorkload()));
                case JAHO:
                    this.shifts.put(JAHO, new JuniorAssistantHoliday(stp.getWorkload()));
                case SAEW:
                    this.shifts.put(SAEW, new SeniorAssistantEveningWeek(stp.getWorkload()));
                case SAWE:
                    this.shifts.put(SAWE, new SeniorAssistantWeekend(stp.getWorkload()));
                case SAHO:
                    this.shifts.put(SAHO, new SeniorAssistantHoliday(stp.getWorkload()));
                case TPWE:
                    this.shifts.put(TPWE, new TransportWeekend(stp.getWorkload()));
                case TPHO:
                    this.shifts.put(TPHO, new TransportHoliday(stp.getWorkload()));
                case CALL:
                    this.shifts.put(CALL, new Call(stp.getWorkload()));
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

    // optimization objective
    public double fairnessScore() {
        List<Double> workloadPerAssistant = new ArrayList<>();
        for (int i = 0; i < getNbAssistants(); i++) {
            double workload = 0.0;
            for (int j = 1; j < getNbDays(); j += 7) {
                workload += workload(schedule[i][j]);
            }
            workloadPerAssistant.add(workload / daysActive(i));
        }

        return workloadPerAssistant.stream().map(w -> w * w).reduce(0.0, Double::sum);
    }

    private double workload(ShiftType shiftType) {
        if (shiftType == FREE)
            return 0.0;
        else
            return shifts.get(shiftType).getWorkload();
    }

    private int daysActive(int assistantIndex) {
        return getNbDays() - data.getAssistants().get(assistantIndex).getFreeDayIds().size();
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
                    if (!idleStreak && schedule[i][j] == FREE) {
                        idleStreak = true;
                        startDay = j;
                    }

                    if (idleStreak && schedule[i][j] != FREE) {
                        idleStreaks.add(j - startDay);
                        startDay = 0;
                        idleStreak = false;
                    }
                }

                if (!afterFirst && schedule[i][j] != FREE) {
                    afterFirst = true;
                }
            }
        }

        return Collections.min(idleStreaks);
    }

    public void addAssignmentOn(Assistant assistant, List<Day> days, Shift shift)
            throws InvalidShiftTypeException, InvalidDayException {

        if (!shift.getAllowedAssistantTypes().contains(assistant.getType())) {
            throw new InvalidShiftTypeException("Shift type not allowed for assistant");
        }

        assign(assistant, days, shift);
    }

    private int nbFreeDaysBefore(Assistant assistant, Day day) {
        int count = 0;
        for (int j = day.getIndex()-1; j >= 0; j--) {
            if (schedule[assistant.getIndex()][day.getIndex()] == FREE)
                count++;
            else {
                return count;
            }
        }
        return getNbDays(); // if no assignment before this one
    }

    private int nbFreeDaysAfter(Assistant assistant, Day day) {
        int count = 0;
        for (int j = day.getIndex()+1; j < getNbDays(); j++) {
            if (schedule[assistant.getIndex()][day.getIndex()] == FREE)
                count++;
            else {
                return count;
            }
        }
        return getNbDays(); // if no assignment after this one
    }

    private void assign(Assistant assistant, List<Day> days, Shift shift) throws InvalidDayException {

        // Hard constraint checks (not complete!)
        for (Day day : days) {
            if (assistant.getFreeDayIds().contains(day.getId())) {
                throw new InvalidDayException("Cannot assign on a free day");
            }
            if (schedule[assistant.getIndex()][day.getIndex()] != FREE) {
                throw new InvalidDayException("A shift was already assigned for this assistant on this day");
            }
        }

        if (nbFreeDaysBefore(assistant, days.get(0)) < parameters.getMinBalance()) {
            throw new InvalidDayException("Assignment violates min balance");
        }

        if (nbFreeDaysAfter(assistant, days.get(days.size()-1)) < parameters.getMinBalance()) {
            throw new InvalidDayException("Assignment violates min balance");
        }

        for (Day day : days)
            this.schedule[assistant.getIndex()][day.getIndex()] = shift.getType();
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

    private ShiftType assignmentOn(Assistant assistant, Day day) {
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
                    } catch (InvalidDayException ignore) {
                        // assignment failed -> swap is invalid
                    }
                }

            }
        }
        return candidateSwaps;
    }

    private List<Assistant> getAllowedAssistants(ShiftType st) {
        return data.getAssistants()
                .stream()
                .filter(a -> shifts.get(st).getAllowedAssistantTypes().contains(a.getType()))
                .collect(Collectors.toList());
    }

    private List<Assistant> getAllowedAssistants(Shift shift) {
        return data.getAssistants()
                .stream()
                .filter(a -> shift.getAllowedAssistantTypes().contains(a.getType()))
                .collect(Collectors.toList());
    }

    private double computeNewFairness(Assistant from, Assistant to, List<Day> days, ShiftType st)
            throws InvalidDayException {
        clear(from, days);
        assign(to, days, shifts.get(st));
        double fairness = fairnessScore();

        // undo assignments
        clear(to, days);
        try {
            assign(from, days, shifts.get(st));
        } catch (InvalidDayException e) {
            throw new RuntimeException("cannot undo assignments");
        }


        return fairness;
    }

    public void performSwap(Swap bestSwap) throws InvalidDayException {
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

}
