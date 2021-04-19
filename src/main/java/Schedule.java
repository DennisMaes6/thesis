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

public class Schedule {

    private final InstanceData data;
    private final ModelParameters parameters;
    private final Map<ShiftTypeId, Double> shiftTypeWorkload = new HashMap<>();
    private final ShiftType[][] schedule;

    public Schedule(InstanceData data, ModelParameters parameters) {
        this.data = data;
        this.parameters = parameters;
        for (ShiftTypeModelParameters stps : parameters.getShiftTypeModelParameters()) {
            shiftTypeWorkload.put(stps.getShiftTypeId(), stps.getWorkload());
        }
        this.schedule = new ShiftType[getNbAssistants()][getNbDays()];

        for (int j = 0; j < getNbDays(); j ++) {
            data.getDays().get(j).setIndex(j);
        }

        for (int i = 0; i < getNbAssistants(); i++) {
            data.getAssistants().get(i).setIndex(i);
            for (int j = 0; j < getNbDays(); j ++) {
                schedule[i][j] = new Free();
            }
        }
    }

    // optimization objective
    public double fairnessScore() {
        List<Double> workloadPerAssistant = new ArrayList<>();
        for (int i = 0; i < getNbAssistants(); i++) {
            double workload = 0.0;
            for (int j = 1; j < getNbDays(); j += 7) {
                workload += getWorkload(schedule[i][j]);
            }
            workloadPerAssistant.add(workload / daysActive(i));
        }

        return workloadPerAssistant.stream().map(w -> w * w).reduce(0.0, Double::sum);
    }

    private int daysActive(int assistantIndex) {
        return getNbDays() - data.getAssistants().get(assistantIndex).getFreeDayIds().size();
    }

    private double getWorkload(ShiftType st) {
        if (st.getId() == ShiftTypeId.FREE) {
            return 0;
        }
        return shiftTypeWorkload.get(st.getId());
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
                    if (!idleStreak && !partOfBalance(schedule[i][j])) {
                        idleStreak = true;
                        startDay = j;
                    }

                    if (idleStreak && partOfBalance(schedule[i][j])) {
                        idleStreaks.add(j - startDay);
                        startDay = 0;
                        idleStreak = false;
                    }
                }

                if (!afterFirst && partOfBalance(schedule[i][j])) {
                    afterFirst = true;
                }
            }
        }

        return Collections.min(idleStreaks);
    }

    private boolean partOfBalance(ShiftType st) {
        return st.getId() != ShiftTypeId.FREE;
    }


    public void addWeekAssignmentOn(Assistant assistant, Week week, WeekShift shift)
            throws InvalidShiftTypeException, InvalidDayException {

        if (!shift.getAllowedAssistantTypes().contains(assistant.getType())) {
            throw new InvalidShiftTypeException("Shift type not allowed for assistant");
        }

        assign(assistant, week.getDays(), shift);
    }

    public void addWeekendAssignmentOn(Assistant assistant, Week week, WeekendHolidayShift shift)
            throws InvalidShiftTypeException, InvalidDayException {

        if (!shift.getAllowedAssistantTypes().contains(assistant.getType())) {
            throw new InvalidShiftTypeException("Shift type not allowed for assistant");
        }

        assign(assistant, week.getWeekendDays(), shift);

    }

    public void addHolidayAssignmentOn(Assistant assistant, Day day, WeekendHolidayShift shift)
            throws InvalidShiftTypeException, AssignWholeWeekendsException, InvalidDayException {

        if (!shift.getAllowedAssistantTypes().contains(assistant.getType())) {
            throw new InvalidShiftTypeException("Shift type not allowed for assistant");
        }

        if (!day.isHoliday()) {
            throw new InvalidShiftTypeException("Given day is not a holiday");
        }

        if (day.isWeekend()) {
            throw new AssignWholeWeekendsException("Cannot assign to single weekend day");
        }

        assign(assistant, Collections.singletonList(day), shift);
    }

    private int nbFreeDaysBefore(Assistant assistant, Day day) {
        int count = 0;
        for (int j = day.getIndex()-1; j >= 0; j--) {
            if (schedule[assistant.getIndex()][day.getIndex()].getId() == ShiftTypeId.FREE)
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
            if (schedule[assistant.getIndex()][day.getIndex()].getId() == ShiftTypeId.FREE)
                count++;
            else {
                return count;
            }
        }
        return getNbDays(); // if no assignment after this one
    }

    private void assign(Assistant assistant, List<Day> days, ShiftType shiftType) throws InvalidDayException {

        // Hard constraint checks (not complete!)
        for (Day day : days) {
            if (assistant.getFreeDayIds().contains(day.getId())) {
                throw new InvalidDayException("Cannot assign on a free day");
            }
            if (schedule[assistant.getIndex()][day.getIndex()].getId() != ShiftTypeId.FREE) {
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
            this.schedule[assistant.getIndex()][day.getIndex()] = shiftType;
    }

    private void clear(Assistant a, List<Day> days) {
        for (Day day : days)
            this.schedule[a.getIndex()][day.getIndex()] = new Free();
    }

    public int nbAssignmentsOfShiftTypeOn(Day day, ShiftType shiftType) {
        int count = 0;
        for (int i = 0; i < getNbAssistants(); i++) {
            if (schedule[i][day.getIndex()].getId() == shiftType.getId()) {
                count++;
            }
        }
        return count;
    }

    private ShiftType assignmentOn(Assistant assistant, Day day) {
        return schedule[assistant.getIndex()][day.getIndex()];
    }


    public List<Swap> getSwaps(Week week, WeekShift st) {
        List<Assistant> allowedAssistants = data.getAssistants()
                    .stream()
                    .filter(a -> st.getAllowedAssistantTypes().contains(a.getType()))
                    .collect(Collectors.toList());

        List<Swap> candidateSwaps = new ArrayList<>();
        for (Assistant assistant : allowedAssistants) {
            // find assignment of given shift type
            if (assignmentOn(assistant, week.getDays().get(0)).getId() == st.getId()) {
                // find all other assistants that are free that week
                List<Assistant> otherAssistants = allowedAssistants.stream()
                        .filter(a -> !a.equals(assistant)
                                && week.getDays().stream()
                                        .allMatch(d -> assignmentOn(a, d).getId() == ShiftTypeId.FREE)
                        )
                        .collect(Collectors.toList());
                // add new swaps
                for (Assistant other : otherAssistants) {
                    try {
                        double fairness = computeNewFairness(assistant, other, week.getDays(), st);
                        candidateSwaps.add(new Swap(assistant, other, week.getDays(), st, fairness));
                    } catch (InvalidDayException ignore) {
                        // assignment failed -> swap is invalid
                    }
                }

            }
        }


        return candidateSwaps;
    }

    private double computeNewFairness(Assistant from, Assistant to, List<Day> days, ShiftType st)
            throws InvalidDayException {
        clear(from, days);
        assign(to, days, st);
        double fairness = fairnessScore();

        // undo assignments
        clear(to, days);
        try {
            assign(from, days, st);
        } catch (InvalidDayException e) {
            throw new RuntimeException("cannot undo assignments");
        }


        return fairness;
    }

    public void performSwap(Swap bestSwap) throws InvalidDayException {
        clear(bestSwap.getFrom(), bestSwap.getDays());
        assign(bestSwap.getTo(), bestSwap.getDays(), bestSwap.getShiftType());
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
