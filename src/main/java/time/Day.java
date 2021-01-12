package time;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;

public class Day {

    private final LocalDate date;

    public Day(int year, int month, int day) {
        date = LocalDate.of(year, month, day);
    }

    public boolean isWeekend() {
        return this.date.getDayOfWeek() == DayOfWeek.SATURDAY
                || this.date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    public boolean isHoliday() {
        // TODO
        return false;
    }

    public String getDay0fWeek() {
        return date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Day day = (Day) o;
        return date.equals(day.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }
}
