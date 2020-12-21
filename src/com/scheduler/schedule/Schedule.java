package com.scheduler.schedule;

import com.scheduler.assistant.Assistant;
import com.scheduler.exceptions.AssignWholeWeekendsException;
import com.scheduler.exceptions.InvalidDayException;
import com.scheduler.exceptions.InvalidShiftTypeException;
import com.scheduler.shifttype.*;
import com.scheduler.time.Day;
import com.scheduler.time.Week;

import java.util.*;

public class Schedule {

    private final static double FAIRNESS_WEIGHT = 1.0; // maximize this
    private final static double BALANCE_WEIGHT = -1.0; // minimize this
    private final ShiftType[][] schedule;
    private final List<Week> weeks;
    private final List<Day> days;
    private final List<Assistant> assistants;
    private final Set<ShiftType> shiftTypes;

    public Schedule(List<Assistant> assistants, List<Week> weeks, Set<ShiftType> shiftTypes) {
        this.weeks = weeks;
        this.assistants = assistants;
        this.shiftTypes = shiftTypes;

        List<Day> days = new ArrayList<>();
        for (Week week : weeks) {
            days.addAll(week.getDays());
        }
        this.days = days;

        this.schedule = new ShiftType[assistants.size()][days.size()];
    }

    // objective function
    public double score() {
        return FAIRNESS_WEIGHT * fairnessScore() + BALANCE_WEIGHT * balanceScore();
    }

    private int balanceScore() {
        List<Integer> balanceScoresPerAssistant = new ArrayList<>();
        for (Assistant assistant : this.assistants) {
            balanceScoresPerAssistant.add(balanceScore(assistant));
        }

        return Collections.min(balanceScoresPerAssistant);
    }

    private int balanceScore(Assistant assistant) {
        List<Integer> idleStreaks = new ArrayList<>();
        boolean idleStreak = false;
        int idleStreakCount = 0;
        for (Day day : this.days) {
            if (freeOn(assistant, day)) {
                idleStreak = true;
                idleStreakCount++;
            } else if (!freeOn(assistant, day) && idleStreak) {
                idleStreaks.add(idleStreakCount);
                idleStreak = false;
                idleStreakCount = 0;
            }
        }

        if (idleStreaks.size() > 0) {
            return Collections.min(idleStreaks);
        }

        return this.days.size();
    }

    private double fairnessScore() {
        List<Double> fairnessScoresPerShiftType = new ArrayList<>();
        for (ShiftType shiftType : this.shiftTypes) {
            List<Double> activeTimes = new ArrayList<>();
            for (Assistant assistant : this.assistants) {
                activeTimes.add(
                    ((double) daysActive(assistant, shiftType)) / ((double) this.days.size() - assistant.getFreeDays().size())
                );
            }
            double max = Collections.max(activeTimes);
            double min = Collections.min(activeTimes);

            fairnessScoresPerShiftType.add(max - min);
        }

        return Collections.max(fairnessScoresPerShiftType);
    }

    private int daysActive(Assistant assistant, ShiftType shiftType) {
        int result = 0;
        for (Day day : this.days) {
            ShiftType assignment = schedule[this.assistants.indexOf(assistant)][this.days.indexOf(day)];
            if (assignment != null && assignment.getId() == shiftType.getId()) {
                result++;
            }
        }
        return result;
    }

    /**
     * Checks whether or not all hard constraints for this schedule, with the given shift types, are met.
     * @return True is schedule is valid, false otherwise.
     */
    public boolean valid() {
        return coverageMet()
            && skillTypesMet()
            && completeWeekendsMet()
            && completeWeeksMet()
            && freeDaysMet();
    }

    private boolean freeDaysMet() {
        for (Assistant assistant : this.assistants) {
            for (Day day : assistant.getFreeDays()) {
                if (!freeOn(assistant, day)) {
                    return false;
                }
            }
        }
        return true;
    }


