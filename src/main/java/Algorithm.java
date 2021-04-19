import exceptions.InvalidDayException;
import exceptions.InvalidShiftTypeException;
import input.InstanceData;
import input.ModelParameters;
import input.assistant.Assistant;
import input.shift.*;
import input.time.Day;
import input.time.Week;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Algorithm {

    private final InstanceData data;
    private final ModelParameters parameters;

    public Algorithm(InstanceData data, ModelParameters parameters) {
        this.data = data;
        this.parameters = parameters;
    }

    // run algorithm
    public Schedule generateSchedule() {
        Schedule schedule = initialSchedule();
        optimizeSchedule(schedule);
        return schedule;
    }

    private void optimizeSchedule(Schedule schedule) {
        boolean changed = true;
        while (changed) {
            List<Boolean> changedList = new ArrayList<>();
            for (Week week : data.getWeeks()) {
                for (Shift shift : schedule.getShifts().values()) {
                    switch (shift.getPeriod()) {
                        case WEEK:
                            assert shift instanceof WeekShift;
                            changedList.add(
                                    performBestSwap(schedule, schedule.getWeekSwaps(week, (WeekShift) shift))
                            );
                            break;
                        case WEEKEND:
                            assert shift instanceof WeekendShift;
                            changedList.add(
                                    performBestSwap(schedule, schedule.getWeekendSwaps(week, (WeekendShift) shift))
                            );
                            break;
                        case HOLIDAY:
                            for (Day day : week.getHolidays()) {
                                assert shift instanceof HolidayShift;
                                changedList.add(
                                        performBestSwap(schedule, schedule.getHolidaySwaps(day, (HolidayShift) shift))
                                );
                            }
                            break;
                    }
                }
            }
            changed = changedList.contains(true);
        }

    }

    private boolean performBestSwap(Schedule schedule, List<Swap> swaps) {
        boolean changed = false;
        if (swaps.size() > 0) {
            double originalFairness = schedule.fairnessScore();
            Swap bestSwap = null;
            for (Swap swap : swaps) {
                if (swap.getFairnessScore() < originalFairness) {
                    bestSwap = swap;
                    originalFairness = swap.getFairnessScore();
                }
            }
            if (bestSwap != null) {
                try {
                    schedule.performSwap(bestSwap);
                    System.out.println("swapped");
                    changed = true;
                } catch (InvalidDayException e) {
                    e.printStackTrace();
                }
            }
        }
        return changed;
    }

    private Schedule initialSchedule() {
        Schedule schedule = new Schedule(data, parameters);
        completeSchedule(schedule);
        return schedule;
    }

    private void completeSchedule(Schedule schedule) {
        for (Week week : data.getWeeks()) {
            for (Shift shift : schedule.getShifts().values()) {
                switch (shift.getPeriod()) {
                    case WEEK:
                        completeScheduleFor(schedule, week.getDays(), shift);
                        break;
                    case WEEKEND:
                        completeScheduleFor(schedule, week.getWeekendDays(), shift);
                        break;
                    case HOLIDAY:
                        for (Day day : week.getHolidays()) {
                            completeScheduleFor(schedule, Collections.singletonList(day), shift);
                        }
                        break;
                }
            }
        }
    }

    private void completeScheduleFor(Schedule schedule, List<Day> days, Shift shift) {
        List<Assistant> invalidAssistants = new ArrayList<>();
        while (schedule.nbAssignmentsOfShiftTypeOn(days.get(0), shift.getType()) < shift.getCoverage(days.get(0))) {
            Assistant assistant = randomAssistantForShift(invalidAssistants, shift);
            try {
                schedule.addAssignmentOn(assistant, days, shift);
            } catch (InvalidDayException e) {
                invalidAssistants.add(assistant);
            } catch (InvalidShiftTypeException e) {
                e.printStackTrace();
            }
        }
    }

    private Assistant randomAssistantForShift(List<Assistant> excludedAssistants, Shift shift) {
        List<Assistant> allowedAssistants = data.getAssistants()
                .stream()
                .filter(assistant -> !excludedAssistants.contains(assistant))
                .filter(assistant -> shift.getAllowedAssistantTypes().contains(assistant.getType()))
                .collect(Collectors.toList());
        Random random = new Random();
        return allowedAssistants.get(random.nextInt(allowedAssistants.size()));
    }
}
