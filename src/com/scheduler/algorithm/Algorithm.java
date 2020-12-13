package com.scheduler.algorithm;

import com.scheduler.schedule.Schedule;

public class Algorithm {

    public Schedule generateSchedule(AlgorithmInput input) {
        Schedule initialSchedule = initialSchedule(input);
        return initialSchedule;
    }

    private Schedule initialSchedule(AlgorithmInput input) {
        Schedule schedule = new Schedule(input.getAssistants(), input.getWeeks(), input.getShiftTypes());
        return schedule;
    }
}
