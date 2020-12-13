package com.scheduler.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Day {

    private Date date;

    public Day(int year, int month, int day) throws ParseException {
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");

        String builder = "%s-%s-%s".formatted(
                String.format("%1$4s", year),
                String.format("%1$2s", month),
                String.format("%1$2s", day)
        );
        date = ft.parse(builder);
    }

    public boolean isWeekend() {
        // TODO
        return this.date.getDay() == 6 || this.date.getDay() == 7;
    }

    public boolean isHoliday() {
        // TODO
        return false;
    }

    public int getDay0fWeek() {
        return this.date.getDay();
    }
}
