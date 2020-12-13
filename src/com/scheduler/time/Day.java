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
        return this.date.getDay() == 6 || this.date.getDay() == 0;
    }

    public boolean isHoliday() {
        // TODO
        return false;
    }

    public String getDay0fWeek() {
        return switch (this.date.getDay()) {
            case 1 -> "Mon";
            case 2 -> "Tue";
            case 3 -> "Wed";
            case 4 -> "Thu";
            case 5 -> "Fri";
            case 6 -> "Sat";
            case 0 -> "Sun";
            default -> throw new IllegalStateException("Unexpected value: " + this.date.getDay());
        };
    }
}
