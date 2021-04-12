package input.time;

import java.util.Objects;

public class Day {

    private final int id;
    private final boolean isHoliday;

    public Day(int id, boolean isHoliday) {

        this.id = id;
        this.isHoliday = isHoliday;
    }

    public boolean isWeekend() {
        return this.id % 7 == 2 || this.id % 7 == 3;
    }

    public boolean isHoliday() {
        return this.isHoliday;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Day day = (Day) o;
        return id == day.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}