package com.scheduler.schedule;

import com.scheduler.assistant.Assistant;
import com.scheduler.shifttype.ShiftType;
import com.scheduler.shifttype.ShiftTypePeriod;
import com.scheduler.time.Day;

import java.util.HashMap;
import java.util.Map;

public class WeekSchedule {

    private final Map<Day, ShiftType> weekSchedule;

    public WeekSchedule() {
        this.weekSchedule = new HashMap<>();
    }


    public boolean skillTypeMet(Assistant assistant) {
        for (Day day : this.weekSchedule.keySet()) {
            if (!this.weekSchedule.get(day).getAllowedAssistantTypes().contains(assistant.getType())) {
                return false;
            }
        }
        return true;
    }

    public ShiftType assignmentOn(Day day) {
        return this.weekSchedule.get(day);
    }

    public boolean completeWeekendMet() {
        if (this.weekSchedule.values().stream().anyMatch(
                (ShiftType shiftType) -> shiftType.getSpanningPeriod() == ShiftTypePeriod.WEEKEND_HOLIDAY)
        ) {
            return this.weekSchedule.keySet().stream()
                    .filter(Day::isWeekend)
                    .map((Day day) -> this.weekSchedule.get(day).getId())
                    .distinct()
                    .count() == 1;
        }
        return true;
    }

    public boolean completeWeekMet() {
        if (this.weekSchedule.values().stream()
                .anyMatch((ShiftType shiftType) -> shiftType.getSpanningPeriod() == ShiftTypePeriod.WEEK)
        ) {
            return this.weekSchedule.keySet().stream()
                    .filter(Day::isWeekend)
                    .map((Day day) -> this.weekSchedule.get(day).getId())
                    .distinct()
                    .count() == 1;
        }
        return true;
    }

    public boolean freeOn(Day day) {
        return !this.weekSchedule.keySet().contains(day);
    }

    public void addAssignment(Day day, ShiftType shiftType) {
        if (!freeOn(day)) {
            throw new RuntimeException("Assignment already exists");
        }
        this.weekSchedule.put(day, shiftType);
    }
}
