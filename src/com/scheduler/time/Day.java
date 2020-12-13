package com.scheduler.time;

import java.util.Date;

public class Day {

    private Date date;
    private Week week;

    public Day(Date date, Week week) {
        this.date = date;
        this.week = week;
    }

    public boolean isWeekend() {
        // TODO: implement
        return false;
    }

    public boolean isHoliday() {
        // TODO: implement
        return false;
    }


    public Week getWeek() {
        return week;
    }
}
