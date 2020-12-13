package com.scheduler.time;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<Day> getWeekendDays() {
        return this.days.stream().filter(Day::isWeekend).collect(Collectors.toList());
    }

    public List<Day> getHolidays() {
        return this.days.stream().filter(Day::isHoliday).collect(Collectors.toList());
    }
}
