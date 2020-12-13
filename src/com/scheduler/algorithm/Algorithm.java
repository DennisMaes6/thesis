package com.scheduler.algorithm;

import com.scheduler.assistant.Assistant;
import com.scheduler.exceptions.AssignWholeWeekendsException;
import com.scheduler.exceptions.InvalidDayException;
import com.scheduler.exceptions.InvalidShiftTypeException;
import com.scheduler.schedule.Schedule;
import com.scheduler.shifttype.DayShift;
import com.scheduler.shifttype.ShiftType;
import com.scheduler.shifttype.WeekShift;
import com.scheduler.shifttype.WeekendHolidayShift;
import com.scheduler.time.Day;
import com.scheduler.time.Week;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Algorithm {

    private final AlgorithmInput input;

    public Algorithm(AlgorithmInput input) {
        this.input = input;
    }

    public Schedule generateSchedule() {
        Schedule initialSchedule = initialSchedule();
        return initialSchedule;
    }

    private Schedule initialSchedule() {
        Schedule schedule = new Schedule(input.getAssistants(), input.getWeeks(), input.getShiftTypes());

        for (ShiftType shiftType : input.getShiftTypes()) {
            switch (shiftType.getSpanningPeriod()) {
                case WEEK: schedule = completeScheduleForWeekShifts(schedule, (WeekShift) shiftType);
                case DAY: schedule = completeScheduleForDayShifts(schedule, (DayShift) shiftType);
                case WEEKEND_HOLIDAY: schedule = completeScheduleForWeekendHolidayShifts(schedule, (WeekendHolidayShift) shiftType);
            }
        }

        return schedule;
    }

    private Schedule completeScheduleForWeekShifts(Schedule schedule, WeekShift shiftType) {
        for (Week week : input.getWeeks()) {
            List<Assistant> invalidAssistants = new ArrayList<>();
            while (schedule.nbAssignmentsOfShiftTypeOn(week.getDays().get(0), shiftType) < shiftType.getRequiredNbAssistants(week.getDays().get(0))) {
                Assistant assistant = randomAssistantForShiftType(invalidAssistants, shiftType);
                try {
                    schedule.addWeekAssignmentOn(assistant, week, shiftType);
                } catch (InvalidDayException e) {
                    invalidAssistants.add(assistant);
                } catch (InvalidShiftTypeException e) {
                    e.printStackTrace();
                }
            }
        }
        return schedule;
    }

    private Schedule completeScheduleForDayShifts(Schedule schedule, DayShift shiftType) {
        for (Week week : input.getWeeks()) {
            for (Day day : week.getDays()) {
                List<Assistant> invalidAssistants = new ArrayList<>();
                while (schedule.nbAssignmentsOfShiftTypeOn(day, shiftType) < shiftType.getRequiredNbAssistants(day)) {
                    Assistant assistant = randomAssistantForShiftType(invalidAssistants, shiftType);
                    try {
                        schedule.addDayAssignmentOn(assistant, day, shiftType);
                    } catch (InvalidDayException e) {
                        invalidAssistants.add(assistant);
                    } catch (InvalidShiftTypeException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return schedule;
    }

    private Schedule completeScheduleForWeekendHolidayShifts(Schedule schedule, WeekendHolidayShift shiftType) {
        for (Week week : input.getWeeks()) {
            List<Assistant> invalidAssistants = new ArrayList<>();
            while (schedule.nbAssignmentsOfShiftTypeOn(week.getWeekendDays().get(0), shiftType) <
                    shiftType.getRequiredNbAssistants(week.getWeekendDays().get(0))) {
                Assistant assistant = randomAssistantForShiftType(invalidAssistants, shiftType);
                try {
                    schedule.addWeekendAssignmentOn(assistant, week, shiftType);
                } catch (InvalidDayException e) {
                    invalidAssistants.add(assistant);
                } catch (InvalidShiftTypeException e) {
                    e.printStackTrace();
                }
            }
            for (Day day : week.getHolidays()) {
                invalidAssistants = new ArrayList<>();
                while (schedule.nbAssignmentsOfShiftTypeOn(day, shiftType) < shiftType.getRequiredNbAssistants(day)) {
                    Assistant assistant = randomAssistantForShiftType(invalidAssistants, shiftType);
                    try {
                        schedule.addHolidayAssignmentOn(assistant, day, shiftType);
                    } catch (InvalidDayException e) {
                        invalidAssistants.add(assistant);
                    } catch (InvalidShiftTypeException | AssignWholeWeekendsException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return schedule;
    }

    private Assistant randomAssistantForShiftType(List<Assistant> excludedAssistants, ShiftType shiftType) {
        List<Assistant> allowedAssistants = input.getAssistants()
                .stream()
                .filter(assistant -> !excludedAssistants.contains(assistant))
                .filter(assistant -> shiftType.getAllowedAssistantTypes().contains(assistant.getType()))
                .collect(Collectors.toList());
        Random random = new Random();
        return allowedAssistants.get(random.nextInt(allowedAssistants.size()));
    }
}
