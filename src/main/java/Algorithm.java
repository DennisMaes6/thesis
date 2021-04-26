import exceptions.InvalidDayException;
import exceptions.InvalidShiftTypeException;
import exceptions.NoSuitableAssistantException;
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
    public Schedule generateSchedule() throws NoSuitableAssistantException {
        Schedule schedule = initialSchedule();
        optimizeSchedule(schedule);
        initJaev(schedule);
        optimizeJaev(schedule);
        return schedule;
    }

    private void optimizeJaev(Schedule schedule) {
        boolean changed = true;
        while (changed) {
            List<Boolean> changedList = new ArrayList<>();
            List<Day> weekdays = data.getDays().stream()
                    .filter(d -> !d.isWeekend() && !d.isHoliday())
                    .collect(Collectors.toList());
            for (Day day : weekdays) {
                changedList.add(
                        performBestSwapJaev(schedule, schedule.getJaevSwaps(day))
                );
            }
            changed = changedList.contains(true);
        }
    }

    private void optimizeSchedule(Schedule schedule) {
        boolean changed = true;
        while (changed) {
            List<Boolean> changedList = new ArrayList<>();
            for (Week week : data.getWeeks()) {
                for (Shift shift : schedule.getShifts().values()) {
                    switch (shift.getPeriod()) {
                        case WEEK -> changedList.add(
                                    performBestSwap(schedule, schedule.getWeekSwaps(week, (WeekShift) shift))
                            );
                        case WEEKEND ->
                            changedList.add(
                                    performBestSwap(schedule, schedule.getWeekendSwaps(week, (WeekendShift) shift))
                            );
                        case HOLIDAY -> {
                            for (Day day : week.getHolidays())
                                changedList.add(
                                        performBestSwap(schedule, schedule.getHolidaySwaps(day, (HolidayShift) shift))
                                );
                        }
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
                    changed = true;
                } catch (InvalidDayException | InvalidShiftTypeException e) {
                    e.printStackTrace();
                }
            }
        }
        return changed;
    }

    private boolean performBestSwapJaev(Schedule schedule, List<JaevSwap> swaps) {
        boolean changed = false;
        if (swaps.size() > 0) {
            double originalFairness = schedule.jaevFairnessScore();
            JaevSwap bestSwap = null;
            for (JaevSwap swap : swaps) {
                if (swap.getJaevFairnessScore() < originalFairness) {
                    bestSwap = swap;
                    originalFairness = swap.getJaevFairnessScore();
                }
            }
            if (bestSwap != null) {
                try {
                    schedule.performJaevSwap(bestSwap);
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
        boolean done = false;
        while (!done) {
            try {
                completeSchedule(schedule);
                done = true;
            } catch (NoSuitableAssistantException e) {
                schedule = new Schedule(data, parameters);
            }
        }
        return schedule;
    }

    private void initJaev(Schedule schedule) {
        boolean done = false;
        while (!done) {
            try {
                completeJaev(schedule);
                done = true;
            } catch (NoSuitableAssistantException e) {
                schedule = new Schedule(data, parameters);
            }
        }
    }

    private void completeJaev(Schedule schedule) throws NoSuitableAssistantException {
        for (Day day: data.getDays()) {
            List<Assistant> invalidAssistants = new ArrayList<>();
            while (schedule.nbAssignmentsOfShiftTypeOn(day, ShiftType.JAEV) < schedule.getJaevShift().getCoverage(day)) {
                Assistant assistant = randomAssistantForShift(invalidAssistants, schedule.getJaevShift());
                try {
                    schedule.addJaevAssignmentOn(assistant, day);
                } catch (InvalidDayException e) {
                    invalidAssistants.add(assistant);
                } catch (InvalidShiftTypeException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void completeSchedule(Schedule schedule) throws NoSuitableAssistantException {
        for (Week week : data.getWeeks()) {
            for (Shift shift : schedule.getShifts().values()) {
                switch (shift.getPeriod()) {
                    case WEEK -> completeScheduleFor(schedule, week.getDays(), shift);
                    case WEEKEND -> completeScheduleFor(schedule, week.getWeekendDays(), shift);
                    case HOLIDAY -> {
                        for (Day day : week.getHolidays())
                                completeScheduleFor(schedule, Collections.singletonList(day), shift);
                    }
                }
            }
        }
    }

    private void completeScheduleFor(Schedule schedule, List<Day> days, Shift shift) throws NoSuitableAssistantException {
        List<Assistant> invalidAssistants = new ArrayList<>();
        while (schedule.nbAssignmentsOfShiftTypeOn(days.get(0), shift.getType()) < shift.getCoverage(days.get(0))) {
            Assistant assistant = randomAssistantForShift(invalidAssistants, shift);
            try {
                schedule.assign(assistant, days, shift);
            } catch (InvalidDayException | InvalidShiftTypeException e) {
                invalidAssistants.add(assistant);
            }
        }
    }

    private Assistant randomAssistantForShift(List<Assistant> excludedAssistants, Shift shift) throws NoSuitableAssistantException {
        List<Assistant> allowedAssistants = data.getAssistants()
                .stream()
                .filter(assistant -> !excludedAssistants.contains(assistant))
                .filter(assistant -> shift.getAllowedAssistantTypes().contains(assistant.getType()))
                .collect(Collectors.toList());
        Random random = new Random();
        if (allowedAssistants.size() == 0) {
            throw new NoSuitableAssistantException("no allowed asssistants left");
        }
        return allowedAssistants.get(random.nextInt(allowedAssistants.size()));

    }
}
