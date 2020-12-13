package com.scheduler.time;

import java.util.List;

public class Week {

    private final int weekNumber;
    private final List<Day> days;

    public Week(int weekNumber, List<Day> days) {
        this.weekNumber = weekNumber;
        this.days = days;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public List<Day> getDays() {
        return days;
    }
}