    private boolean completeWeekendsMet() {
        for (Assistant assistant : this.assistants) {
            for (Week week : this.weeks) {
                List<ShiftType> assignments = new ArrayList<>();
                for (Day day : week.getWeekendDays()) {
                    assignments.add(this.assignmentOn(assistant, day));
                }
               if (assignments.stream()
                       .filter(shiftType -> shiftType == null || shiftType.getSpanningPeriod() == ShiftTypePeriod.WEEKEND_HOLIDAY)
                       .distinct()
                       .count() > 1) {
                   return false;
               }
            }
        }
        return true;
    }

    private boolean completeWeeksMet() {
        for (Assistant assistant : this.assistants) {
            for (Week week : this.weeks) {
                List<ShiftType> assignments = new ArrayList<>();
                for (Day day : week.getDays()) {
                    assignments.add(this.assignmentOn(assistant, day));
                }
                if (assignments.stream()
                        .filter(shiftType -> shiftType == null || shiftType.getSpanningPeriod() == ShiftTypePeriod.WEEK)
                        .distinct()
                        .count() > 1) {
                    return false;
                }
            }
        }
        return true;
    }


    private boolean skillTypesMet() {
        for (Assistant assistant : this.assistants) {
            for (Day day : this.days) {
                ShiftType shiftType = assignmentOn(assistant, day);
                if (shiftType != null && !shiftType.getAllowedAssistantTypes().contains(assistant.getType())) {
                    return false;
                }
            }
        }
        return true;
    }



    private boolean coverageMet() {
        for (ShiftType shiftType : this.shiftTypes) {
            for (Day day: this.days) {
                if (nbAssignmentsOfShiftTypeOn(day, shiftType) != shiftType.getRequiredNbAssistants(day)) {
                    return false;
                }
            }
        }
        return true;
    }

    public int nbAssignmentsOfShiftTypeOn(Day day, ShiftType shiftType) {
        int sum = 0;
        for (Assistant assistant : this.assistants) {
            if (assignmentOn(assistant, day) == shiftType) {
                sum++;
            }
        }
        return sum;
    }


    private boolean freeOn(Assistant assistant, Day day) {
        return null == this.schedule[this.assistants.indexOf(assistant)][this.days.indexOf(day)];
    }

    private ShiftType assignmentOn(Assistant assistant, Day day) {
        return this.schedule[this.assistants.indexOf(assistant)][this.days.indexOf(day)];
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
        if (assistant.getFreeDays().contains(day)) {
            throw new InvalidDayException("Cannot assign on a free day");
        }

        if (assignmentOn(assistant, day) != null) {
            throw new InvalidDayException("A shift was already assigned for this assistant on this day");
        }

        this.schedule[this.assistants.indexOf(assistant)][this.days.indexOf(day)] = shiftType;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append(String.format("fairness score: %f, balance score: %d\n\n", fairnessScore(), balanceScore()));

        result.append(String.format("%1$15s", ""));
        for (Week week : weeks) {
            result.append(String.format("%1$-68s", "WEEK " + week.getWeekNumber()));
        }
        result.append("\n");
        result.append(String.format("%1$15s", ""));

        for (Week week : weeks) {
            for (Day day : week.getDays()) {
                result.append(String.format("%1$-9s", day.getDay0fWeek()));
            }
            result.append(String.format("%1$5s", ""));
        }
        result.append("\n\n");

        for (Assistant assistant : this.assistants) {
            result.append(String.format("%1$3s", assistant.getName()));
            result.append(String.format("%1$12s", "(" + assistant.getType().toString() + ")  "));
            for (Week week : weeks) {
                for (Day day : week.getDays()) {
                    ShiftType shiftType = assignmentOn(assistant, day);
                    String string;
                    if (shiftType == null) {
                        string = "_";
                    } else {
                        string = shiftType.getId().toString();
                    }
                    result.append(String.format("%1$-9s", string));
                }
                result.append(String.format("%1$5s", ""));
            }
            result.append("\n");
        }
        return result.toString();
    }
}
