package com.scheduler.schedule;

import com.scheduler.assistant.Assistant;
import com.scheduler.shifttype.ShiftTypePeriod;
import com.scheduler.time.Day;
import com.scheduler.shifttype.ShiftType;
import com.scheduler.time.Week;

import java.util.*;

public class Schedule {

    private final ShiftType[][] schedule;
    private final List<Week> weeks;
    private final List<Day> days;
    private final List<Assistant> assistants;
    private final List<ShiftType> shiftTypes;

    public Schedule(List<Week> weeks, List<Assistant> assistants, List<ShiftType> shiftTypes) {
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

    public float score() {
        // TODO: implement this
        return 0;
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
                int sum = 0;
                for (Assistant assistant : this.assistants) {
                    if (assignmentOn(assistant, day) == shiftType) {
                        sum++;
                    }
                }
                if (sum != shiftType.getRequiredNbAssistants(day)) {
                    return false;
                }
            }
        }
        return true;
    }


    private boolean freeOn(Assistant assistant, Day day) {
        return null == this.schedule[this.assistants.indexOf(assistant)][this.days.indexOf(day)];
    }

    private ShiftType assignmentOn(Assistant assistant, Day day) {
        return this.schedule[this.assistants.indexOf(assistant)][this.days.indexOf(day)];
    }

    public void addAssignment(Assistant assistant, Day day, ShiftType shiftType) {
        this.schedule[this.assistants.indexOf(assistant)][this.days.indexOf(day)] = shiftType;
    }


}
