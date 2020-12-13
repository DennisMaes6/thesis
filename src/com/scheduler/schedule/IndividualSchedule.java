package com.scheduler.schedule;

import com.scheduler.assistant.Assistant;
import com.scheduler.time.Day;
import com.scheduler.shifttype.ShiftType;
import com.scheduler.time.Week;

import java.util.HashMap;
import java.util.Map;

public class IndividualSchedule {

    private final Map<Week, WeekSchedule> individualSchedule;

    public IndividualSchedule() {
        this.individualSchedule = new HashMap<>();
    }

    public ShiftType assignmentOn(Day day) {
        return individualSchedule.get(day.getWeek()).assignmentOn(day);
    }

    public boolean skillTypeMet(Assistant assistant) {
        for (Week week : this.individualSchedule.keySet()) {
            if (!this.individualSchedule.get(week).skillTypeMet(assistant)) {
                return false;
            }
        }
        return true;
    }

    public boolean completeWeekendsMet() {
        for (Week week : this.individualSchedule.keySet()) {
            if (!this.individualSchedule.get(week).completeWeekendMet()) {
                return false;
            }
        }
        return true;
    }

    public boolean completeWeeksMet() {
        for (Week week : this.individualSchedule.keySet()) {
            if (!this.individualSchedule.get(week).completeWeekMet()) {
                return false;
            }
        }
        return true;
    }

    public boolean freeOn(Day day) {
        if (this.individualSchedule.containsKey(day.getWeek())) {
            return this.individualSchedule.get(day.getWeek()).freeOn(day);
        }
        return true;
    }

    public void addAssignment(Day day, ShiftType shiftType) {
        if (!this.individualSchedule.containsKey(day.getWeek())) {
            this.individualSchedule.put(day.getWeek(), new WeekSchedule());
        }
        this.individualSchedule.get(day.getWeek()).addAssignment(day, shiftType);
    }
}
