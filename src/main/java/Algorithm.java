import input.assistant.Assistant;
import exceptions.AssignWholeWeekendsException;
import exceptions.InvalidDayException;
import exceptions.InvalidShiftTypeException;
import input.InstanceData;
import input.ModelParameters;
import input.shifttype.*;
import input.time.Day;
import input.time.Week;

import java.util.*;
import java.util.stream.Collectors;

public class Algorithm {

    private final static Set<ShiftType> SHIFT_TYPES = new HashSet<>(
            Arrays.asList(
                    new JuniorAssistantNightWeek(),
                    new JuniorAssistantWeekendHoliday(),
                    new SeniorAssistantEveningWeek(),
                    new SeniorAssistantWeekendHoliday(),
                    new Transport(),
                    new Call()
            )
    );

    private final InstanceData data;
    private final ModelParameters parameters;

    public Algorithm(InstanceData data, ModelParameters parameters) {
        this.data = data;
        this.parameters = parameters;
    }

    public Schedule generateSchedule() {
        return initialSchedule();
    }

    private Schedule initialSchedule() {
        Schedule schedule = new Schedule(data, parameters);
        completeSchedule(schedule);
        return schedule;
    }

    private void completeSchedule(Schedule schedule) {
        for (ShiftType shiftType : SHIFT_TYPES) {
            switch (shiftType.getSpanningPeriod()) {
                case WEEK -> completeScheduleForWeekShifts(schedule, (WeekShift) shiftType);
                case WEEKEND_HOLIDAY -> completeScheduleForWeekendHolidayShifts(schedule, (WeekendHolidayShift) shiftType);
            }
        }
    }

    private void completeScheduleForWeekShifts(Schedule schedule, WeekShift shiftType) {
        for (Week week : data.getWeeks()) {
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
    }

    private void completeScheduleForWeekendHolidayShifts(Schedule schedule, WeekendHolidayShift shiftType) {
        for (Week week : data.getWeeks()) {
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
    }

    private Assistant randomAssistantForShiftType(List<Assistant> excludedAssistants, ShiftType shiftType) {
        List<Assistant> allowedAssistants = data.getAssistants()
                .stream()
                .filter(assistant -> !excludedAssistants.contains(assistant))
                .filter(assistant -> shiftType.getAllowedAssistantTypes().contains(assistant.getType()))
                .collect(Collectors.toList());
        Random random = new Random();
        return allowedAssistants.get(random.nextInt(allowedAssistants.size()));
    }
}
