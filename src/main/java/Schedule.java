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

public class Schedule {

    private final InstanceData data;
    private final Map<ShiftTypeId, Double> shiftTypeWorkload = new HashMap<>();
    private final ShiftType[][] schedule;

    public Schedule(InstanceData data, ModelParameters parameters) {
        this.data = data;
        for (ShiftTypeModelParameters stps : parameters.getShiftTypeModelParameters()) {
            shiftTypeWorkload.put(stps.getShiftTypeId(), stps.getWorkload());
        }
        this.schedule = new ShiftType[getNbAssistants()][getNbDays()];
        for (int i = 0; i < getNbAssistants(); i++) {
            for (int j = 0; j < getNbDays(); j ++) {
                schedule[i][j] = new Free();
            }
        }
    }

    public double fairnessScore() {
        List<Double> workloadPerAssistant = new ArrayList<>();
        for (int i = 0; i < getNbAssistants(); i++) {
            double workload = 0.0;
            for (int j = 1; j < getNbDays(); j += 7) {
                workload += getWorkload(schedule[i][j]);
            }
            workloadPerAssistant.add(workload / daysActive(i));
        }

        return Collections.max(workloadPerAssistant) - Collections.min(workloadPerAssistant);
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

        for (Day day : week.getDays()) {
            assign(assistant, day, shift);
        }
    }

    public void addWeekendAssignmentOn(Assistant assistant, Week week, WeekendHolidayShift shift)
            throws InvalidShiftTypeException, InvalidDayException {

        if (!shift.getAllowedAssistantTypes().contains(assistant.getType())) {
            throw new InvalidShiftTypeException("Shift type not allowed for assistant");
        }

        for (Day day : week.getWeekendDays()) {
            assign(assistant, day, shift);
        }
    }


    public void addDayAssignmentOn(Assistant assistant, Day day, DayShift shift)
            throws InvalidShiftTypeException, InvalidDayException {

        if (!shift.getAllowedAssistantTypes().contains(assistant.getType())) {
            throw new InvalidShiftTypeException("Shift type not allowed for assistant");
        }

        assign(assistant, day, shift);
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

        assign(assistant, day, shift);
    }

    private void assign(Assistant assistant, Day day, ShiftType shiftType) throws InvalidDayException {
        if (assistant.getFreeDayIds().contains(day.getId())) {
            throw new InvalidDayException("Cannot assign on a free day");
        }

        int assistantIndex = data.getAssistants().indexOf(assistant);
        int dayIndex = data.getDays().indexOf(day);

        if (schedule[assistantIndex][dayIndex].getId() != ShiftTypeId.FREE) {
            throw new InvalidDayException("A shift was already assigned for this assistant on this day");
        }

        this.schedule[assistantIndex][dayIndex] = shiftType;
    }

    public int nbAssignmentsOfShiftTypeOn(Day day, ShiftType shiftType) {
        int dayIndex = data.getDays().indexOf(day);
        int count = 0;
        for (int i = 0; i < getNbAssistants(); i++) {
            if (schedule[i][dayIndex].getId() == shiftType.getId()) {
                count++;
            }
        }
        return count;
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

    private ShiftType assignmentOn(Assistant assistant, Day day) {
        int assistantIndex = data.getAssistants().indexOf(assistant);
        int dayIndex = data.getDays().indexOf(day);
        return schedule[assistantIndex][dayIndex];
    }
}
