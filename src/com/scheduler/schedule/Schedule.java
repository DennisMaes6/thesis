package com.scheduler.schedule;

import com.scheduler.assistant.Assistant;
import com.scheduler.time.Day;
import com.scheduler.shifttype.ShiftType;
import com.scheduler.time.Week;

import java.util.*;

public class Schedule {

    private final boolean[][][] schedule;

    public Schedule(List<Week> weeks, Set<Assistant> assistants, Set<ShiftType> shiftTypes) {
        int nbDays = 0;
        for (Week week : weeks) {
            nbDays += week.getDays().size();
        }
        this.schedule = new boolean[nbDays][assistants.size()][shiftTypes.size()];
    }

    public float score() {
        // TODO: implement this
        return 0;
    }

    /**
     * Checks whether or not all hard constraints for this schedule, with the given shift types, are met.
     * @return True is schedule is valid, false otherwise.
     */
    public boolean valid(Set<ShiftType> shiftTypes) {
        return coverageMet(shiftTypes)
            && skillTypesMet()
            && completeWeekendsMet()
            && completeWeeksMet()
            && freeDaysMet();
    }


    private boolean fr

    private boolean freeDaysMet() {
        for (Assistant assistant : this.schedule.keySet()) {
            for (Day day : assistant.getFreeDays()) {
                if (!this.schedule.get(assistant).freeOn(day)) {
                    return false;
                }
            }

        }
        return true;
    }

    private boolean completeWeeksMet() {
        for (Assistant assistant : this.schedule.keySet()) {
            if (!this.schedule.get(assistant).completeWeeksMet()) {
                return false;
            }
        }
        return true;
    }

    private boolean completeWeekendsMet() {
        for (Assistant assistant : this.schedule.keySet()) {
            if (!this.schedule.get(assistant).completeWeekendsMet()) {
                return false;
            }
        }
        return true;
    }

    private boolean skillTypesMet() {
        for (Assistant assistant : this.schedule.keySet()) {
            if (!this.schedule.get(assistant).skillTypeMet(assistant)) {
                return false;
            }
        }
        return true;
    }

    private boolean coverageMet(Set<ShiftType> shiftTypes) {
        for (Week week : this.weeks) {
            for (Day day : week.getDays()) {
                for (ShiftType shiftType : shiftTypes) {
                    if (getNbAssignmentsOnDayForShiftType(day, shiftType) != shiftType.getRequiredNbAssistants(day)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private int getNbAssignmentsOnDayForShiftType(Day day, ShiftType shiftType) {
        int count = 0;
        for (Assistant assistant : this.schedule.keySet()) {
            if (schedule.get(assistant).assignmentOn(day).getId().equals(shiftType.getId())) {
                count++;
            }
        }
        return count;
    }

    public void addAssignment(Assistant assistant, Day day, ShiftType shiftType) {
        if (!this.schedule.keySet().contains(assistant)) {
           this.schedule.put(assistant, new IndividualSchedule());
        }
        this.schedule.get(assistant).addAssignment(day, shiftType);
    }
}
